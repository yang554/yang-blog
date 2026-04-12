<template>
  <el-container class="admin-home-view">
    <el-header class="header-container">
      <div class="logo">
        <img src="@/assets/images/logo.png" alt="" width="40px" height="40px" />
        <span class="title">枫萱悦宁后台管理</span>
      </div>

      <div class="right">
        <div class="user-info">
          <el-dropdown class="userinfo-dropdown" @command="handleCommand">
            <span class="el-dropdown-link">
              <div class="avatar-container">
                <img :src="this.$store.state.login_user.avatar" />
                <div class="online-indicator"></div>
              </div>
            </span>
            <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
              <el-dropdown-item command="userInfo">
                <i class="el-icon-user"></i>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="setting">
                <i class="el-icon-setting"></i>设置
              </el-dropdown-item>
              <el-dropdown-item command="logout">
                <i class="el-icon-switch-button"></i>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <span class="username">{{ this.$store.state.login_user.username }}</span>
        </div>
      </div>
    </el-header>

    <el-container class="main">
      <el-aside class="main-aside" :class="{ 'is-collapsed': this.isCollapsed }">
        <div class="sidebar-header">
          <span>系统导航</span>
          <i class="el-icon-s-fold toggle-btn" @click="toggleSidebar"></i>
        </div>

        <el-menu class="home-aside" unique-opened :default-active="sidebarActiveIndex" router background-color="#0f172a"
          text-color="#cbd5e1" active-text-color="#38bdf8">
          <el-menu-item index="/admin/dashboard">
            <span slot="title">首页</span>
          </el-menu-item>

          <el-submenu :index="item.path" v-for="(item, index) in menus" :key="index" class="tech-submenu">
            <template slot="title">
              <i :class="item.myIcon"></i>
              <span>{{ item.meta.title }}</span>
            </template>
            <el-menu-item :index="child.path" v-for="(child, idx) in item.children" v-if="!child.hidden" :key="idx"
              class="submenu-item">
              <i :class="child.myIcon ? child.myIcon : item.myIcon"></i>
              <span>{{ child.meta.title }}</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <el-main class="main-container">
        <div class="breadcrumb">
          <el-breadcrumb separator=">" v-if="this.$router.currentRoute.path != '/admin/dashboard'">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">
              <i class="el-icon-s-home"></i>首页
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ this.$router.currentRoute.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="content-container">
          <router-view :key="this.$store.state.date" />
        </div>
      </el-main>
    </el-container>

    <div class="tech-overlay">
      <div class="grid-line"></div>
      <div class="grid-line"></div>
      <div class="grid-line"></div>
    </div>
  </el-container>
</template>

<script>
import { _userLogout } from "@/api/api.js";
export default {
  name: "HomeView",
  components: {},
  data() {
    return {
      timeLineHeight: "",
      sidebarActiveIndex: "/admin/dashboard",
      activeDate: new Date().getTime(),
      isCollapsed: false,
    };
  },
  computed: {
    menus() {
      let arr = [""];
      arr = this.$router.options.routes.filter((item) => {
        if (item.meta.roles == undefined && item.hidden == false) {
          return true;
        } else if (item.meta.roles != undefined) {
          return item.meta.roles.includes(
            this.$store.state.login_user.roles[0].name
          );
        }
      });
      return arr;
    },
  },
  methods: {
    async handleCommand(command) {
      if (command === "logout") {
        try {
          // 调用后端登出接口（如果有）
          const result = await _userLogout();
          console.log(result.data.msg)
        } catch (error) {
          console.error('登出失败:', error);
        } finally {
          // 清除前端认证状态
          this.$store.dispatch('logout');
          // 跳转到登录页
          this.$router.push('/login');
        }
      }
    },
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
    },
  },
  created() {
    
  },
  watch: {
    $route(route) {
      this.sidebarActiveIndex = route.path;
    },
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
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

// 科技感配色方案
@primary-color: #2563eb; // 科技蓝
@accent-color: #8b5cf6; // 紫色点缀
@background-dark: #0f172a; // 深蓝背景
@text-primary: #e2e8f0; // 主文本色
@text-secondary: #94a3b8; // 次文本色
@card-bg: rgba(30, 41, 59, 0.7); // 卡片背景
@border-tech: #1e293b; // 边框颜色
@hover-color: #334155; // 悬停颜色
@success-color: #10b981; // 成功色
@warning-color: #f59e0b; // 警告色

.admin-home-view {
  font-family: 'Noto Sans SC', 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  height: 100vh;
  position: relative;
  overflow: hidden;

  .header-container {
    background: rgba(15, 23, 42, 0.8);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid rgba(56, 189, 248, 0.2);
    height: 70px;
    display: flex;
    align-items: center;
    padding: 0 30px;
    position: relative;
    z-index: 10;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);

    .logo {
      display: flex;
      align-items: center;

      img {
        margin-right: 15px;
        filter: drop-shadow(0 0 5px rgba(56, 189, 248, 0.5));
      }

      .title {
        font-size: 22px;
        font-weight: 700;
        background: linear-gradient(to right, #38bdf8, @accent-color);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        letter-spacing: 1px;
        text-shadow: 0 0 10px rgba(56, 189, 248, 0.3);
      }
    }

    .right {
      margin-left: auto;

      .user-info {
        display: flex;
        align-items: center;

        .avatar-container {
          position: relative;
          width: 50px;
          height: 50px;
          margin-right: 15px;
          cursor: pointer;
          transition: transform 0.3s;

          &:hover {
            transform: translateY(-3px);
          }

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            border: 2px solid @primary-color;
            object-fit: cover;
            box-shadow: 0 0 15px rgba(56, 189, 248, 0.4);
          }

          .online-indicator {
            position: absolute;
            bottom: 0;
            right: 0;
            width: 12px;
            height: 12px;
            background: @success-color;
            border-radius: 50%;
            border: 2px solid @background-dark;
            box-shadow: 0 0 5px @success-color;
          }
        }

        .username {
          color: @text-primary;
          font-size: 16px;
          font-weight: 500;
          letter-spacing: 0.5px;
        }
      }
    }
  }

  .main {
    height: calc(100vh - 70px);

    .main-aside {
      width: 240px !important;
      background: @background-dark;
      border-right: 1px solid @border-tech;
      transition: width 0.3s ease;
      position: relative;
      z-index: 5;

      &.is-collapsed {
        width: 64px !important;

        .sidebar-header span,
        .el-menu-item span,
        .el-submenu__title span {
          display: none;
        }

        .menu-arrow {
          display: none;
        }
      }

      .sidebar-header {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0 20px;
        border-bottom: 1px solid @border-tech;

        span {
          color: @text-primary;
          font-size: 16px;
          font-weight: 600;
          letter-spacing: 0.5px;
          text-transform: uppercase;
          background: linear-gradient(to right, #38bdf8, @accent-color);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
        }

        .toggle-btn {
          color: @text-primary;
          font-size: 20px;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            color: @primary-color;
            transform: scale(1.1);
          }
        }
      }

      .home-aside {
        border-right: none;
        padding: 10px 0;

        .el-menu-item,
        .el-submenu__title {
          height: 50px;
          line-height: 50px;
          margin: 5px 10px;
          border-radius: 8px;
          transition: all 0.3s;

          i {
            color: @text-primary;
            margin-right: 12px;
            font-size: 18px;
          }

          &:hover {
            background: rgba(56, 189, 248, 0.15) !important;
          }

          &.is-active {
            background: linear-gradient(to right, rgba(37, 99, 235, 0.3), rgba(56, 189, 248, 0.15)) !important;
            border-left: 3px solid @primary-color;
          }
        }

        .tech-submenu {
          .el-submenu__title {
            display: flex;
            align-items: center;

            .menu-arrow {
              margin-left: auto;
              transition: transform 0.3s;
            }
          }

          &.is-opened {
            .el-submenu__title .menu-arrow {
              transform: rotate(90deg);
            }
          }

          .submenu-item {
            height: 45px;
            padding-left: 50px !important;

            i {
              font-size: 14px;
            }
          }
        }
      }
    }

    .main-container {
      background: rgba(15, 23, 42, 0.5);
      padding: 20px 30px;
      position: relative;

      .breadcrumb {
        margin-bottom: 20px;
      }

      .content-container {
        background: @card-bg;
        border: 1px solid @border-tech;
        border-radius: 12px;
        padding: 5px;
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
        min-height: calc(100vh - 150px);
        overflow: auto;

        &::-webkit-scrollbar {
          width: 8px;
        }

        &::-webkit-scrollbar-track {
          background: rgba(30, 41, 59, 0.5);
          border-radius: 4px;
        }

        &::-webkit-scrollbar-thumb {
          background: linear-gradient(@primary-color, @accent-color);
          border-radius: 4px;
        }
      }
    }

    
  }
}

.user-dropdown-menu {
  background: rgba(15, 23, 42, 0.95) !important;
  border: 1px solid @border-tech !important;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4) !important;
  border-radius: 8px !important;
  overflow: hidden;
  backdrop-filter: blur(10px);

  .el-dropdown-menu__item {
    color: @text-primary !important;
    padding: 10px 20px !important;
    transition: all 0.3s;
    display: flex;
    align-items: center;

    i {
      margin-right: 10px;
      font-size: 16px;
      width: 20px;
      text-align: center;
    }

    &:hover {
      background: rgba(56, 189, 248, 0.15) !important;
      color: @primary-color !important;
    }

    &.is-divided {
      border-top: 1px solid @border-tech !important;
    }
  }
}
</style>