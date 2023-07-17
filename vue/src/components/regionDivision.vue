<template>
  <div>
    <el-card style="margin-bottom: 40px;">
      划分区域
      <span :style="{ marginRight: '10px' }">摄像头：</span>
      <el-select v-model="value" filterable placeholder="请选择摄像头" :style="{ marginRight: '20px' }" @change="handleCameraChange">
        <el-option
            v-for="item in options"
            :key="item.cid"
            :label="item.cid"
            :value="item.cid"
        ></el-option>
      </el-select>
      <el-button type="primary" :plain="true" @click="startDrawing">开始绘制</el-button>
      <el-button type="primary" @click="clearDrawing">清空绘制</el-button>

      <el-button type="primary" @click="submitPoints">确认绘制</el-button>

    </el-card>

    <el-card>
      <div class="card-container">
        <el-card class="card">
          <div class="card-content">
            <!--            <img ref="image" :src="'http://116.204.11.171:8080/images/1.png'" class="card-image">-->
            <img ref="image" :src="image" class="card-image">

          </div>
        </el-card>
      </div>
    </el-card>
    <canvas ref="canvas"
            :style="{ position: imageLoaded ? 'absolute' : 'initial' }"
            @mousedown="handleMouseDown"
            @mousemove="handleMouseMove"
            @mouseup="handleMouseUp">
    </canvas>

  </div>
</template>

<script>
import axios from "axios";
import global from "./GlobalPage.vue";
const restweburl = global.ip;

export default {
  name: "regionDivision",
  data() {
    return {
      isStyleChanged: false,
      options: [],
      value: "",
      image:"",
    // image: "http://116.204.11.171:8080/images/1.png", // 图片的URL
      //image: require("http://116.204.11.171:8080/images/1.png"),
      canvas: null,
      ctx: null,
      points: [],
      dragging: false,
      imageLoaded: false, // 新增的属性

      imageWidth:'',
      imageHeight:'',

    };
  },
  mounted() {
    this.canvas = this.$refs.canvas;
    this.ctx = this.canvas.getContext("2d");
    const image = new Image();
    image.src = this.image;

      this.canvas.width = image.width;
      this.canvas.height = image.height;
      const imageRect = this.$refs.image.getBoundingClientRect();
      this.canvas.style.top = imageRect.top + "px";
      this.canvas.style.left = imageRect.left + "px";

      const imageWidth = imageRect.width;
      const imageHeight = imageRect.height;
      this.canvas.width = imageWidth;
      this.canvas.height = imageHeight;
      this.imageHeight=imageHeight;
      this.imageWidth=imageWidth;
      console.log(imageRect.width);
      console.log(imageRect.height);
     // this.imageLoaded = true; // 设置图像已加载完成

  },
  methods:{
    handleCameraChange() {
      // 向后端发送请求并包含选择的摄像头值
      axios.get(restweburl + "getImg", {
        params: {
          cID: this.value,
        }
      })
          .then((res) => {

            console.log(res.data.data);
           const imageUrl = "http://"+res.data.data; // 获取返回的图片 URL
          // this.image = "http://116.204.11.171:8080/images/1.png"; // 更新图片 URL
            this.image =imageUrl;

          })
          .catch(function (error) {
            console.log(error);
          });

      //
      // this.canvas = this.$refs.canvas;
      // this.ctx = this.canvas.getContext("2d");
      // const image = new Image();
      // image.src = this.image;
      // this.canvas.width = image.width;
      // this.canvas.height = image.height;
      // const imageRect = this.$refs.image.getBoundingClientRect();
      // this.canvas.style.top = imageRect.top + "px";
      // this.canvas.style.left = imageRect.left + "px";
      //
      // const imageWidth = imageRect.width;
      // const imageHeight = imageRect.height;
      // this.canvas.width = imageWidth;
      // this.canvas.height = imageHeight;
      // console.log("imageRect");
      // console.log(imageRect.width);
      // console.log(imageRect.height);
      // this.imageLoaded = true; // 设置图像已加载完成




    },
    startDrawing(){

      this.isStyleChanged = true;
      this.canvas = this.$refs.canvas;
      this.ctx = this.canvas.getContext("2d");
      const image = new Image();
      image.src = this.image;
      this.canvas.width = image.width;
      this.canvas.height = image.height;
      const imageRect = this.$refs.image.getBoundingClientRect();
      this.canvas.style.top = imageRect.top + "px";
      this.canvas.style.left = imageRect.left + "px";

      const imageWidth = imageRect.width;
      const imageHeight = imageRect.height;
      this.canvas.width = imageWidth;
      this.canvas.height = imageHeight;

      this.imageHeight=imageHeight;
      this.imageWidth=imageWidth;
      console.log("imageRect");
      console.log(imageRect.width);
      console.log(imageRect.height);
      this.imageLoaded = true; // 设置图像已加载完成

      this.$message({
        message: '请开始绘制！可点击图片任意四个点绘制四边形！',
        type: 'success'
      });
    },
    submitPoints(){

      console.log(this.points);
      console.log('x0:', this.points[0].x);
      console.log('y0:', this.points[0].y);
      console.log('x1:', this.points[1].x);
      console.log('y1:', this.points[1].y);
      console.log('x2:', this.points[2].x);
      console.log('y2:', this.points[2].y);
      console.log('x3:', this.points[3].x);
      console.log('y3:', this.points[3].y);


      var x0 = (parseFloat(this.points[0].x) / this.imageWidth).toFixed(3);
      var y0 = (parseFloat(this.points[0].y) / this.imageHeight).toFixed(3);
      var x1 = (parseFloat(this.points[1].x) / this.imageWidth).toFixed(3);
      var y1 = (parseFloat(this.points[1].y) / this.imageHeight).toFixed(3);
      var x2 = (parseFloat(this.points[2].x )/ this.imageWidth).toFixed(3);
      var y2 = (parseFloat(this.points[2].y) / this.imageHeight).toFixed(3);
      var x3 = (parseFloat(this.points[3].x )/ this.imageWidth).toFixed(3);
      var y3 = (parseFloat(this.points[3].y) / this.imageHeight).toFixed(3);

      console.log(x0);
      const string=x0+','+y0+','+x1+','+y1+','+x2+','+y2+','+x3+','+y3;
      console.log(string);
      // 发送添加摄像头的请求
      var params = new URLSearchParams();
      params.append("cID",this.value);
      params.append("zone",string);
      axios.post('http://116.204.11.171:8080/addZone', params)
          .then((response) => {
            if (response.data.code === 1) {
              console.log("成功");
              this.$message({
                message: '提交成功！',
                type: 'success'
              });
            } else {
              // 添加失败的处理
              alert( response.data.msg);
            }
          })
          .catch((error) => {
            console.error(error);
            // 错误的处理
          });
    },
    handleMouseDown(event) {
      const rect = this.canvas.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;
      const point = { x, y };

      console.log('Point:', point); // 打印坐标
      this.points.push(point);
      console.log(this.points);
      this.drawPoints();
      this.dragging = true;
    },
    handleMouseMove(event) {
      if (this.dragging) {
        const rect = this.canvas.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const y = event.clientY - rect.top;
        const point = { x, y };
        this.points[this.points.length - 1] = point;
        this.drawPoints();
      }
    },
    handleMouseUp() {
      this.dragging = false;
    },
    drawPoints() {
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      this.ctx.strokeStyle = "blue";
      this.ctx.lineWidth = 2;
      this.ctx.beginPath();
      for (let i = 0; i < this.points.length; i++) {
        const point = this.points[i];
        if (i === 0) {
          this.ctx.moveTo(point.x, point.y);
        } else {
          this.ctx.lineTo(point.x, point.y);
        }
      }
      if (this.points.length >= 3) {
        this.ctx.closePath();
        this.ctx.fillStyle = "rgba(0, 0, 255, 0.5)";
        this.ctx.fill();
      }
      this.ctx.stroke();
    },

    clearDrawing() {
      this.points = [];
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    },
  },
  created() {
    axios
        .get(restweburl + "cameras")
        .then((res) => {
          this.options = res.data.data;
          this.loading = false;
        })
        .catch(function (error) {
          console.log(error);
        });
  },
};
</script>

<style scoped>
.card-container {
  display: flex;
  justify-content: center;
}

.card {
  display: flex;
  align-items: center;
}

.card-content {
  flex: 1;
  text-align: center;
}

.card-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

canvas {
  border: 1px solid black;
}
</style>
