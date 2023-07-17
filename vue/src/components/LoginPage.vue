<style src="./css/login.css" scoped></style>
<template>
  <body>
  <div class="login-container">
    <el-form ref="form" :model="loginForm" label-width="80px">
      <div class="title-container">
        <h3 class="title">WELCOME TO OWL SYSTEM</h3>
      </div>
      <el-form-item prop="username">
        <!-- <span class="svg-container">
            <svg-icon icon-class="user" />
        </span> -->

        <el-input
            ref="username"
            v-model="loginForm.mail"
            placeholder="用户名"
            name="username"
            type="text"
            tabindex="1"
            auto-complete="on"
        />
      </el-form-item>
      <el-form-item prop="password">
        <!-- <span class="svg-container">
            <svg-icon icon-class="password" />
        </span> -->
        <el-input
            :key="passwordType"
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="密码"
            name="password"
            tabindex="2"
            auto-complete="on"
            @keyup.enter.native="handleLogin"
        />
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">登录</el-button>
      <el-button type="text" @click="showRegisterDialog" style="width:94%;margin-bottom:30px; color: #ffffff">注册</el-button>
      <el-dialog title="注册" :visible.sync="registerDialogVisible">
        <el-form ref="registerForm" :model="registerForm" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="registerForm.mail" placeholder="请输入邮箱"></el-input>
          </el-form-item>
          <el-form-item label="验证码" prop="verificationCode">
            <el-row>
              <el-col :span="18">
                <el-input v-model="registerForm.code" placeholder="请输入验证码" :disabled="isSendingCode"></el-input>
              </el-col>
              <el-col :span="6" style="padding-left: 10px;">
                <el-button type="primary" @click="sendCode" style="width: 100%;">发送验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="registerForm.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="confirmPassword" placeholder="请再次输入密码"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="resetRegisterForm">重置</el-button>
          <el-button type="primary" @click="register">注册</el-button>
        </div>
      </el-dialog>
    </el-form>
  </div>
  </body>
</template>

<script>
import axios from 'axios'
import global from './GlobalPage.vue'
const restweburl = global.ip;
export default {
  data() {

    //message: 'Hello Vue.js!'
    return{
      loginForm: {
        mail: '1985136419@qq.com',
        password: '123456'
      },
      registerForm: {
        username: '',
        mail: '',
        password: '',
        code: ''
      },
      confirmPassword:'',
      registerDialogVisible:false,
      ac:"",
      pw:"",
      loading:false,
      isSendingCode:false
      //     loginRules: {
      //         username: [{ required: true, trigger: 'blur', validator: validateUsername }],
      //         password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      //     },
      // loading: false,
      // passwordType: 'password',
      // redirect: undefined
    }
  },
  methods: {
    handleLogin: function () {
      // 获取message变量值将其转换成数组，逆转后再链接，再赋值给message
      // alert("登录");
      // var a = this.ac;
      // var p = this.pw;
      // alert(a);
      // alert(p);
      this.loading=true;
      var params = new URLSearchParams();
      params.append('mail',this.loginForm.mail);
      params.append('password',this.loginForm.password);

      axios.post(restweburl+'login', params).then(response => {
        console.log(response.data)
        // var obj = JSON.parse(response.data);
        // console.log(obj)
        if(response.data.code==1){
          window.sessionStorage.setItem("DBDtoken",response.data.data);
          this.$router.push('/home');
        }
        else{
          alert("密码错误")
        }
      }).catch(error => {
        alert(error)
        alert("请求失败")
      })
    },
    showRegisterDialog:function (){
      this.registerDialogVisible = true;
    },
    sendCode:function (){
      var m = this.registerForm.mail;
      var emreg=/^\w{3,}(\.\w+)*@[A-z0-9]+(\.[A-z]{2,5}){1,2}$/;
      if(emreg.test(m)===false){
        alert("请输入正确的邮箱地址");
        return;
      }
      if(m.length===0){
        alert("请输入正确的邮箱地址");
        return;
      }
      var params = new URLSearchParams();
      params.append("mail",this.registerForm.mail);
      const response = axios.post("http://116.204.11.171:8080/code",params);
      if(response.data.code===1){
        alert("验证码已发送到指定邮箱");
      }else {
        alert("验证码发送失败"+response.data.msg);
      }
    },
    register: function () {
      var m = this.registerForm.mail;
      var n = this.registerForm.name;
      var p = this.registerForm.password;
      var q = this.confirmPassword;
      var code = this.registerForm.code;

      var emreg=/^\w{3,}(\.\w+)*@[A-z0-9]+(\.[A-z]{2,5}){1,2}$/;
      if(p!=q){
        alert("两次密码不一致");
        return;
      }
      if(m==''){
        alert("邮箱地址不能为空");
        return;
      }
      else if(emreg.test(m)==false){
        alert("请输入正确的邮箱地址");
        return;
      }
      if(n==''){
        alert("昵称不能为空");
        return;
      }
      if(p==''){
        alert("密码不能为空");
        return;
      }

      if(p.length<5){
        alert("密码位数至少大于5");
        return;
      }
      if(code.length!=6){
        alert("验证码应为6位");
        return;
      }
      var params = new URLSearchParams();
      params.append("username",this.registerForm.username);
      params.append("mail",this.registerForm.mail);
      params.append("password",this.registerForm.password);
      params.append("code",this.registerForm.code);
      const response = axios.post("http://116.204.11.171:8080/register",params);
      if(response.data.code===1){
        alert("注册成功");
        window.sessionStorage.setItem("DBDtoken",response.data.data);
        this.$router.push('/home');
      }else {
        alert("注册失败"+response.data.msg);
      }
      this.registerDialogVisible = false;

    },
    resetRegisterForm() {
      this.$refs.registerForm.resetFields();
    }
  }
}
</script>
<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      //   -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  background-image: url("../img/back.jpg") ;
  background-size: 100%;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 35px 35px 10px;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
