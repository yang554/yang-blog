<script setup lang="ts">
import TextScrollView from "@/components/TextScrollView.vue"
import { ref, onMounted, onUnmounted, defineComponent, reactive, nextTick } from 'vue'

const state = reactive({
  hHeight: document.documentElement.clientHeight * 0.22,
  width: document.documentElement.clientWidth ,
  avatarBlock: document.documentElement.clientWidth * 0.22,
  contentRightWith:document.documentElement.clientWidth * 0.75,
})

const handleResize = () => {
  state.hHeight = document.documentElement.clientHeight * 0.22;
  state.width = document.documentElement.clientWidth * 0.97;

  state.avatarBlock = document.documentElement.clientWidth * 0.22;
  state.contentRightWith = document.documentElement.clientWidth * 0.75;
}

onMounted(() => {
  window.addEventListener('home', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('home', handleResize)
})

</script>

<template>
    <div class="home" :style="{ width: state.width + 'px', height: state.hHeight + 'px' }">
        <!-- <img class="img" :style="{ width: state.width + 'px', height: state.hHeight + 'px' }" src="@/assets/top-bg.png" /> -->
        <div class="avatar-block" :style="{ width: state.avatarBlock + 'px',height: state.hHeight + 'px'}">
            <TextScrollView class="block-left" />
            <div class="block-right">
                <el-avatar :size="130" src="../assets/defaultImg.png" />
            </div>
        </div>
        <div class="content-right" :style="{ width: state.contentRightWith + 'px',height: state.hHeight + 'px'}">
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
    </div>
</template>

<style lang="scss" scoped>
.img {
  z-index: -1;
  background-repeat: no-repeat;
  background-position: center center;
  position: fixed;
}
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
}</style>