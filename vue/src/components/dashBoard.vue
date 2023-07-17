<template>
  <div class="dashboard-editor-container">

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <el-row>
            <el-col :span="16"><div class="grid-content bg-purple">
              <img src="../img/smoking.png">
            </div></el-col>
            <el-col :span="8"><div class="grid-content bg-purple-light">
              <span>今日共监测到抽烟</span>
              <span style="font-size: 50px;color: #724d54;"><br>{{smoking}}</span>
              <span>次</span>
            </div></el-col>
          </el-row>
<!--          <raddar-chart />-->
<!--          <h1>22</h1>-->
        </div>
      </el-col>


      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <el-row>
          <el-col :span="16"><div class="grid-content bg-purple">
            <img src="../img/calling.png">
          </div></el-col>
          <el-col :span="8"><div class="grid-content bg-purple-light">
            <span>今日监测到打电话</span>
            <span style="font-size: 50px;color: #486573;"><br>{{call}}</span>
            <span>次</span>
          </div></el-col>
          </el-row>
        </div>
      </el-col>


      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
         <el-row>
          <el-col :span="16"><div class="grid-content bg-purple">
            <img src="../img/down.png">
          </div></el-col>
          <el-col :span="8"><div class="grid-content bg-purple-light">
            <span>今日共监测到摔倒</span>
            <span style="font-size: 50px;color: #486573;"><br>{{down}}</span>
            <span>次</span>
          </div></el-col>
         </el-row>
        </div>
      </el-col>

    </el-row>




<el-card>
        <el-row>

          <el-col :span="12">
            <div class="grid-content bg-purple">
              <div id="myChart" :style="{width: '600px', height: '500px'}"></div>
            </div>
          </el-col>


          <el-col :span="12"><div class="grid-content bg-purple-light"> <div id="myChart2" :style="{width: '600px', height: '500px'}"></div></div></el-col>

        </el-row>

        <el-row>
        <el-col :span="12">
          <div class="grid-content bg-purple"><div id="myChart3" :style="{width: '600px', height: '500px'}"></div></div></el-col>


        <el-col :span="12"><div class="grid-content bg-purple-light"> <div id="myChart4" :style="{width: '600px', height: '500px'}"></div></div></el-col>

        </el-row>
</el-card>



    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}" style="padding-right:8px;margin-bottom:30px;">

      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}" style="margin-bottom:30px;">

      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}" style="margin-bottom:30px;">

      </el-col>
    </el-row>



  </div>
</template>

<script>
import global from './GlobalPage.vue'
//import AMapLoader from '@amap/amap-jsapi-loader';
import dayjs from 'dayjs';
import axios from "axios";
//import global from './GlobalPage.vue'
const rest = global.ip;


export default {
  name: 'dashBoard.vue',

  data() {
    return {

      smoking:'',
      down:'',
      call:'',
      map: null, //初始化 map 对象，
      startDate: new Date(), // 初始化为当前日期
      endDate: new Date(Date.now() - 24 * 60 * 60 * 1000), // 一天前的日期
      weekAgoDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), // 一周前的日期
      monthAgoDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000), // 一个月前的日期
      yearAgoDate: new Date(Date.now() - 365 * 24 * 60 * 60 * 1000), // 一年前的日期

      value:'',
      xDayValue:[],
      yDayValue:[],
      xWeekValue:[],
      yWeekDayValue:[],
      xMonthValue:[],
      yMonthValue:[],
      xYearValue:[],
      yYearValue:[],

    }
  },




  methods: {

    fetchData(){
         this.drawLine("myChart","bar");
         this.drawLineWeek("myChart2","bar");
         this.drawLineMonth("myChart3","bar");
         this.drawLineYear("myChart4","bar");
    },

    // fetchData: async function() {
    //   try {
    //     const response = await axios.get(rest + 'count', {
    //       params: {
    //         // eTime: startDate,
    //         // sTime: endDate
    //       }
    //     });
    //     this.xDayValue = response.data.data.type;
    //     this.yDayValue = response.data.data.cnt;
    //     console.log(response.data.data);
    //   } catch (error) {
    //     console.log(error);
    //   }
    // },

    drawLine(chartID,type){
      console.log("绘制一日图");
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById(chartID));

      myChart.setOption({
        title: { text: '近一日内统计信息' },
        tooltip: {},
        xAxis: {
          data: this.xDayValue,
          axisLabel: {
            interval: 0 // 设置横坐标显示的间隔，这里设置为2表示每隔两个数据显示一个
          }
        },
        yAxis: {},
        series: [{
          name: '数量',
          type: type,
          data: this.yDayValue,
          itemStyle: {
            color: '#81bcd9'// 每个柱子的颜色分别为红、绿、蓝、黄、紫
          }
        }],
        label: {
          show: true,
          position: 'top',
          formatter: '{c}' // 显示具体的数值
        }
      });
    },
    drawLineWeek(chartID,type){
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById(chartID));

      myChart.setOption({
        title: { text: '近一周统计信息' },
        tooltip: {},
        xAxis: {
          data: this.xWeekValue,
          axisLabel: {
            interval: 0 // 设置横坐标显示的间隔，这里设置为2表示每隔两个数据显示一个
          }
        },
        yAxis: {},
        series: [{
          name: '数量',
          type: type,
          data: this.yWeekValue,
          itemStyle: {
            color: '#81bcd9'// 每个柱子的颜色分别为红、绿、蓝、黄、紫
          }
        }],
        label: {
          show: true,
          position: 'top',
          formatter: '{c}' // 显示具体的数值
        }
      });
    },
    drawLineMonth(chartID,type){
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById(chartID));

      myChart.setOption({
        title: { text: '近一月统计信息' },
        tooltip: {},
        xAxis: {
          data: this.xMonthValue,
          axisLabel: {
            interval: 0 // 设置横坐标显示的间隔，这里设置为2表示每隔两个数据显示一个
          }
        },
        yAxis: {},
        series: [{
          name: '数量',
          type: type,
          data: this.yMonthValue,
          itemStyle: {
            color: '#81bcd9'// 每个柱子的颜色分别为红、绿、蓝、黄、紫
          }
        }],
        label: {
          show: true,
          position: 'top',
          formatter: '{c}' // 显示具体的数值
        }
      });
    },
    drawLineYear(chartID,type){
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById(chartID));

      myChart.setOption({
        title: { text: '近一年统计信息' },
        tooltip: {},
        xAxis: {
          data: this.xYearValue,
          axisLabel: {
            interval: 0 // 设置横坐标显示的间隔，这里设置为2表示每隔两个数据显示一个
          }
        },
        yAxis: {},
        series: [{
          name: '数量',
          type: type,
          data: this.yYearValue,
          itemStyle: {
            color: '#81bcd9'// 每个柱子的颜色分别为红、绿、蓝、黄、紫
          }
        }],
        label: {
          show: true,
          position: 'top',
          formatter: '{c}' // 显示具体的数值
        }
      });
    }
  },

  // mounted() {
  //   // this.drawLine("myChart","bar");
  //   // this.drawLineWeek("myChart2","bar");
  //   // this.drawLineMonth("myChart3","bar");
  //   // this.drawLineYear("myChart4","bar");
  //  // this.initMap();
  //
  // },

  created() {

    const startDate = dayjs(this.startDate).format('YYYY-MM-DDTHH:mm:ss');
    const endDate = dayjs(new Date(Date.now() - 24 * 60 * 60 * 1000)).format('YYYY-MM-DDTHH:mm:ss');
    const weekDate = dayjs(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)).format('YYYY-MM-DDTHH:mm:ss');
    const monthDate = dayjs(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)).format('YYYY-MM-DDTHH:mm:ss');
    const yearDate = dayjs(new Date(Date.now() - 365 * 24 * 60 * 60 * 1000)).format('YYYY-MM-DDTHH:mm:ss');

    const fetchData = async () => {
        try {
          const response = await axios.get(rest + 'count', {
            params: {
              eTime: startDate,
              sTime: endDate
            }
          });
          this.xDayValue = response.data.data.type;
          this.yDayValue = response.data.data.cnt;

          const indexcall = response.data.data.type.indexOf("calling");
          if (indexcall!==-1){
            this.call = response.data.data.cnt[indexcall];
          }
          const indexdown = response.data.data.type.indexOf("down");
          if (indexdown!==-1){
            this.down = response.data.data.cnt[indexdown];
          }
          const indexsmoking = response.data.data.type.indexOf("smoking");
          if (indexdown!==-1){
            this.smoking = response.data.data.cnt[indexsmoking];
          }




          console.log(response.data.data);

          this.drawLine("myChart","bar");

          // 在数据赋值完成后再调用函数
          this.fetchData();
        } catch (error) {
          console.log(error);
        }
      };

      fetchData();

    const fetchDataWeek = async () => {
      try {
        const response = await axios.get(rest + 'count', {
          params: {
            eTime: startDate,
            sTime: weekDate
          }
        });
        this.xWeekValue = response.data.data.type;
        this.yWeekValue = response.data.data.cnt;
        console.log(response.data.data);

        this.drawLineWeek("myChart2","bar");


        // 在数据赋值完成后再调用函数
        this.fetchData();
      } catch (error) {
        console.log(error);
      }
    };

    fetchDataWeek();


    const fetchDataMonth = async () => {
      try {
        const response = await axios.get(rest + 'count', {
          params: {
            eTime: startDate,
            sTime: monthDate
          }
        });
        this.xMonthValue = response.data.data.type;
        this.yMonthValue = response.data.data.cnt;
        console.log(response.data.data);

        this.drawLineMonth("myChart3","bar");
        //this.drawLineYear("myChart4","bar");


        // 在数据赋值完成后再调用函数
        this.fetchData();
      } catch (error) {
        console.log(error);
      }
    };

    fetchDataMonth();


    const fetchDataYear = async () => {
      try {
        const response = await axios.get(rest + 'count', {
          params: {
            eTime: startDate,
            sTime: yearDate
          }
        });
        this.xYearValue = response.data.data.type;
        this.yYearValue = response.data.data.cnt;
        console.log(response.data.data);

        //this.drawLineMonth("myChart3","bar");
        this.drawLineYear("myChart4","bar");


        // 在数据赋值完成后再调用函数
        this.fetchData();
      } catch (error) {
        console.log(error);
      }
    };

    fetchDataYear();

  },
  mounted() {

  }

}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}

#container {
  width: 80%;
  height: 400px;
  margin: 100px auto;
  border: 2px solid red;
}
</style>
