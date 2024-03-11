<template>
    <div class="block text-center">
        <el-carousel style="height: 200px;">
            <el-carousel-item v-for="item in this.imgs" :key="item">
                <img style="height: 200px;" :src="item" />
            </el-carousel-item>
        </el-carousel>
    </div>
</template>
<script>
import a from "../assets/calendar/1666794911461.png";
import b from "../assets/calendar/1666794830957.png";
import c from "../assets/calendar/1666794890983.png";
import d from "../assets/calendar/1666794906748.png";
import e from "../assets/calendar/1666794890983.png";

export default {
    name: 'CarouselView',
    data() {
        return {
            imgs: [a, b, c, d, e]
        };
    },
    methods: {

    }

}
</script>

<style >
.el-header {
    padding: 0;
}
</style>
  