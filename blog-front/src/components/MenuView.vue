<template>
    <div class="h-6" />
    <el-menu
      :default-active="activeIndex2"
      class="el-menu-demo"
      mode="horizontal"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      @select="handleSelect"
    >
      <el-menu-item index="1">最近</el-menu-item>
      <el-menu-item index="2">文章</el-menu-item>
      <el-menu-item index="3">资源</el-menu-item>
      <el-menu-item index="4">问答</el-menu-item>
      <el-menu-item index="5">收藏</el-menu-item>
      <el-menu-item index="5">族谱</el-menu-item>
      <el-menu-item index="5">经期</el-menu-item>
      <el-menu-item index="5">事件</el-menu-item>
      <el-menu-item index="6">设置</el-menu-item>
      <el-menu-item index="7">其他</el-menu-item>
    </el-menu>
  </template>
  
  <script lang="ts" setup>
  import { ref } from 'vue'
  
  const activeIndex = ref('1')
  const activeIndex2 = ref('1')
  const handleSelect = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
  }
  </script>
  