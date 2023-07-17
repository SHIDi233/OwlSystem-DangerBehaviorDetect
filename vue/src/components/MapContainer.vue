<template>
  <div id="container"></div>
</template>

<style scoped>
  #container{
    padding:0px;
    margin: 0px;
    width: 100%;
    height: 800px;
  }
</style>

<script>
import AMapLoader from '@amap/amap-jsapi-loader';
import { shallowRef } from '@vue/reactivity'
import axios from "axios";

export default {
  data(){
    return{
      cameras:[],
      markers:[],
    }
  },
  methods:{
    setup(){
      const map = shallowRef(null);
      return{
        map,
      }
    },
    async search(){
        try {
          const response = await axios.get("http://116.204.11.171:8080/getAxises");
          this.cameras = response.data.data;
          this.initMap();
          console.log(this.cameras);
          this.cameraID='';
        }catch (e) {
          console.error(e);
        }
    },
    initMap(){
      console.log("现在的数据是:"+this.cameras);
      AMapLoader.load({
        key:"4355fbccd4a26bf20c3d8e74ad9afd3e",             // 申请好的Web端开发者Key，首次调用 load 时必填
        version:"2.0",      // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins:[''],       // 需要使用的的插件列表，如比例尺'AMap.Scale'等
      }).then((AMap)=>{
        this.map = new AMap.Map("container",{  //设置地图容器id
          viewMode:"3D",    //是否为3D地图模式
          zoom:5,           //初始化地图级别
          center:[116.342802,39.952291], //初始化地图中心点位置
        });
        this.cameras.forEach(cameraInfo=>{
          const title = 'cID:'+cameraInfo.cID+'\nLon:'+cameraInfo.axis.split(',')[1]+'\nLat:'+cameraInfo.axis.split(',')[0]+'\n点击查看直播';
          const marker = new AMap.Marker({
            // position:new AMap.LngLat(parseFloat(116),parseFloat(39)),
            position:new AMap.LngLat(parseFloat(cameraInfo.axis.split(',')[1]),parseFloat(cameraInfo.axis.split(',')[0])),
            title:title,
            map:this.map
          });
          marker.on("click", () => {
            window.open("http://116.204.11.171:8081?cid="+cameraInfo.cID);
          });
          this.markers.push(marker);
        });
        this.map.add(this.markers);
      }).catch(e=>{
        console.log(e);
      })
    },
    addMarker(){

    }
  },
  mounted(){
    //DOM初始化完成进行地图初始化
    this.search();
    // this.initMap();


  },
}
</script>
