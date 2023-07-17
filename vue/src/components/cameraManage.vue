<template>
  <div>
    <el-card class="box-card">
      <el-row>
        <el-col :span="12"><div class="grid-content bg-purple">
          <div style="margin-top: 15px;">
            <el-input placeholder="请输入内容" v-model="input3" class="input-with-select">
<!--              <el-button slot="append" icon="el-icon-search"></el-button>-->
            </el-input>
          </div></div></el-col>
        <el-col :span="12"><div class="grid-content bg-purple-light">
          <el-button style="float: right; "  @click="search" type="primary" round>搜索</el-button>
        </div></el-col>
      </el-row>
    </el-card>
    <el-card class="box-card">
      <el-table
          :data="cameras"
          style="width: 100%">
        <el-table-column
            prop="cid"
            label="摄像头编号"
            width="200">
        </el-table-column>
        <el-table-column
            prop="content"
            label="描述"
            width="300">
        </el-table-column>
        <el-table-column
            prop="addr"
            label="摄像头地址"
            width="200">
        </el-table-column>
        <el-table-column
            prop="owner"
            label="是否具有管理权限"
            width="200">
          <template slot-scope="scope">
            <span>{{ scope.row.owner ? '是' : '否' }}</span>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <el-button-group>
            <el-button type="primary" icon="el-icon-search" v-show="scope.row.owner" @click="viewMembers(scope.row)">查看成员</el-button>
            <el-button type="primary" icon="el-icon-edit" v-show="scope.row.owner" @click="showAddMemberDialog(scope.row)">添加成员</el-button>
            <el-button type="primary" icon="el-icon-delete" v-show="scope.row.owner" @click="showDeleteDialog(scope.row)">删除成员</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>


      <el-dialog title="成员列表" :visible.sync="dialogTableVisible">
        <el-table :data="members">
          <el-table-column property="uid" label="成员ID" width="150"></el-table-column>
          <el-table-column property="mail" label="成员邮箱" width="200"></el-table-column>
          <el-table-column property="uname" label="姓名" width="150"></el-table-column>
          <el-table-column property="owner" label="是否具有管理权限" width="200">
            <template slot-scope="scope">
            <span>{{ scope.row.owner ? '是' : '否' }}</span>
          </template></el-table-column>
        </el-table>
      </el-dialog>


      <el-dialog title="删除成员" :visible.sync="deleteDialogTableVisible" width="60%">
        <el-table :data="members">
          <el-table-column property="uid" label="成员ID" width="150"></el-table-column>
          <el-table-column property="mail" label="成员邮箱" width="200"></el-table-column>
          <el-table-column property="uname" label="姓名" width="150"></el-table-column>
          <el-table-column property="owner" label="是否具有管理权限" width="200">
            <template slot-scope="scope">
              <span>{{ scope.row.owner ? '是' : '否' }}</span>
            </template>
          </el-table-column>
          <el-table-column>
            <template slot-scope="scope">
              <el-button type="primary" icon="el-icon-delete" v-show="!scope.row.owner" @click="deleteMember(scope.row)">删除成员</el-button>
            </template>
          </el-table-column>

        </el-table>
      </el-dialog>

      <el-dialog title="添加成员" :visible.sync="addDialogTableVisible" width="500px">
        <el-form :model="addParams" label-width="120px">
          <el-form-item label="摄像头ID:">
            <span>{{ addParams.cID }}</span>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="addParams.mail"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
      <el-button @click="addCancel">取消</el-button>
      <el-button type="primary" @click="addMember">确定</el-button>
    </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      dialogTableVisible:false,
      deleteDialogTableVisible:false,
      addDialogTableVisible:false,
      confirmDelete:false,
      selectedCid:'',
      cameraID: '',
      cameras:[],
      members:[],
      deleteParams:{cID:'',uID:''},
      addParams:{cID:'',mail:''},
    }
  },
  methods:{
    async search(){
      if(this.cameraID===''){
        try {
          const response = await axios.get("http://116.204.11.171:8080/cameras");
          this.cameras = response.data.data;
          console.log(this.cameras);
          this.cameraID='';
        }catch (e) {
          console.error(e);
        }
      }
    },
    async viewMembers(row) {
      // 在这里访问 row 对象，执行查看成员的操作
      console.log('查看成员', row);
      try {
        const response = await axios.get("http://116.204.11.171:8080/members",{
          params:{
            cID:row.cid,
          }
        });
        this.members = response.data.data;
        this.dialogTableVisible=true;
      }catch (e) {
        console.error(e);
      }
    },
    showAddMemberDialog(row){
      this.addDialogTableVisible = true;
      this.addParams.cID = row.cid;
      console.log(this.addParams);
    },


    async addMember() {
      // 在这里访问 row 对象，执行添加成员的操作
      console.log('添加成员');
      console.log(this.addParams);
      var params = new URLSearchParams();
      params.append("cID",this.addParams.cID);
      params.append("mail",this.addParams.mail);
      try {
        const response = await axios.post("http://116.204.11.171:8080/addMember",params);
        if(response.data.code===1){
          alert("添加成功");
          this.addParams = {cID:'',mail:''};
        }else {
          alert("添加失败"+response.data.msg);
        }
      }catch (e) {
        console.error(e);
      }
    },


    addCancel(){
      this.addDialogTableVisible = false;
    },
    async showDeleteDialog(row){
      this.deleteDialogTableVisible = true;
      this.deleteParams.cID = row.cid;
      console.log(this.members);
      try {
        const response = await axios.get("http://116.204.11.171:8080/members",{
          params:{
            cID:row.cid,
          }
        });
        this.members = response.data.data;
      }catch (e) {
        console.error(e);
      }
    },
    async deleteMember(row) {
      // 在这里访问 row 对象，执行删除成员的操作
      console.log('删除成员', row);
      this.deleteParams.uID=row.uid;
      console.log(this.deleteParams);
      try {
        const response = await axios.delete("http://116.204.11.171:8080/delMember",
            { params: { cID: this.deleteParams.cID, uID: this.deleteParams.uID }} );
        if(response.data.code===1){
          alert("删除成功");
        }else {
          alert("删除失败");
        }
      }catch (e) {
        console.error(e);
      }
    },

  },
  created(){
    this.search();
  },
}
</script>



<style scoped>
.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.box-card {
  width: 100%; /* 或根据需要设置其他适合的宽度 */
}

</style>
