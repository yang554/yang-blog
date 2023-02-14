<template>
  <div class="admin-home-view">
    <div class="header-container">
      <div class="logo">
        <!-- <img src='@/assets/images/logo.png' alt='' width='50px' height='50px'> -->
      </div>

      <div class="title">后台管理系统</div>

      <div class="right">
        <div>
          <el-dropdown class="userinfo-dropdown" @command="handleCommand">
            <span class="el-dropdown-link">
              <span>{{ this.$store.state.username }}</span>
              <img style="width: 55px; height: 55px;border-radius:50%;border: 1px solid #ddd;" alt="">
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="userInfo">个人中心</el-dropdown-item>
              <el-dropdown-item command="setting">设置</el-dropdown-item>
              <el-dropdown-item command="logout">注销登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main">
      <div class="main-aside">
        <el-menu class="home-aside" :default-active="sidebarActiveIndex" style="height: 100%" router
          background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">

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
      </div>

      <div class="main-container">
        <!-- 面包屑 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/" v-if="this.$router.currentRoute.path != '/home/blog'">
            <el-breadcrumb-item :to="{ path: '/home/blog' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ this.$router.currentRoute.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!--主要内容-->
        <router-view class="main-content" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomeView",
  // components: {},
  data() {
    return {
      sidebarActiveIndex: "/admin/dashboard"
    };
  },
  computed: {
    menus() {
      let arr = [''];
      arr = this.$router.options.routes.filter(item => {
        // console.log(item.hidden);
        return item.hidden == false;
      })
      // console.log(arr);
      return arr;
    }
  },
  methods: {
    handleCommand(command) {
    }
  },
  created() {
    // console.log(this.$store.state.login_user);
    // console.log(this.$router.options.routes);
  },
  watch: {
    $route(route) {
      this.sidebarActiveIndex = route.path;
    }
  }
};
</script>

<style lang="less" scoped>
.admin-home-view {
  background-color: rgb(239, 241, 244);
}

.header-container {
  width: 100%;
  height: 60px;
  line-height: 60px;
  font-size: 24px;
  font-weight: bold;
  display: flex;
  box-shadow: 0 2px 3px #ddd;
  background-color: #545c64;
  color: white;

  .logo {
    margin-left: 15px;
  }

  .title {
    flex: 1;
    margin-left: 30px;
    line-height: 60px;
    font-size: 26px;
  }

  .right {
    display: flex;

    div {
      margin-left: 10px;
      margin-right: 10px;
    }
  }
}

.main {
  width: 100%;
  min-height: 100%;
  display: flex;

  .main-aside {
    width: 200px;
    height: calc(100vh - 60px);
    background-color: rgb(84, 92, 100);

    .home-aside {
      width: 100%;
      // min-height: 100vh;
    }
  }

  .main-container {
    flex: 1;
    margin: 20px 0 0 10px;

    .main-content {
      padding: 20px;
      // height: calc(100vh - 180px);
    }
  }
}
</style>
