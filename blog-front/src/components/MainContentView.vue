<template>
    <div class="common-layout">
        <el-container class="left-home">
            <el-header>
                <el-menu class="el-menu-demo" mode="horizontal" background-color="#545c64" text-color="#fff"
                    active-text-color="#ffd04b" :default-active="sidebarActiveIndex" router>
                    <el-menu-item :index="item.path" v-for="(item, index) in menus" :key="index">
                        <i :class="item.myIcon ? item.myIcon : item.myIcon"></i>
                        <span>{{ item.meta.title }}</span>
                    </el-menu-item>
                </el-menu>
            </el-header>
            <el-main>
                <router-view />
                <!-- <component :is="currentView" /> -->
            </el-main>
            <el-footer></el-footer>
        </el-container>
    </div>
</template>
<script>
import { ref, computed } from 'vue'
const currentPath = ref(window.location.hash)

window.addEventListener('hashchange', () => {
  currentPath.value = window.location.hash
})

const currentView = computed(() => {
  return this.$routes[currentPath.value.slice(1) || '/'] || null
})

export default {
    name: 'LeftView',
    props: {

    },
    components: {
    },
    data() {
        return {
            sidebarActiveIndex: "/home",
        };
    },
    computed: {
        menus() {
            let arr = [''];
            arr = this.$router.options.routes.filter(item => {
                if (item.meta.hidden == undefined || item.meta.hidden == false) {
                    return true
                }
            })
            console.log(arr);
            return arr;
        }
    },
    created() {
    },
    watch: {
        $route(route) {
            this.sidebarActiveIndex = route.path;
        }
    },
}
</script>
  
<style scoped>
.left-home {
    /* width: 100%;
    height: calc(100vh - 230px); */

    .el-header {
        padding: 0px;
    }

    .el-main {
        /* background-color: rgb(255, 2, 2); */
        padding: 0px;
        margin-top: 0px;
        padding-top: 0px;
        /* padding-left: 15px;
        padding-top: 2px;
        padding-right: 8px; */
    }

    .el-footer {
        height: 120px;
        /* background-color: rgb(94, 61, 61); */
    }
}
</style>  