<template>
  <div class="category-container">
    <!-- 搜索区 -->
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="分类名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入分类名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="分类类型">
          <el-select v-model="queryParams.type" placeholder="全部分类" clearable class="type-select">
            <el-option label="菜品分类" :value="1" />
            <el-option label="套餐分类" :value="2" />
          </el-select>
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
        <el-button type="primary" @click="openAdd">新增分类</el-button>
      </div>

      <el-table :data="records" v-loading="loading" border stripe>
        <el-table-column prop="name" label="分类名称" min-width="160" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            {{ row.type === 1 ? '菜品分类' : '套餐分类' }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
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
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
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
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">菜品分类</el-radio>
            <el-radio :value="2">套餐分类</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
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
  addCategory,
  updateCategory,
  deleteCategory,
  setCategoryStatus
} from '@/api/category'

const loading = ref(false)
const total = ref(0)
const records = ref([])

const queryParams = reactive({
  name: '',
  type: null,
  page: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  type: 1,
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择分类类型', trigger: 'change' }]
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
  queryParams.type = null
  queryParams.page = 1
  handleQuery()
}

function openAdd() {
  dialogTitle.value = '新增分类'
  Object.assign(form, { id: null, name: '', type: 1, sort: 0 })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '编辑分类'
  Object.assign(form, { id: row.id, name: row.name, type: row.type, sort: row.sort })
  dialogVisible.value = true
}

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    const data = { ...form }
    const api = form.id ? updateCategory : addCategory
    api(data).then(() => {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      handleQuery()
    })
  })
}

function handleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确认${action}分类「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(() => setCategoryStatus(row.status === 1 ? 0 : 1, row.id))
    .then(() => {
      ElMessage.success(`${action}成功`)
      handleQuery()
    })
    .catch(() => {})
}

function handleDelete(row) {
  ElMessageBox.confirm(`确认删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(() => deleteCategory(row.id))
    .then(() => {
      ElMessage.success('删除成功')
      handleQuery()
    })
    .catch(() => {})
}

onMounted(handleQuery)
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.type-select {
  width: 160px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
