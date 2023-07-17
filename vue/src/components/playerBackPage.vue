<template>
    <div>
        <el-header>
            <el-page-header @back="goBack" content="详情页面">
            </el-page-header>
        </el-header>
    <div>
        <video 
        id="video"
        :src=this.videoUrl
        v-bind:currentTime="currentTime"
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
            videoUrl: null,
            currentTime: 10
        }
    },
    created() {

        axios.get(restweburl + "jumpPlayback", {params:{'sID':this.$route.params.sid}})
        .then((res) => {
            this.videoUrl = res.data.data.url;
            var i = new Number(res.data.data.time)
            this.currentTime =  i;

            var video = document.getElementById('video')
            video.currentTime = this.currentTime;
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