<template>
    <div class="block">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="currentPage4" :page-sizes="[100, 200, 300, 400]" :page-size="100"
            layout="total, sizes, prev, pager, next, jumper" :total="400">
        </el-pagination>
    </div>
</template>

<script>
export default {
    name: "Pagination",
    components: {},
    data() {
        return {
            currentPage: 1,
            pagSize: 10,
            pagSizeArr: [5, 10, 15, 20],
            totalSize: 0
        }
    }
}
</script>