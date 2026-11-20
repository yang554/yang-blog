<template>
  <div class="kanban-container">
    <div class="kanban-column" v-for="column in columns" :key="column.id">
      <div class="kanban-column-header">
        {{ column.title }}
      </div>
      <div class="kanban-items">
        <div class="kanban-item" v-for="item in column.items" :key="item.id">
          {{ item.name }}
        </div>
      </div>
    </div>
  </div>
</template>
 
<script>
export default {
  data() {
    return {
      columns: [
        {
          id: 1,
          title: '待处理',
          items: [
            { id: 1, name: '任务1' },
            { id: 2, name: '任务2' },
          ],
        },
        {
          id: 2,
          title: '进行中',
          items: [
            { id: 3, name: '任务3' },
            { id: 4, name: '任务4' },
          ],
        },
        {
          id: 3,
          title: '已完成',
          items: [
            { id: 5, name: '任务5' },
            { id: 6, name: '任务6' },
          ],
        },
      ],
    };
  },
};
</script>
 
<style scoped>
.kanban-container {
  display: flex;
}
 
.kanban-column {
  margin-right: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 10px;
}
 
.kanban-column-header {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 5px 5px 0 0;
}
 
.kanban-items {
  padding: 10px;
  background-color: #fff;
  border-radius: 0 0 5px 5px;
}
 
.kanban-item {
  margin-bottom: 5px;
  padding: 5px;
  background-color: #f0f0f0;
  border-radius: 5px;
}
</style>