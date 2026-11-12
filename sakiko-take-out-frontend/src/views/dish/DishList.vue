<template>
  <div class="dish-container">
    <!-- 搜索区 -->
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="菜品名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入菜品名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.categoryId" placeholder="全部分类" clearable class="w160">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="售卖状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable class="w140">
            <el-option label="起售" :value="1" />
            <el-option label="停售" :value="0" />
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
        <el-button type="primary" @click="openAdd">新增菜品</el-button>
        <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
        <el-button type="success" @click="handleBatchStatus(1)">批量起售</el-button>
        <el-button type="warning" @click="handleBatchStatus(0)">批量停售</el-button>
      </div>

      <el-table :data="records" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="name" label="菜品名称" min-width="140" />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              fit="cover"
              class="dish-img"
              :preview-src-list="[row.image]"
              preview-teleported
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="售价" width="100">
          <template #default="{ row }">￥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="售卖状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '起售' : '停售' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="最后操作时间" min-width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">修改</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleStatus(row)"
            >
              {{ row.status === 1 ? '停售' : '起售' }}
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" class="w-full">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.5" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-upload :show-file-list="false" :before-upload="beforeUpload" accept="image/*">
            <img v-if="form.image" :src="form.image" class="avatar" />
            <div v-else class="avatar-uploader">
              <el-icon><Plus /></el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入菜品描述" />
        </el-form-item>
        <el-form-item label="口味">
          <div class="flavors">
            <div v-for="(f, idx) in form.flavors" :key="idx" class="flavor-row">
              <el-input v-model="f.name" placeholder="口味名称，如：辣度" class="flavor-name" />
              <el-input v-model="f.value" placeholder="口味值，逗号分隔，如：不辣,微辣,中辣" class="flavor-value" />
              <el-button type="danger" link @click="removeFlavor(idx)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addFlavor">+ 添加口味</el-button>
          </div>
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
  addDish,
  updateDish,
  deleteDish,
  getDishById,
  setDishStatus
} from '@/api/dish'
import { listCategory } from '@/api/category'
import { uploadImage } from '@/api/common'

const loading = ref(false)
const total = ref(0)
const records = ref([])
const categoryList = ref([])
const selection = ref([])

const queryParams = reactive({
  name: '',
  categoryId: null,
  status: null,
  page: 1,
  pageSize: 10
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品')
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  price: null,
  image: '',
  description: '',
  flavors: []
})

const rules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  image: [{ required: true, message: '请上传图片', trigger: 'change' }]
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
  queryParams.categoryId = null
  queryParams.status = null
  queryParams.page = 1
  handleQuery()
}

function loadCategories() {
  listCategory(1).then(res => {
    categoryList.value = res.data || []
  })
}

function handleSelectionChange(rows) {
  selection.value = rows
}

function openAdd() {
  dialogTitle.value = '新增菜品'
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: null,
    price: null,
    image: '',
    description: '',
    flavors: []
  })
  dialogVisible.value = true
}

function openEdit(row) {
  dialogTitle.value = '修改菜品'
  getDishById(row.id).then(res => {
    const d = res.data
    Object.assign(form, {
      id: d.id,
      name: d.name,
      categoryId: d.categoryId,
      price: d.price,
      image: d.image,
      description: d.description,
      flavors: (d.flavors || []).map(f => ({ name: f.name, value: f.value }))
    })
    dialogVisible.value = true
  })
}

function buildSubmitData() {
  return {
    id: form.id,
    name: form.name,
    categoryId: form.categoryId,
    price: form.price,
    image: form.image,
    description: form.description,
    flavors: form.flavors.filter(f => f.name && f.name.trim())
  }
}

function handleSubmit() {
  formRef.value.validate(valid => {
    if (!valid) return
    const data = buildSubmitData()
    const api = form.id ? updateDish : addDish
    api(data).then(() => {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      handleQuery()
    })
  })
}

function handleStatus(row) {
  const action = row.status === 1 ? '停售' : '起售'
  ElMessageBox.confirm(`确认${action}菜品「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(() => setDishStatus(row.status === 1 ? 0 : 1, row.id))
    .then(() => {
      ElMessage.success(`${action}成功`)
      handleQuery()
    })
    .catch(() => {})
}

function handleDelete(row) {
  ElMessageBox.confirm(`确认删除菜品「${row.name}」吗？`, '提示', { type: 'warning' })
    .then(() => deleteDish(row.id))
    .then(() => {
      ElMessage.success('删除成功')
      handleQuery()
    })
    .catch(() => {})
}

function handleBatchDelete() {
  if (!selection.value.length) {
    ElMessage.warning('请先选择菜品')
    return
  }
  const ids = selection.value.map(row => row.id).join(',')
  ElMessageBox.confirm('确认删除选中的菜品吗？', '提示', { type: 'warning' })
    .then(() => deleteDish(ids))
    .then(() => {
      ElMessage.success('删除成功')
      handleQuery()
    })
    .catch(() => {})
}

function handleBatchStatus(status) {
  if (!selection.value.length) {
    ElMessage.warning('请先选择菜品')
    return
  }
  const action = status === 1 ? '起售' : '停售'
  ElMessageBox.confirm(`确认批量${action}选中的菜品吗？`, '提示', { type: 'warning' })
    .then(() => Promise.all(selection.value.map(row => setDishStatus(status, row.id))))
    .then(() => {
      ElMessage.success(`批量${action}成功`)
      handleQuery()
    })
    .catch(() => {})
}

function beforeUpload(file) {
  uploadImage(file)
    .then(res => {
      form.image = res.data
      ElMessage.success('图片上传成功')
    })
  return false // 阻止 el-upload 自动上传，改用上面的 axios 手动上传
}

function addFlavor() {
  form.flavors.push({ name: '', value: '' })
}

function removeFlavor(idx) {
  form.flavors.splice(idx, 1)
}

onMounted(() => {
  handleQuery()
  loadCategories()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.w160 {
  width: 160px;
}

.w140 {
  width: 140px;
}

.w-full {
  width: 100%;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.dish-img {
  width: 56px;
  height: 56px;
  border-radius: 4px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 28px;
  cursor: pointer;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.flavors {
  width: 100%;
}

.flavor-row {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}

.flavor-name {
  width: 180px;
}

.flavor-value {
  flex: 1;
}
</style>
