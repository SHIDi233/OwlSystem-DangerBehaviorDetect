<template>
    <div>
<el-drawer
  title="参数选择"
  :visible.sync="drawer"
  :direction="direction"
  :before-close="handleClose">

    <el-container>
    <el-main>
      <div>
    <el-date-picker
      v-model="value1"
      type="date"
      placeholder="选择日期"
      value-format="yyyy-MM-dd">
    </el-date-picker>
    <el-table
    ref="singleTable"
    :data="tableData_2"
    highlight-current-row
    @current-change="handleCurrentChange"
    style="width: 100%">
    <el-table-column
      fixed
      prop="cid"
      label="摄像头编号"
      width="150">
    </el-table-column>
    <el-table-column
      fixed
      prop="content"
      label="描述"
      width="750">
    </el-table-column>
    <el-table-column
      fixed
      prop="owner"
      label="是否为管理员"
      width="140">
    </el-table-column>
  </el-table>
  </div>
  <el-footer>
    <div style="text-align: center">
      <el-button type="primary" @click="chdate" >提交</el-button>
    </div>
    
  </el-footer>
  
</el-main>
</el-container>
</el-drawer>


<div style="text-align: right;">
  <el-button @click="drawer = true,loading=true" type="primary" style="text-align: right;">
  选择日期与摄像头
</el-button>
<h5>
    日期：{{this.date}}，摄像头：{{this.cid}}
</h5>
</div>


  <el-table
    :data="tableData"
    style="width: 100%"
    max-height="8 50"
    v-loading="loading">
    <el-table-column
      fixed
      prop="pid"
      label="回放的编号"
      width="150">
    </el-table-column>
    <el-table-column
      fixed
      prop="stime"
      label="开始时间"
      width="550">
    </el-table-column>
    <el-table-column
      fixed
      prop="etime"
      label="结束时间"
      width="550">
    </el-table-column>
    <el-table-column
      fixed="right"
      label="操作"
      width="120">
      <template slot-scope="scope">
        <el-button
        @click.native.prevent="startPlay(scope.$index, scope.row)"
          type="text"
          size="small">
          查看回放
        </el-button>
      </template>
    </el-table-column>
  </el-table>
        
    </div>
</template>

<script>
  import axios from 'axios'
  import global from './GlobalPage.vue'
//   import { Calendar } from 'element-ui';
  const restweburl = global.ip;
  // const restweburl = "http://192.168.10.167:8888/";
  export default {
    
    methods: {
      startPlay(index, rows) {
        console.log(rows);
        // window.location.href=`http://116.204.11.171:8081?cid=${rows.cid}`
        this.$router.push(`player/${rows.pid}`);
      },
      handleCurrentChange(val) {
        this.currentRow = val;
      },
      handleClose(done) {
        this.loading=false;
        done()
      },
      chdate(){
        var date = this.value1.split('-')
        this.drawer=false;

        this.date=date;
        this.cid=this.currentRow.cid;

        this.year=date[0]
        this.month=date[1]
        this.day = date[2]

        axios.get(restweburl + "playbacks", {params:{'cID':this.currentRow.cid,'year':date[0],'month':date[1],'day':date[2]}})
        .then((res) => {
          this.tableData = res.data.data;
          this.loading=false;
        })
        .catch(function (error) {
          console.log(error);
        });
      }
    },
    data() {
      return {
        drawer: false,
        direction: 'rtl',
        loading:true,
        tableData: [
        ],
        tableData_2: [
        ],
        year:2023,
        month:7,
        day:10,
        value1: '',
        currentRow: null,
        date:'未选定',
        cid:'未选定'
      }
    },
    created() {
        axios.get(restweburl + "playbacks", {params:{'cID':1,'year':this.year,'month':this.month,'day':this.day}})
        .then((res) => {
          this.tableData = res.data.data;
          this.loading=false;
        })
        .catch(function (error) {
          console.log(error);
        });

        axios.get(restweburl + "cameras",)
        .then((res) => {
          this.tableData_2 = res.data.data;
          this.loading_2=false;
        })
        .catch(function (error) {
          console.log(error);
        });
  },
}

</script>