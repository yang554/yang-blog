<template>
  <el-container class="admin-home-view">
    <el-header class="header-container">
      <div class="logo">
        <img src='@/assets/images/logo.png' alt='' width='30px' height='30px'>
      </div>
      <div class="title">杨柳博客后台管理</div>
      <div class="right">
        <div>
          <el-dropdown class="userinfo-dropdown" @command="handleCommand">
            <span class="el-dropdown-link" style="margin-bottom: 100px;">
              <img style="width: 55px; height: 55px;border-radius:50%;border: 1px solid #ddd;" alt=""
                :src="this.$store.state.login_user.avatar">
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="userInfo">个人中心</el-dropdown-item>
              <el-dropdown-item command="setting">设置</el-dropdown-item>
              <el-dropdown-item command="logout">注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <span style="color: #ddd;font-size: 20px;margin-top: 15px;">{{ this.$store.state.login_user.username }}</span>
      </div>
    </el-header>

    <el-container class="main">
      <el-aside class="main-aside" >
        <!-- unique-opened="true" -->
        <el-menu class="home-aside"  unique-opened :default-active="sidebarActiveIndex" router background-color="#545c64"
          text-color="#fff" active-text-color="#ffd04b">

          <el-menu-item index="/admin/dashboard">
            <i class="el-icon-menu"></i>
            <span slot="title">首页</span>
          </el-menu-item>

          <el-submenu :index="item.path" collapse="true" v-for="(item, index) in menus" :key="index">
            <template slot="title">
              <i :class="item.myIcon"></i>
              <span>{{ item.meta.title }}</span>
            </template>
            <el-menu-item :index="child.path" v-for="(child, idx) in item.children" v-if="!child.hidden" :key="idx">
              <i :class="child.myIcon ? child.myIcon : item.myIcon"></i>
              <span>{{ child.meta.title }}</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>

      </el-aside>

      <el-main class="main-container">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/" v-if="this.$router.currentRoute.path != '/home/blog'">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ this.$router.currentRoute.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!--主要内容-->
        <div class="main-content" :style="{ 'max-height': this.timeLineHeight + 'px' }">
          <router-view :key="this.$store.state.date" /></div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>

export default {
  name: "HomeView",
  components: {},
  data() {
    return {
      timeLineHeight: "",
      sidebarActiveIndex: "/admin/dashboard",
      activeDate: new Date().getTime(),
    };
  },
  computed: {
    menus() {
      let arr = [''];
      arr = this.$router.options.routes.filter(item => {
        if(item.meta.roles == undefined && item.hidden == false) {
          return true
        }else if (item.meta.roles != undefined){
          return item.meta.roles.includes(this.$store.state.login_user.roles[0].name)
        }
      })
      // console.log(arr);
      return arr;
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.$store.commit('removeLoginUser')
        this.$router.push("/login")
      }
    }
  },
  created() {
    // if (undefined == this.$store.state.login_user.username) {
    //   this.$router.push("/login")
    // }
  },
  watch: {
    $route(route) {
      this.sidebarActiveIndex = route.path;
    }
  },
  mounted() {
    this.timeLineHeight = document.documentElement.clientHeight - 60;
    window.onresize = () => {
      this.timeLineHeight = document.documentElement.clientHeight - 60;
    };
  },
};
</script>

<style lang="less" scoped>
.admin-home-view {
  //主
  background-color: rgb(239, 241, 244);
  position: relative;
  // z-index: 999;
  overflow: hidden;

  .header-container {
    //头部
    position: fixed;
    z-index: 99;
    width: 100%;
    height: 60px;
    font-size: 24px;
    font-weight: bold;
    display: flex;
    box-shadow: 0 1px 1px #ddd;
    background-color: #545c64;
    color: white;

    .logo {
      margin: 15px;
    }

    .title {
      flex: 1;
      margin-left: 5px;
      line-height: 60px;
      font-size: 26px;
    }

    .right {
      display: flex;

      div {
        margin-left: 1px;
        margin-right: 1px;
        margin-top: 1px;
      }
    }
  }

  .main {
    //主体
    width: 100%;
    overflow: hidden;
    display: flex;

    .main-aside {
      // overflow: hidden;
      overflow-y: auto;
      display: flex;
      height: 100vh;

      //菜单
      .home-aside {
        width: 280px;
        margin-top: 60px;
        background-color: rgb(84, 92, 100)
      }
    }

    .main-container {
      //内容
      margin: 70px 0 0 10px;
      overflow-y: hidden;
      // overflow-y:scroll;
      height: calc(100vh - 70px);

      .main-content {
        flex: 1;
        padding: 10px;
        overflow-y: scroll;
        height: calc(100vh - 150px);
      }
    }
  }
}
</style>
