<template>
    <div>
        <el-header>
            <el-page-header @back="goBack" content="详情页面">
            </el-page-header>
        </el-header>
    <div>
        <video 
        :src=this.videoUrl
        width="870"
        height="490"
        autoplay
        controls
        >
        </video>
    </div>
    </div>
</template>

<script>
    import axios from 'axios'
    import global from './GlobalPage.vue'
  const restweburl = global.ip;
  export default {
    data(){
        return {
            videoUrl: null
        }
    },
    created() {
        axios.get(restweburl + "getPlayback", {params:{'pID':this.$route.params.pid}})
        .then((res) => {
            this.videoUrl = res.data.data;
        })
        .catch(function (error) {
          console.log(error);
        });
  },
  methods: {
      goBack() {
        this.$router.go(-1)
      }
    }
}
</script>