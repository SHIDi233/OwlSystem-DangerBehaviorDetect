<template>
    <el-container class="home-container">
       <!--头部  -->
       <el-header style="background-color: #3f5e69; height: 80px; ">
        <div>
          <!-- <img src="../assets/home页面.jpg" alt=""> -->
          
          <span style="font-size: 35px;">O W L 管 理 系 统</span>
        </div>

         <div>
           <el-badge value="new" class="item">
             <el-button @click="drawer = true" type="text" class="black-button">通知列表</el-button>
           </el-badge>

           <el-drawer
               title="我是标题"
               :visible.sync="drawer"
               :direction="direction"
               :before-close="handleClose">
             <span>我来啦!</span>
           </el-drawer>
           <el-button type="info" @click="closeID">退出</el-button>
         </div>

       </el-header>
       <!-- 页面主体区  嵌套容器  包裹 Aside与Main -->

       <el-container>
             <!-- 左侧 -->
             <el-aside width="200px">

                <!-- <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick"></el-tree> -->
             <el-menu
      default-active="1"
      class="el-menu-vertical-demo"
      @select="clickmenu">
          <el-menu-item index="home" @select="clickmenu">
            <i class="el-icon-s-home"></i>
            <span>首页</span>
          </el-menu-item>
      <el-submenu index="2" @select="clickmenu">
        <template slot="title">
          <i class="el-icon-video-camera-solid"></i>
          <span>危险监控</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="live" @select="clickmenu">
            <i class="el-icon-view"></i>
            实时监控
          </el-menu-item>
          <el-menu-item index="merge" @select="clickmenu">
            <i class="el-icon-cpu"></i>
            实验室
          </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-menu"></i>
          <span slot="title">视频回放</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="active">行为回放</el-menu-item>
          <el-menu-item index="list">回放列表</el-menu-item>
        </el-menu-item-group>
      </el-submenu>
      <el-submenu index="4">
        <template slot="title">
          <i class="el-icon-s-tools"></i>
          <span slot="title">摄像头管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item index="cameraList">摄像头列表</el-menu-item>
          <el-menu-item index="addCamera">添加摄像头</el-menu-item>
          <el-menu-item index="cameraMap">摄像头地图</el-menu-item>
        </el-menu-item-group>
      </el-submenu>

               <el-submenu index="5">
                 <template slot="title">
                   <i class="el-icon-s-data"></i>
                   <span slot="title">分析与统计</span>
                 </template>
                 <el-menu-item-group>
                   <el-menu-item index="dataAnalyse">分析统计</el-menu-item>

                 </el-menu-item-group>
               </el-submenu>


               <el-submenu index="6">
                 <template slot="title">
                   <i class="el-icon-warning"></i>
                   <span slot="title">划定危险区域</span>
                 </template>
                 <el-menu-item-group>
                   <el-menu-item index="regionDivision">划分区域</el-menu-item>

                 </el-menu-item-group>
               </el-submenu>
    </el-menu>

  </el-aside>
             <!-- 主体 -->
             <el-main>
              <keep-alive>
                <router-view v-if="$route.meta.keepAlive"></router-view>
              </keep-alive>
              <router-view v-if="!$route.meta.keepAlive"></router-view>
             </el-main>
       </el-container>
   </el-container>

</template>
<script>
    export default {
        data() {
      return {
           drawer: false,
        direction: 'rtl',
        data: [{
          label: '危险监控',
          children: [{
            label: '实时监控',
            url:'live',
          },
          {
            label: '实验室',
            url:'merge',
          }
        ]
        },
        {
            label: '视频回放',
            children:[
              {
                label: '行为回放',
                url:'active',
              },
              {
                label: '回放列表',
                url:'list',
              }
            ]
          },
         {
          label: '摄像头管理',
          children: [{
            label: '摄像头列表',
            url:'cameraList'
          },
            {
              label: '添加摄像头',
              url:'addCamera'
            },
              {
                label: '摄像头地图',
                url:'cameraMap'
              },
            ]
        },

          {
            label: '统计分析',
            children: [{
              label: '统计分析',
              url:'dataAnalyse'
            },
              ]
          }],
        defaultProps: {
          children: 'children',
          label: 'label'
        }
      };
    },
        methods: {
          handleClose(done) {
            this.$confirm('确认关闭？')
                // eslint-disable-next-line no-unused-vars
                .then(_ => {
                  done();
                })
                // eslint-disable-next-line no-unused-vars
                .catch(_ => {});
          },
            closeID(){
                localStorage.removeItem("DBDtoken");
                this.$router.push("../../login");
            },
            handleNodeClick(data) {
              console.log(data);
              if(data.url==undefined)
                return
              this.$router.push(data.url);
            },
            clickmenu(key){
              if(key=='merge'){
                window.open('http://116.204.11.171:8081/stitch.html')
                return
              }
              this.$router.push(key);
            }
        }
    }
</script>


<style scoped>


  .item {
    margin-top: 10px;
    margin-right: 40px;
  }
    /* // 标签的名字就是类的名字 */
   .home-container{
        height: 100%;
   }

   .el-header{
        background-color: #343d41;
        display: flex;
        justify-content: space-between;/*此时会留有空白*/
        padding-right: 20px;
        align-items: center;/*按钮上下居中*/
        /*文本*/
        color: #fff;
        font-size: 20px;
         >div{
            /*文本居中*/
            display: flex;
            align-items:center;

            span{
                margin-left: 15px;
            }

        }
      }

  /* .el-aside{
    background-color;
  } */

  .el-main{
    background-color: #EAEDF1;
  }
</style>
<style>
  .el-col {
    border-radius: 4px;
  }
  /* .bg-purple-dark {
    background: #99a9bf;
  } */
  /* .bg-purple {
    background: #d3dce6;
  }
  .bg-purple-light {
    background: #e5e9f2;
  } */
  .grid-content {
    border-radius: 4px;
    min-height: 36px;
  }
  .el-row-inline {
    display: flex;
    flex-wrap: wrap;
}
</style>

