<template>
    <div>
        <el-drawer
  title="参数选择"
  :visible.sync="drawer">

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
        <!-- <video 
        :src=this.videoUrl
        width="870"
        height="490"
        autoplay
        controls
        >
        </video> -->
    
    <div>
        <el-button @click="drawer = true" type="primary" style="margin-left: 16px;">
            更改
        </el-button>
        <div class="block">
            <el-table
          :data=this.marks
          style="width: 100%">
        <el-table-column
            prop="sid"
            label="回放编号"
            width="300">
        </el-table-column>
        <el-table-column
            prop="type"
            label="行为"
            width="400">
        </el-table-column>
        <el-table-column
            prop="stime"
            label="时间"
            width="500">
        </el-table-column>
        <!-- <el-table-column
            prop="owner"
            label="是否具有管理权限"
            width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.owner ? '是' : '否' }}</span>
          </template>
        </el-table-column> -->
        <el-table-column>
          <template slot-scope="scope">
            <!-- <el-button-group>
            <el-button type="primary" icon="el-icon-search" v-show="scope.row.owner" @click="viewMembers(scope.row)">查看成员</el-button>
            <el-button type="primary" icon="el-icon-edit" v-show="scope.row.owner" @click="showAddMemberDialog(scope.row)">添加成员</el-button>
            <el-button type="primary" icon="el-icon-delete" v-show="scope.row.owner" @click="showDeleteDialog(scope.row)">删除成员</el-button>
            </el-button-group> -->
            <el-button
                @click.native.prevent="startPlay(scope.$index, scope.row)"
                type="text"
                size="small">
                查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
  </div>
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
            // videoUrl: 'https://dbd-video-0.oss-cn-beijing.aliyuncs.com/A.mp4',
            drawer:true,
            value:0,
            min:34000,
            max:38000,
            tableData:[],
            tableData_2:[],
            value1:null,
            marks: []
        
        }
    },
    created() {
        axios.get(restweburl + "cameras",)
        .then((res) => {
          this.tableData_2 = res.data.data;
          this.loading_2=false;
        })
        .catch(function (error) {
          console.log(error);
        });
  },
  methods: {
      goBack() {
        this.$router.go(-1)
      },
      startPlay(index, rows) {
        console.log(rows);
        // window.location.href=`http://116.204.11.171:8081?cid=${rows.cid}`
        this.$router.push(`playerback/${rows.sid}`);
      },
      chdate(){
        var date = this.value1.split('-')
        this.drawer=false;

        axios.get(restweburl + "sus", {params:{'cID':this.currentRow.cid,'year':date[0],'month':date[1],'day':date[2]}})
        .then((res) => {
          this.marks = res.data.data;
          console.log(res.data);
          this.loading=false;
        })
        .catch(function (error) {
          console.log(error);
        });
      },
      handleCurrentChange(val) {
        this.currentRow = val;
      },
    }
}
</script>