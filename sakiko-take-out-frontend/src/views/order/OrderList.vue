<template>
  <div class="order-container">
    <!-- 状态统计 -->
    <el-card class="stat-card">
      <div class="stat-list">
        <div class="stat-item" @click="filterByStatus(2)">
          <div class="stat-num">{{ stats.toBeConfirmed ?? 0 }}</div>
          <div class="stat-label">待接单</div>
        </div>
        <div class="stat-item" @click="filterByStatus(3)">
          <div class="stat-num">{{ stats.confirmed ?? 0 }}</div>
          <div class="stat-label">待派送</div>
        </div>
        <div class="stat-item" @click="filterByStatus(4)">
          <div class="stat-num">{{ stats.deliveryInProgress ?? 0 }}</div>
          <div class="stat-label">派送中</div>
        </div>
        <div class="stat-item" @click="filterByStatus(null)">
          <div class="stat-num">{{ total }}</div>
          <div class="stat-label">全部订单</div>
        </div>
      </div>
    </el-card>

    <!-- 搜索区 -->
    <el-card class="search-card">
      <el-form inline>
        <el-form-item label="订单号">
          <el-input v-model="queryParams.number" placeholder="请输入订单号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable class="w140">
            <el-option v-for="(s, k) in statusMap" :key="k" :label="s.text" :value="Number(k)" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="date-range"
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
      <el-table :data="records" v-loading="loading" border stripe>
        <el-table-column prop="number" label="订单号" min-width="180" show-overflow-tooltip />
        <el-table-column label="收货信息" min-width="180">
          <template #default="{ row }">
            <div>{{ row.consignee }}　{{ row.phone }}</div>
            <div class="sub-text">{{ row.address }}</div>
          </template>
        </el-table-column>
        <el-table-column label="菜品" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ formatDishes(row.orderDishes) }}</template>
        </el-table-column>
        <el-table-column label="金额" width="100" align="right">
          <template #default="{ row }">￥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column label="支付方式" width="90">
          <template #default="{ row }">{{ payMethodMap[row.payMethod] || '-' }}</template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type">{{ statusMap[row.status]?.text || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" min-width="165" />
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 2" size="small" type="primary" link @click="handleConfirm(row)">接单</el-button>
            <el-button v-if="row.status === 2" size="small" type="danger" link @click="openRejection(row)">拒单</el-button>
            <el-button v-if="row.status === 4" size="small" type="success" link @click="handleComplete(row)">完成</el-button>
            <el-button v-if="[1, 2, 3, 4].includes(row.status)" size="small" type="danger" link @click="openCancel(row)">取消</el-button>
            <el-button size="small" type="primary" link @click="openDetail(row)">详情</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="680px" top="6vh">
      <template v-if="detail">
        <el-descriptions :column="2" border class="mb16">
          <el-descriptions-item label="订单号" :span="2">{{ detail.number }}</el-descriptions-item>
          <el-descriptions-item label="订单状态" :span="2">
            <el-tag :type="statusMap[detail.status]?.type">{{ statusMap[detail.status]?.text || detail.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">{{ detail.consignee }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ detail.address }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ detail.orderTime }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ payMethodMap[detail.payMethod] || '-' }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">{{ payStatusMap[detail.payStatus] || '-' }}</el-descriptions-item>
          <el-descriptions-item label="打包费">￥{{ detail.packAmount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="实付金额" :span="2">￥{{ detail.amount }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.remark" label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.rejectionReason" label="拒单原因" :span="2">{{ detail.rejectionReason }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.cancelReason" label="取消原因" :span="2">{{ detail.cancelReason }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">商品明细</div>
        <el-table :data="detail.orderDetailList || []" border size="small">
          <el-table-column prop="name" label="名称" min-width="160" />
          <el-table-column label="口味" min-width="130">
            <template #default="{ row }">{{ row.dishFlavor || '-' }}</template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ row }">￥{{ row.amount }}</template>
          </el-table-column>
          <el-table-column prop="number" label="数量" width="80" />
        </el-table>
      </template>
    </el-dialog>

    <!-- 拒单对话框 -->
    <el-dialog v-model="rejectionVisible" title="拒单" width="420px">
      <el-form label-width="80px">
        <el-form-item label="拒单原因">
          <el-input v-model="rejectionForm.rejectionReason" type="textarea" :rows="3" placeholder="请输入拒单原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectionVisible = false">取消</el-button>
        <el-button type="danger" @click="handleRejection">确定拒单</el-button>
      </template>
    </el-dialog>

    <!-- 取消订单对话框 -->
    <el-dialog v-model="cancelVisible" title="取消订单" width="420px">
      <el-form label-width="80px">
        <el-form-item label="取消原因">
          <el-input v-model="cancelForm.cancelReason" type="textarea" :rows="3" placeholder="请输入取消原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelVisible = false">取消</el-button>
        <el-button type="danger" @click="handleCancel">确定取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageQuery,
  orderStatistics,
  getOrderDetail,
  confirmOrder,
  rejectionOrder,
  cancelOrder,
  completeOrder
} from '@/api/order'

const statusMap = {
  1: { text: '待付款', type: 'warning' },
  2: { text: '待接单', type: 'info' },
  3: { text: '已接单', type: 'primary' },
  4: { text: '派送中', type: 'primary' },
  5: { text: '已完成', type: 'success' },
  6: { text: '已取消', type: 'danger' }
}
const payMethodMap = { 1: '微信', 2: '支付宝' }
const payStatusMap = { 0: '未支付', 1: '已支付', 2: '退款' }

const loading = ref(false)
const total = ref(0)
const records = ref([])
const stats = reactive({ toBeConfirmed: 0, confirmed: 0, deliveryInProgress: 0 })
const timeRange = ref([])

const queryParams = reactive({
  number: '',
  phone: '',
  status: null,
  page: 1,
  pageSize: 10
})

const detailVisible = ref(false)
const detail = ref(null)

const rejectionVisible = ref(false)
const rejectionForm = reactive({ id: null, rejectionReason: '' })

const cancelVisible = ref(false)
const cancelForm = reactive({ id: null, cancelReason: '' })

function formatDishes(s) {
  if (!s) return '-'
  return s.split(';').filter(Boolean).join('、')
}

function buildParams() {
  return {
    ...queryParams,
    beginTime: timeRange.value && timeRange.value[0] ? timeRange.value[0] : null,
    endTime: timeRange.value && timeRange.value[1] ? timeRange.value[1] : null
  }
}

function handleQuery() {
  loading.value = true
  pageQuery(buildParams())
    .then(res => {
      total.value = res.data.total
      records.value = res.data.records || []
    })
    .finally(() => {
      loading.value = false
    })
}

function handleReset() {
  queryParams.number = ''
  queryParams.phone = ''
  queryParams.status = null
  queryParams.page = 1
  timeRange.value = []
  handleQuery()
}

function filterByStatus(status) {
  queryParams.status = status
  queryParams.page = 1
  handleQuery()
}

function loadStats() {
  orderStatistics().then(res => {
    Object.assign(stats, res.data || {})
  })
}

function refresh() {
  handleQuery()
  loadStats()
}

function handleConfirm(row) {
  ElMessageBox.confirm(`确认接单「${row.number}」吗？`, '提示', { type: 'warning' })
    .then(() => confirmOrder({ id: row.id, status: 3 }))
    .then(() => {
      ElMessage.success('接单成功')
      refresh()
    })
    .catch(() => {})
}

function openRejection(row) {
  rejectionForm.id = row.id
  rejectionForm.rejectionReason = ''
  rejectionVisible.value = true
}

function handleRejection() {
  if (!rejectionForm.rejectionReason.trim()) {
    ElMessage.warning('请输入拒单原因')
    return
  }
  rejectionOrder({ id: rejectionForm.id, rejectionReason: rejectionForm.rejectionReason })
    .then(() => {
      ElMessage.success('拒单成功')
      rejectionVisible.value = false
      refresh()
    })
}

function openCancel(row) {
  cancelForm.id = row.id
  cancelForm.cancelReason = ''
  cancelVisible.value = true
}

function handleCancel() {
  if (!cancelForm.cancelReason.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }
  cancelOrder({ id: cancelForm.id, cancelReason: cancelForm.cancelReason })
    .then(() => {
      ElMessage.success('取消成功')
      cancelVisible.value = false
      refresh()
    })
}

function handleComplete(row) {
  ElMessageBox.confirm(`确认完成订单「${row.number}」吗？`, '提示', { type: 'warning' })
    .then(() => completeOrder(row.id))
    .then(() => {
      ElMessage.success('订单已完成')
      refresh()
    })
    .catch(() => {})
}

function openDetail(row) {
  getOrderDetail(row.id).then(res => {
    detail.value = res.data
    detailVisible.value = true
  })
}

onMounted(() => {
  handleQuery()
  loadStats()
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 16px;
}
.stat-list {
  display: flex;
}
.stat-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
  padding: 10px 0;
  border-radius: 8px;
}
.stat-item:hover {
  background: rgba(144, 238, 144, 0.25);
}
.stat-num {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}
.stat-label {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

.search-card {
  margin-bottom: 16px;
}
.date-range {
  width: 360px;
}
.w140 {
  width: 140px;
}

.sub-text {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.mb16 {
  margin-bottom: 16px;
}
.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
</style>
