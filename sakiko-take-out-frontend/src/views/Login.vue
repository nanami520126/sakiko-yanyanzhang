<template>
  <div class="login-container" :style="{ backgroundImage: `url(${bgImage})` }">
    <el-card class="login-card">
      <h2 class="title">缘缘庄餐饮系统</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <div class="show-pwd">
          <el-checkbox v-model="showPassword">显示密码</el-checkbox>
        </div>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/employee'
import { useUserStore } from '@/store/user'
const bgImage = 'https://sakikosama.oss-cn-beijing.aliyuncs.com/si-bg.webp'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const showPassword = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function handleLogin() {
  formRef.value.validate(valid => {
    if (!valid) return
    loading.value = true
    login(form)
      .then(res => {
        userStore.setLoginInfo(res.data)
        ElMessage.success('登录成功')
        router.push('/employee')
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style scoped>
.login-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-card {
  width: 380px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
}

.title {
  text-align: center;
  margin: 0 0 24px;
  color: #303133;
}

.show-pwd {
  text-align: left;
  margin: -8px 0 16px;
}

/* 显示密码复选框：高亮与文字均为浅绿色 */
.show-pwd :deep(.el-checkbox) {
  --el-checkbox-text-color: #90ee90;
  --el-checkbox-checked-text-color: #90ee90;
  --el-checkbox-checked-bg-color: #90ee90;
  --el-checkbox-checked-input-border-color: #90ee90;
  --el-checkbox-checked-icon-color: #303133;
}

.login-btn {
  width: 100%;
  --el-button-bg-color: #90ee90;
  --el-button-text-color: #303133;
  --el-button-border-color: #90ee90;
  --el-button-hover-bg-color: #7fd97f;
  --el-button-hover-text-color: #303133;
  --el-button-hover-border-color: #7fd97f;
  --el-button-active-bg-color: #6fc96f;
  --el-button-active-text-color: #303133;
  --el-button-active-border-color: #6fc96f;
}

/* 用户名/密码标签与输入文字：深黑色 */
.login-card :deep(.el-form-item__label) {
  color: #000000;
}

.login-card :deep(.el-input__inner) {
  color: #000000;
}

/* 输入框：淡蓝色填充整个输入框，上下留出几个像素间隔 */
.login-card :deep(.el-input__wrapper) {
  background-color: light-dark(rgb(232, 240, 254), rgba(70, 90, 126, 0.4));
  padding-top: 6px;
  padding-bottom: 6px;
}

/* 聚焦时：淡绿色边框（加粗） */
.login-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #90ee90 inset;
}
</style>
