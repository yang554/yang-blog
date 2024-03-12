<script setup lang="ts">
import CarouselView from "@/components/CarouselView.vue"
import { ref, onMounted, onUnmounted, defineComponent, reactive, nextTick } from 'vue'

const state = reactive({
    hHeight: document.documentElement.clientHeight * 0.78,
    width: document.documentElement.clientWidth * 0.138,

    mainHeight: document.documentElement.clientHeight * 0.65,
    footerHeight: document.documentElement.clientHeight * 0.13,
})

const handleResize = () => {
    state.hHeight = document.documentElement.clientHeight * 0.7;
    state.width = document.documentElement.clientWidth * 0.138;

    state.mainHeight = document.documentElement.clientHeight * 0.65;
    state.footerHeight = document.documentElement.clientHeight * 0.13;
}

onMounted(() => {
    window.addEventListener('common-layout', handleResize)


})

onUnmounted(() => {
    window.removeEventListener('common-layout', handleResize)
})

</script>

<template>
    <div class="common-layout" :style="{ width: state.width + 'px', height: state.hHeight + 'px' }">
        <el-container class="left-home">
            <el-main :style="{ width: state.width + 'px', height: state.mainHeight + 'px' }">
                <CarouselView class="home-left-car" />
                <div style="height: 100px;background-color: blueviolet;">1111</div>
                <div style="height: 100px;background-color: blueviolet;">222</div>
                <div style="height: 100px;background-color: blueviolet;">333</div>
                <div style="height: 100px;background-color: blueviolet;">444</div>
                <div style="height: 100px;background-color: blueviolet;">555</div>
                <div style="height: 100px;background-color: blueviolet;">666</div>
            </el-main>
            <el-footer :style="{ height: state.footerHeight + 'px' }"></el-footer>
        </el-container>
    </div>
</template>

<style scoped>
.left-home {
    .el-main {
        padding: 0
    }

    .home-left-car {
        z-index: -1;
        /* position: fixed; */
    }

    .el-footer {
        /* height: 118px; */
        /* background-color: rgb(94, 61, 61); */
    }
}
</style>