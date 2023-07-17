<template>
  <div>
    <el-card style="margin-bottom: 15px">
      <div class="block">
        <span class="demonstration" :style="{ marginRight: '10px' }">时间段：</span>
        <el-date-picker
            v-model="value1"
            type="datetimerange"
            :picker-options="pickerOptions"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            align="right"
            :style="{ marginRight: '20px' }">
        </el-date-picker>



        <span :style="{ marginRight: '10px' }">摄像头：</span>
        <el-select v-model="value" filterable placeholder="请选择摄像头" :style="{ marginRight: '20px' }">
          <el-option
              v-for="item in options"
              :key="item.cid"
              :label="item.cid"
              :value="item.cid">
          </el-option>
        </el-select>


        <span :style="{ marginRight: '10px' }">图表类型：</span>
        <el-select v-model="pictureType" filterable placeholder="图表类型" :style="{ marginRight: '20px' }">
          <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.value"
              :value="item.value">
          </el-option>
        </el-select>
        <el-button :plain="true" type="primary" icon="el-icon-search" circle  @click="getData">获取数据</el-button>
        <el-button type="primary" icon="el-icon-search" circle @click="() => drawLine('myChart')">绘制图形</el-button>

      </div>
    </el-card>
    <el-card>
      <div class="grid-content bg-purple"><div id="myChart" :style="{width: '1000px', height: '700px'}"></div></div>
<!--      <el-row>-->

<!--        <el-col :span="12">-->
<!--          <div class="grid-content bg-purple"><div id="myChart" :style="{width: '600px', height: '500px'}"></div></div></el-col>-->


<!--        <el-col :span="12"><div class="grid-content bg-purple-light"> <div id="myChart2" :style="{width: '600px', height: '500px'}"></div></div></el-col>-->

<!--      </el-row>-->

<!--      <el-row>-->
<!--      <el-col :span="12">-->
<!--        <div class="grid-content bg-purple"><div id="myChart3" :style="{width: '600px', height: '500px'}"></div></div></el-col>-->


<!--      <el-col :span="12"><div class="grid-content bg-purple-light"> <div id="myChart4" :style="{width: '600px', height: '500px'}"></div></div></el-col>-->

<!--      </el-row>-->

    </el-card>
  </div>
</template>

<script>
import dayjs from 'dayjs';
import axios from "axios";
import global from './GlobalPage.vue'
const restweburl = global.ip;

export default {
  name: "dataAnalyse",
  data() {
    return {
      options: [],
      typeOptions: [{
        value: 'bar',
      }, {
        value: 'line',
      }, {
        value: 'pine',
      }],
      value: '',
      xValue:["抽烟","玩手机","明火"],
      yValue:[5, 20, 36],

      dialogTableVisible: false,
      dialogFormVisible: false,
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      },
      formLabelWidth: '120px',
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      value1: [],
      value2: '',
      pictureType:'bar',
    };
  },
  mounted(){
    //this.drawLine("myChart");
    // this.drawLine("myChart2");
    // this.drawLine("myChart3");
    // this.drawLine("myChart4");
  },
  created() {
    //   var params = new URLSearchParams();
    //   params.append('wName',this.$parent.$route.params.id);
    //   params.append('grant','-1');
    //   var c =this.$parent.$route.params.id;
    axios.get(restweburl + "cameras",)
        .then((res) => {
          this.options = res.data.data;
          this.loading=false;
        })
        .catch(function (error) {
          console.log(error);
        });

  },
  methods:{

    getData(){
      const startDate = dayjs(this.value1[0]).format('YYYY-MM-DDTHH:mm:ss');
      const endDate = dayjs(this.value1[1]).format('YYYY-MM-DDTHH:mm:ss');

      console.log(startDate); // 开始日期，格式为 "2023-07-13T21:44:12"
      console.log(endDate); // 结束日期，格式为 "2023-07-13T21:44:12"

      var params = new URLSearchParams();
      params.append('cID',this.value);
      params.append('sTime',startDate);
      params.append('eTime',endDate);
      console.log("111");
      console.log(params.toString());
      console.log(params);
      // params.append('month',this.month);
      // params.append('day',this.day);
      //
      axios.get(restweburl + "count", {
        params: {
          cID: this.value,
          sTime:startDate,
          eTime:endDate
        }
      })
          .then((res) => {
            this.xValue = res.data.data.type;
            this.yValue=res.data.data.cnt;

            console.log(res.data.data);

            this.$message({
              message: '成功获取数据！\n点击绘制图形开始绘制！',
              type: 'success'
            });

          })
          .catch(function (error) {
            console.log(error);
          });

    },
    drawLine(chartID){

      // 基于准备好的dom，初始化echarts实例
//console.log(this.value1.);

      // 获取选择的日期

      console.log(this.xValue);
      console.log(this.yValue);
      let myChart = this.$echarts.init(document.getElementById(chartID));


      myChart.setOption({
        title: { text: '危险行为数量统计图' },
        tooltip: {},
        xAxis: {
          data: this.xValue,
          axisLabel: {
            interval: 0 // 设置横坐标显示的间隔，这里设置为2表示每隔两个数据显示一个
          }
        },
        grid: {
          left: '10%',
          right: '10%',
          top: '10%',
          bottom: '10%',
          borderColor: '#012f4a',
          containLabel: true // 是否包含坐标轴标签
        },
        yAxis: {},
        series: [{
          name: '数量',
          type: this.pictureType,
          data: this.yValue,
          itemStyle: {
            color: '#81bcd9'// 每个柱子的颜色分别为红、绿、蓝、黄、紫
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}' // 显示具体的数值
          }
        }]
      });


    }
  },
}
</script>

<style scoped>

</style>
