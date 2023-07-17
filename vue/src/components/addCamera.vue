<template>
  <div>
    <el-card>
      <el-form :model="cameraData" label-width="100px">
        <el-form-item label="摄像头地址">
          <el-input v-model="cameraData.addr"></el-input>
        </el-form-item>
        <el-form-item label="摄像头描述">
          <el-input v-model="cameraData.content"></el-input>
        </el-form-item>
        <el-form-item label="获取地址">

        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addCamera">添加摄像头</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      cameraData: {
        addr: '',
        content: '',
      },
    };
  },
  methods: {
    addCamera() {
      // 发送添加摄像头的请求
      var params = new URLSearchParams();
      params.append("addr",this.cameraData.addr);
      params.append("content",this.cameraData.content);
      axios.post('http://116.204.11.171:8080/addCamera', params)
          .then((response) => {
            if (response.data.code === 1) {
              // 添加成功的处理
              alert('添加摄像头成功');
              this.$router.push("/cameraList");
            } else {
              // 添加失败的处理
              alert('添加摄像头失败：' + response.data.msg);
            }
          })
          .catch((error) => {
            console.error(error);
            // 错误的处理
          });
    },
  },
};
</script>

<style scoped>

</style>
