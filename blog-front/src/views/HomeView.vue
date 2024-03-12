<template>
  <div class="resize" :style="{ width: state.width + 'px', height: state.hHeight + 'px' }">
    <el-container class="content">
      <el-header class="header">
        <img
          class="img"
          :style="{ width: state.width + 'px', height: state.headerhHeight + 'px' }"
          src="@/assets/top-bg.png"
        />
        <div
          class="avatar-block"
          :style="{ width: state.avatarBlock + 'px', height: state.headerhHeight + 'px' }"
        >
          <TextScrollView class="block-left" />
          <div class="block-right">
            <el-avatar :size="130" src="../assets/defaultImg.png" />
          </div>
        </div>
        <div
          class="content-right"
          :style="{ width: state.contentRightWith + 'px', height: state.headerhHeight + 'px' }"
        >
          <div class="span-div">
            <el-icon color="#409EFC">
              <Avatar />
            </el-icon>
            <span class="span-txt">用户名：杨华杰</span>
            <el-icon color="#409EFC">
              <Iphone />
            </el-icon>
            <span class="span-txt">联系方式：15723084621</span>
            <el-icon color="#409EFC">
              <Postcard />
            </el-icon>
            <span class="span-txt">邮箱：1251120808@qq.com</span>
          </div>
          <div class="span-div">
            <el-icon color="#409EFC">
              <AlarmClock />
            </el-icon>
            <span class="span-txt">注册时间：2023-11-24</span>
          </div>
          <div class="span-div">
            <el-icon color="#409EFC">
              <Location />
            </el-icon>
            <span class="span-txt">地址：福建省福州市马尾区快安村</span>
          </div>
          <div class="span-div">
            <el-icon color="#409EFC">
              <Connection />
            </el-icon>
            <span class="span-txt">访问量：158956</span>
            <el-icon color="#409EFC">
              <Notebook />
            </el-icon>
            <span class="span-txt">文章：66</span>
            <el-icon color="#409EFC">
              <Collection />
            </el-icon>
            <span class="span-txt">原创：58</span>
          </div>
        </div>
      </el-header>
      <el-container class="container">
        <el-aside
          class="home-left"
          :style="{ width: state.homeLeftWidth + 'px', height: state.homeLeftHeight + 'px' }"
        >
          <img
            class="img"
            :style="{ width: state.homeLeftWidth + 'px', height: state.homeLeftHeight + 'px' }"
            src="@/assets/left-bg.png"
          />
          <div class="common-layout">
            <el-container>
              <!-- <el-header style="height:10px"></el-header> -->
              <el-main
                :style="{ width: state.homeLeftWidth + 'px', height: state.leftMainHeight + 'px' }"
              >
                <CarouselView class="home-left-car" />
              </el-main>
              <el-footer :style="{ height: state.footerHeight + 'px' }"></el-footer>
            </el-container>
          </div>
        </el-aside>
        <el-main class="main-content">
          <img
            class="img"
            :style="{ width: state.mainWidth + 'px', height: state.homeLeftHeight + 'px' }"
            src="@/assets/main-bg.png"
          />
          <div class="common-layout">
            <el-container>
              <el-header>
                <el-menu
                  class="el-menu"
                  mode="horizontal"
                  background-color="#86ac79"
                  text-color="#fff"
                  active-text-color="#ffd04b"
                  router
                >
                  <el-menu-item :index="menu.path" v-for="(menu, idx) in menus" :key="idx">
                    <span>{{ menu.meta.title }}</span>
                  </el-menu-item>
                </el-menu>
              </el-header>
              <el-main :style="{ width: state.mainWidth + 'px', height: state.mainHeight + 'px' }">
                <router-view :key="$route.fullPath" />
              </el-main>
              <el-footer></el-footer>
            </el-container>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from "vue-router";
import {
  ref,
  onMounted,
  onUnmounted,
  defineComponent,
  reactive,
  nextTick,
  watch,
  computed
} from "vue";
import CarouselView from "@/components/CarouselView.vue";
import TextScrollView from "@/components/TextScrollView.vue";
import LeftView from "@/components/LeftView.vue";

const router = useRouter();
const route = useRoute();

const state = reactive({
  sidebarActiveIndex: "/home",
  arr: [],
  hHeight: document.documentElement.clientHeight,
  width: document.documentElement.clientWidth,

  headerhHeight: document.documentElement.clientHeight * 0.22,
  contentRightWith: document.documentElement.clientWidth * 0.75,
  avatarBlock: document.documentElement.clientWidth * 0.22,

  leftMainHeight: document.documentElement.clientHeight * 0.64,

  homeLeftHeight: document.documentElement.clientHeight * 0.78,
  homeLeftWidth: document.documentElement.clientWidth * 0.14,

  mainWidth: document.documentElement.clientWidth * 0.86,
  mainHeight: document.documentElement.clientHeight * 0.55,
  homeLeftFooterHeight: document.documentElement.clientWidth * 0.08
});

// watch(router, () => {
//   console.log("fdsf")
//   state.sidebarActiveIndex = route.path;
// })

//菜单
const menus = computed(() => {
  let menuList = [];
  state.arr = router.options.routes.filter(item => {
    if (item.name === "home") {
      menuList = item.children;
    }
  });
  // console.log(state.arr)
  return menuList;
});

const handleResize = () => {
  state.hHeight = document.documentElement.clientHeight;
  state.width = document.documentElement.clientWidth;

  state.headerhHeight = document.documentElement.clientHeight * 0.22;
  state.contentRightWith = document.documentElement.clientWidth * 0.75;
  state.avatarBlock = document.documentElement.clientWidth * 0.22;

  state.leftMainHeight = document.documentElement.clientHeight * 0.64;

  state.homeLeftHeight = document.documentElement.clientHeight * 0.78;
  state.homeLeftWidth = document.documentElement.clientWidth * 0.14;

  state.mainHeight = document.documentElement.clientHeight * 0.55;
  state.mainWidth = document.documentElement.clientWidth * 0.86;
  state.homeLeftFooterHeight = document.documentElement.clientHeight * 0.08;
};

onMounted(() => {
  window.onresize = () => {
    handleResize();
  };
});

onUnmounted(() => {
  window.onresize = null;
});
</script>

<style lang="scss" scoped>
.resize {
  // background-image: url('@/assets/home-bg1080.png');
  background-size: cover;
  z-index: -1;
  background-repeat: no-repeat;
  background-position: center center;
  position: fixed;
}

.img {
  z-index: -1;
  background-repeat: no-repeat;
  background-position: center center;
  position: fixed;
}

.header {
  // background-color: rgb(165, 177, 170);
  padding: 0px;
  margin: 0px;
  height: 100%;
  width: 100%;

  .avatar-block {
    flex: 1;
    display: flex;
    justify-content: center;
    float: left;

    .block-left {
      float: left;
      z-index: 2;
      position: fixed;
    }

    .block-right {
      margin-top: 50px;
    }
  }

  .content-right {
    float: right;
  }

  .el-descriptions {
    margin-top: 20px;
  }

  .cell-item {
    display: flex;
    align-items: center;
  }

  .margin-top {
    margin-top: 20px;
  }

  .span-div {
    margin-top: 20px;

    .span-txt {
      font-size: 18px;
      margin: 5px;
      color: rgb(15, 15, 15);
    }
  }
}

.container {
  // margin: 0 30px 0 30px;
  margin: 0px;
  padding: 0px;

  .home-left {
    padding: 0px;
    margin: 0px;
    .el-main {
      margin: 0px;
      padding: 12px;
      //overflow: hidden;
    }
    /* 隐藏滚动条的CSS */
    .el-main::-webkit-scrollbar {
      display: none;
    }
    .footer {
      background-color: rgb(0, 255, 98);
    }
  }

  .main-content {
    // background-color: rgb(145, 6, 6);
    // background-image: url('@/assets/main-bg.png');
    // background-size: cover;
    padding: 0px 10px 0px 0px;

    .el-main {
      overflow: hidden;
      scrollbar-width: none;
      padding: 0px 20px 0px 20px;
    }

    .router-content {
      overflow: auto;
    }
    /* 隐藏滚动条的CSS */
    .el-main::-webkit-scrollbar {
      display: none;
    }

    .el-header {
      padding: 0px;
    }
  }
}
</style>