<template>
  <div class="all-blog-view" v-loading="loading">
    <!--搜索-->
    <el-row style="margin-top: 5px;margin-left:5px;">
      <el-col :span="8">
        <el-input placeholder="按照博客标题搜索" v-model="inputSearchKeyWord">
          <template #prefix>
            <el-icon class="el-input__icon">
              <search />
            </el-icon>
          </template>
          <template #append>
            <el-button type="success" @click="searchByTitle">搜索</el-button>
          </template>
        </el-input>
      </el-col>
    </el-row>

    <el-table
      v-loading="pagination.loading"
      :data="pagination.currentTableData"
      style="width: 100%"
      :height="state.tabHeight + 'px'"
    >
      <el-table-column style="width: 100%">
        <template #default="scope">
          <el-card
            style="cursor: pointer;"
            class="box-card"
            @click="handlePreviewContent(scope.$index, scope.row)"
          >
            <el-row type="flex" :gutter="10">
              <el-col :span="6">
                <el-image
                  style="width: 100%;height: 180px;border: 1px solid #eee;"
                  :src="scope.row.cover"
                  fit="cover"
                ></el-image>
              </el-col>

              <el-col :span="18">
                <el-row style="font-size: 22px;margin: 10px">
                  <span style="font-weight: bold;">{{ scope.row.title }}</span>
                </el-row>
                <el-row style="font-size: 16px;margin: 10px">{{ scope.row.description }}</el-row>

                <div style="margin-top: 15px;margin: 10px;font-size: 10px;">
                  <div style="margin-right: 20px;">
                    <i style="margin: 5px;" class="iconfont icon-zuozhe">{{ scope.row.uName }}</i>
                    <i style="margin: 5px;" class="iconfont icon-shuben1">{{ scope.row.tName }}</i>
                    <i
                      style="margin: 5px;"
                      class="iconfont icon-31shijian"
                    >{{ scope.row.createtime }}</i>
                  </div>
                </div>

                <div style="margin-top: 15px;margin: 10px">
                  <div style="margin-right: 10px;">
                    <el-tag style="margin: 5px;" v-if="scope.row.issue">
                      {{
                      scope.row.isoriginal }}
                    </el-tag>

                    <i class="iconfont" style="margin: 5px;">
                      <el-tag
                        style="margin-left: 5px;"
                        v-for="tag in scope.row.blogTags.t"
                        :type="tag.color"
                      >{{ tag.name }}</el-tag>
                    </i>
                  </div>
                </div>
                <div style="margin-top: 15px;margin: 10px;display:flex" class="my-flex">
                  <div style="margin-right: 20px;">
                    <i style="margin: 5px;" class="iconfont icon-yanjing_xianshi">
                      {{
                      scope.row.browsecount }}
                    </i>
                  </div>
                  <div style="margin-right: 20px;">
                    <i class="iconfont icon-dianzan_huaban">{{ scope.row.likes }}</i>
                  </div>
                  <div style="margin-right: 20px;">
                    <i class="iconfont icon-31shoucang">{{ scope.row.collection }}</i>
                  </div>
                  <div class="my-flex-span1" style="margin-right: 20px;"></div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </template>
      </el-table-column>
    </el-table>

    <!--dialog预览博客内容-->
    <el-dialog :title="currentBlog.title" v-model="previewBlogDialogVisible">
      <mavon-editor
        v-model="currentBlog.content"
        defaultOpen="preview"
        :boxShadow="false"
        style="z-index:1;height:70vh"
        :editable="false"
        :subfield="false"
        :toolbarsFlag="false"
      ></mavon-editor>
    </el-dialog>

    <!-- 分页 -->
    <div style="margin-top: 5px;margin-left:5px;" class="my-pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="pagination.pageSizes"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.totalBlogs"
      ></el-pagination>
    </div>
  </div>
</template>
<script setup lang="ts">
import { _getBlog, _delBlog, _delBlogH, _getBlogByTile } from "@/api/api.js";
import { ref, onMounted, defineProps, reactive, onUnmounted } from "vue";
import { formatDate } from "@/utils/formatDate";
const props = defineProps({
  mainHeight: {
    type: Number
  }
});
let previewBlogDialogVisible = ref(false);
let currentBlog = ref({});
const inputSearchKeyWord = ref("");
const pagination = reactive({
  listData: [], //微博总data
  currentPage: 1, //当前页
  pageSizes: [5, 10, 15, 20],
  pageSize: 5, //每页显示条数
  totalPage: 1, //总页数
  currentTableData: [], //当前页data
  totalBlogs: 0, //总数量
  loading: false //table加载样式是否显示
});
const loading = ref(true);
const baseUrl = ref("/blog/getBlogByPage?isdel=0&issue=1&ispublic=1");
const baseLikeUrl = ref("/blog/getBlogByPageTitle?isdel=0&issue=1&ispublic=1");
/* 初始化方法*/
function initBlogs() {
  //获取所有博客内容
  _getBlog(baseUrl.value).then(res => {
    // console.log(res);
    if (res.data.status === 200) {
      pagination.totalBlogs = Number(res.data.obj.length);
      pagination.listData = res.data.obj;
      pagination.loading = true;
      pagination.totalPage = Math.ceil(
        pagination.listData.length / pagination.pageSize
      );
      pagination.totalPage =
        pagination.totalPage == 0 ? 1 : pagination.totalPage;

      getCurrentPageData(
        pagination.listData,
        pagination.currentPage,
        pagination.pageSize
      );

      pagination.loading = false;
    } else {
      pagination.loading = false;
      //   this.$message.warning(res.data.msg);
    }
  });
}
/* 预览博客内容*/
function handlePreviewContent(index, row) {
  currentBlog = row;
  previewBlogDialogVisible.value = true;

  console.log(currentBlog.content);
}
/* 根据标题栏搜索*/
function searchByTitle() {
  //根据博客标题关键字搜索
  _getBlogByTile(baseLikeUrl.value + "&title=" + inputSearchKeyWord.value).then(
    res => {
        console.log(res)
      if (res.data.status === 200) {
        pagination.totalBlogs = Number(res.data.obj.length);
        pagination.listData = res.data.obj;
        pagination.loading = true;
        pagination.totalPage = Math.ceil(
          pagination.listData.length / pagination.pageSize
        );
        pagination.totalPage =
          pagination.totalPage == 0 ? 1 : pagination.totalPage;

        getCurrentPageData(
          pagination.listData,
          pagination.currentPage,
          pagination.pageSize
        );

        pagination.loading = false;
      } else {
        pagination.loading = false;
      }
    }
  );
}
/*分页*/
function getCurrentPageData(listData, currentPage, pageSize) {
  let begin = (currentPage - 1) * pageSize;
  let end = currentPage * pageSize;
  pagination.currentTableData = listData.slice(begin, end);
}

/*分页事件*/
function handleSizeChange(val) {
  pagination.pageSize = val;
  getCurrentPageData(
    pagination.listData,
    pagination.currentPage,
    pagination.pageSize
  );
}

function handleCurrentChange(val) {
  pagination.currentPage = val;
  getCurrentPageData(
    pagination.listData,
    pagination.currentPage,
    pagination.pageSize
  );
}
const state = reactive({
  tabHeight: props.mainHeight - 80
});

const handleResize = () => {
  state.tabHeight = document.documentElement.clientHeight * 0.55 - 80;
  console.log("tabHeight", state.tabHeight);
};

onMounted(() => {
  window.onresize = () => {
    handleResize();
  };
  initBlogs();
  loading.value = false;
});

onUnmounted(() => {
  window.onresize = null;
});
</script>
<style lang="scss" scoped>
.my-pagination {
  float: left;
}
</style>