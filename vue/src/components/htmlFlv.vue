<template>
    <div>
        <el-table
    :data="tableData"
    style="width: 100%"
    max-height="8 50"
    v-loading="loading">
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
      <template slot-scope="scope">
            <span>{{ scope.row.owner ? '是' : '否' }}</span>
          </template>
    </el-table-column>
    <!-- <el-table-column
      prop="senderName"
      label="发送人"
      width="100">
    </el-table-column>
    <el-table-column
      prop="content"
      label="简介"
      width="320">
    </el-table-column> -->
    <el-table-column
      fixed="right"
      label="操作"
      width="120">
      <template slot-scope="scope">
        <el-button
        @click.native.prevent="deleteRow(scope.$index, scope.row)"
          type="text"
          size="small">
          进入
        </el-button>
      </template>
    </el-table-column>
  </el-table>
    </div>
</template>

<script>
  import axios from 'axios'
  import global from './GlobalPage.vue'
  const restweburl = global.ip;
  // const restweburl = "http://192.168.10.167:8888/";
  export default {
    
    methods: {
      deleteRow(index, rows) {
        console.log(rows);
        window.open(`http://116.204.11.171:8081?cid=${rows.cid}`)
      }
    },
    data() {
      return {
        loading:true,
        tableData: [
        ]
      }
    },
    created() {
    //   var params = new URLSearchParams();
    //   params.append('wName',this.$parent.$route.params.id);
    //   params.append('grant','-1');
    //   var c =this.$parent.$route.params.id;
      axios.get(restweburl + "cameras",)
        .then((res) => {
          this.tableData = res.data.data;
          this.loading=false;
        })
        .catch(function (error) {
          console.log(error);
        });
  },
  }
</script>