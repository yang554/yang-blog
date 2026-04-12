<template>
  <div class="resize" :style="{ width: state.width + 'px', height: state.hHeight + 'px' }">
    <el-container class="content">
      <el-header class="header">
        <img class="img" :style="{ width: state.width + 'px', height: state.headerhHeight + 'px' }"
          src="@/assets/top-bg.png" />
        <div class="avatar-block" :style="{ width: state.avatarBlock + 'px', height: state.headerhHeight + 'px' }">
          <TextScrollView class="block-left" />
          <div class="block-right">
            <el-avatar :size="130" :src="img" />
          </div>
        </div>
        <div class="content-right" :style="{ width: state.contentRightWith + 'px', height: state.headerhHeight + 'px' }">
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
            <span class="span-txt">访问量：{{ userInfo.browsecount }}</span>
            <el-icon color="#409EFC">
              <Notebook />
            </el-icon>
            <span class="span-txt">文章：{{ userInfo.count }}</span>
            <el-icon color="#409EFC">
              <Collection />
            </el-icon>
            <span class="span-txt">原创：{{ userInfo.originalCount }}</span>
          </div>
        </div>
      </el-header>
      <el-container class="container">
        <el-main class="main-content">
          <img class="img" :style="{ width: state.mainWidthImg + 'px', height: state.homeLeftHeight + 'px' }"
            src="@/assets/main-bg.png" />
          <div >
            <el-container>
              <el-header class="el-header-menu" :style="{ width: state.mainWidth + 'px'}">
                <el-menu mode="horizontal" background-color="#86ac79" text-color="#fff"
                  active-text-color="#ffd04b" router>
                  <el-menu-item :index="menu.path" v-for="(menu, idx) in menus" :key="idx">
                    <span>{{ menu.meta.title }}</span>
                  </el-menu-item>
                </el-menu>
              </el-header>
              <el-main class="common-layout" :style="{ width: state.mainWidth + 'px', height: state.mainHeight + 'px' }">
                <router-view :key="$route.fullPath" :mainHeight="state.mainHeight" />
              </el-main>
              <el-footer :style="{ height: state.footerHeight + 'px' }"></el-footer>
            </el-container>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { _getBlogByUser } from "@/api/api.js";
import { useRouter, useRoute } from "vue-router";
import img from "../../../blog-end/public/static/defaultImg.png";
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

  homeLeftHeight: document.documentElement.clientHeight * 0.78,

  mainWidth: document.documentElement.clientWidth*0.997,
  mainWidthImg: document.documentElement.clientWidth,
  mainHeight: document.documentElement.clientHeight * 0.58,
  homeLeftFooterHeight: document.documentElement.clientWidth * 0.08
});
const userInfo = reactive({
  browsecount:0,
  likes:0,
  collection:0,
  count:0,
  originalCount:0
});

//菜单
const menus = computed(() => {
  let menuList = [];
  state.arr = router.options.routes.filter(item => {
    if (item.name === "home") {
      menuList = item.children;
    }
  });

  return menuList;
});

const handleResize = () => {
  state.hHeight = document.documentElement.clientHeight;
  state.width = document.documentElement.clientWidth;

  state.headerhHeight = document.documentElement.clientHeight * 0.22;
  state.contentRightWith = document.documentElement.clientWidth * 0.75;
  state.avatarBlock = document.documentElement.clientWidth * 0.22;

  state.homeLeftHeight = document.documentElement.clientHeight * 0.78;

  state.mainHeight = document.documentElement.clientHeight * 0.58;
  state.mainWidth = document.documentElement.clientWidth*0.997;
  mainWidthImg: document.documentElement.clientWidth,
  state.homeLeftFooterHeight = document.documentElement.clientHeight * 0.08;
};

/* 初始化方法*/
function init() {
  _getBlogByUser("1680851452956").then(res => {
    if(res.data.msg == "ok"){
      const bolgList = res.data.obj;
      console.log(bolgList)
      bolgList.forEach( item => {
        userInfo.browsecount += parseInt(item.browsecount)
        if(item.ispublic == "1" && item.issue == "1"){
          userInfo.count += 1
        }
        if(item.ispublic == "1" && item.issue == "1" && item.isoriginal == "原创"){
          userInfo.originalCount += 1
        }
      });
    }
  });
}

onMounted(() => {
  window.onresize = () => {
    handleResize();
  };
  init();
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
    padding: 0px 6px 6px 0px;

    .el-header-menu{
      padding: 0px 0px 0px 12px !important; 
    }
    .common-layout{
      padding: 0px 0px 0px 12px !important; 
    }

    .el-main {
      //overflow: hidden;
      //scrollbar-width: none;
      padding: 0px 0px 0px 0px;
    }

    /* 隐藏滚动条的CSS */
    .el-main::-webkit-scrollbar {
      display: none;
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