<template>
  <div class="employee-container">
    <!-- 搜索区 -->
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="员工姓名">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表区 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="openAdd">新增员工</el-button>
        <el-upload :show-file-list="false" accept=".xls,.xlsx" :before-upload="handleImport">
          <el-button type="success">批量导入</el-button>
        </el-upload>
      </div>

      <el-table :data="records" v-loading="loading" border stripe>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="sex" label="性别" width="80" />
        <el-table-column prop="idNumber" label="身份证号" min-width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-sizes="[10, 20, 50]"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增 / 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="form.idNumber" placeholder="身份证号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageQuery,
  addEmployee,
  updateEmployee,
  getEmployeeById,
  setEmployeeStatus,
  importEmployees
} from '@/api/employee'

const loading = ref(false)
const total = ref(0)
const records = ref([])

const queryParams = reactive({
  name: '',
  page: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const formRef = ref()
const form = reactive({
  id: null,
  username: '',
  name: '',
  phone: '',
  sex: '男',
  idNumber: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  idNumber: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ]
}

function handleQuery() {
  loading.value = true
  pageQuery(queryParams)
    .then(res => {
      total.value = res.data.total
      records.value = res.data.records
    })
    .finally(() => {
      loading.value = false
    })
}

function handleReset() {
  queryParams.name = ''
  queryParams.page = 1
  handleQuery()
}

function openAdd() {
  dialogTitle.value = '新增员工'
  Object.assign(form, { id: null, username: '', name: '', phone: '', sex: '男', idNumber: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑员工'
  getEmployeeById(row.id).then(res => {
    const data = res.data
    Object.assign(form, {
      id: data.id,
      username: data.username,
      name: data.name,
      phone: data.phone,
      sex: data.sex,
      idNumber: data.idNumber
    })
    dialogVisible.value = true
  })
}

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    const data = { ...form }
    const api = form.id ? updateEmployee : addEmployee
    api(data).then(() => {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      handleQuery()
    })
  })
}

function handleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确认${action}员工「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(() => setEmployeeStatus(row.status === 1 ? 0 : 1, row.id))
    .then(() => {
      ElMessage.success(`${action}成功`)
      handleQuery()
    })
    .catch(() => {})
}

function handleImport(file) {
  importEmployees(file).then(res => {
    ElMessage.success(`成功导入 ${res.data} 名员工`)
    handleQuery()
  })
  return false // 阻止 el-upload 自动上传，改由上面的 axios 手动上传
}

onMounted(handleQuery)
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
