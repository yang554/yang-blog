<template>
  <view class="sv-editor-toolbar">
    <view class="editor-tools" @tap="onTool">
      <text
        v-for="item in allTools"
        :key="item.title"
        class="iconfont"
        :class="item.icon"
        :data-name="item.name"
      ></text>
      <!-- [展开/折叠] 为固定工具 -->
      <text v-if="isShowPanel" class="iconfont icon-xiajiantou" data-name="fold" data-value="0"></text>
      <text v-else class="iconfont icon-shangjiantou" data-name="fold" data-value="1"></text>
    </view>
    <!-- 样式面板 不建议使用 :key="item.name" 因为 name 可能重复 -->
    <view class="tool-panel" v-if="curTool == 'style' && isShowPanel">
      <view class="panel-grid panel-style">
        <view
          class="panel-style-item"
          :class="[(item.value ? formats[item.name] === item.value : formats[item.name]) ? 'ql-active' : '']"
          :style="{ color: item.name == 'color' ? curTextColor : item.name == 'backgroundColor' ? curBgColor : '' }"
          v-for="item in allStyleTools"
          :key="item.title"
          :title="item.title"
          :data-name="item.name"
          :data-value="item.value"
          @tap="onToolStyleItem"
        >
          <text class="iconfont pointer-events-none" :class="item.icon"></text>
          <text class="tool-item-title pointer-events-none">{{ item.title }}</text>
        </view>
      </view>
    </view>
    <!-- 表情面板 -->
    <view class="tool-panel" v-if="curTool == 'emoji' && isShowPanel">
      <view class="panel-grid panel-emoji">
        <view
          class="panel-emoji-item"
          v-for="item in allEmojiTools"
          :key="item"
          :data-name="item"
          @tap="onToolEmojiItem"
        >
          {{ item }}
        </view>
      </view>
      <!-- #ifdef H5 -->
      <view class="editor-backspace iconfont icon-tuige" @click="onBackSpace"></view>
      <!-- #endif -->
      <!-- #ifdef APP -->
      <view v-if="!isIOS" class="editor-backspace iconfont icon-tuige" @click="onBackSpace"></view>
      <!-- #endif -->
    </view>
    <!-- 更多功能面板 -->
    <view class="tool-panel" v-if="curTool == 'more' && isShowPanel">
      <view class="panel-grid panel-more">
        <view
          class="panel-more-item"
          v-for="item in allMoreTools"
          :key="item.title"
          :title="item.title"
          :data-name="item.name"
          :data-value="item.value"
          @tap="onToolMoreItem"
        >
          <view class="iconfont pointer-events-none" :class="item.icon"></view>
          <view class="panel-more-item-title pointer-events-none">{{ item.title }}</view>
        </view>
      </view>
    </view>
    <!-- 扩展面板 -->
    <view class="tool-panel" v-if="curTool == 'setting' && isShowPanel">
      <slot name="setting"></slot>
    </view>

    <!-- 弹窗 因vue2/3的v-model写法有区别，故需要条件编译，我也是醉了 -->
    <!-- #ifdef VUE3 -->
    <sv-editor-popup-more v-model:show="showMorePop" :tool-name="curMoreTool" @confirm="moreItemConfirm">
      <!-- APP端不支持循环插槽，此处建议挨个写 -->
      <!-- <template v-for="(slot, name) in $slots" #[name]="scope">
				<slot :name="name" v-bind="scope"></slot>
			</template> -->
      <template #at>
        <slot name="at"></slot>
      </template>
      <template #topic>
        <slot name="topic"></slot>
      </template>
    </sv-editor-popup-more>
    <!-- #endif -->

    <!-- 弹窗 特别是微信小程序端的vue2，必须使用.sync，服 -->
    <!-- #ifdef VUE2 -->
    <sv-editor-popup-more :show.sync="showMorePop" :tool-name="curMoreTool" @confirm="moreItemConfirm">
      <template #at>
        <slot name="at"></slot>
      </template>
      <template #topic>
        <slot name="topic"></slot>
      </template>
    </sv-editor-popup-more>
    <!-- #endif -->

    <!-- 调色板 -->
    <!-- #ifdef VUE3 -->
    <sv-editor-colorpicker
      v-model:show="showColorPicker"
      :type="colorType"
      :color="curColor"
      @confirm="selectColor"
    ></sv-editor-colorpicker>
    <!-- #endif -->
    <!-- #ifdef VUE2 -->
    <sv-editor-colorpicker
      :show.sync="showColorPicker"
      :type="colorType"
      :color="curColor"
      @confirm="selectColor"
    ></sv-editor-colorpicker>
    <!-- #endif -->
  </view>
</template>

<script>
/**
 * sv-editor 默认工具栏
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

import store from '../common/store.js'
import { toolList, emojiToolList, styleToolList, moreToolList } from '../common/tool-list.js'
import { noKeyboardEffect } from '../common/utils.js'
import SvEditorPopupMore from './sv-editor-popup-more.vue'
import SvEditorColorpicker from './sv-editor-colorpicker.vue'

export default {
  // #ifdef MP-WEIXIN
  // 微信小程序特殊配置
  options: {
    addGlobalClass: true,
    virtualHost: true,
    styleIsolation: 'shared'
  },
  // #endif
  components: {
    SvEditorPopupMore,
    SvEditorColorpicker
  },
  props: {
    // 工具栏列表
    tools: {
      type: Array,
      default: () => {
        return [] // 空默认为全列表
      }
    },
    // 样式工具列表
    styleTools: {
      type: Array,
      default: () => {
        return [] // 空默认为全列表
      }
    },
    // 更多功能列表
    moreTools: {
      type: Array,
      default: () => {
        return [] // 空默认为全列表
      }
    }
  },
  emits: [
    'toolMoreItem',
    'moreItemConfirm',
    'keyboardChange',
    'changeMorePop',
    'changeTool',
    'tapTool',
    'tapStyle',
    'tapEmoji',
    'backspace'
  ],
  data() {
    return {
      curTool: '', // 当前工具（头部工具栏）默认第一个
      showPanel: false, // 是否能显示工具面板，区别于isShowPanel
      showMorePop: false, // 是否弹出更多功能面板弹窗
      showColorPicker: false, // 是否弹出调色板
      curColor: '', // 当前颜色
      curTextColor: '', // 当前文字颜色暂存
      curBgColor: '', // 当前背景颜色暂存
      colorType: '', // 当前颜色模式，可选 color | backgroundColor
      curMoreTool: '', // 当前所选的更多功能项
      keyboardHeight: 0 // 键盘高度
    }
  },
  computed: {
    isIOS() {
      return uni.getSystemInfoSync().platform == 'ios'
    },
    allTools() {
      if (this.tools.length == 0) return toolList
      const indexMap = new Map(this.tools.map((item, index) => [item, index]))
      const filtered = toolList
        .filter((item) => indexMap.has(item.name)) // 过滤
        .sort((a, b) => indexMap.get(a.name) - indexMap.get(b.name)) // 排序
      return filtered
    },
    allStyleTools() {
      if (this.styleTools.length == 0) return styleToolList
      const indexMap = new Map(this.styleTools.map((item, index) => [item, index]))
      const filtered = styleToolList
        .filter((item) => indexMap.has(item.name)) // 过滤
        .sort((a, b) => indexMap.get(a.name) - indexMap.get(b.name)) // 排序
      return filtered
    },
    allEmojiTools() {
      return emojiToolList
    },
    allMoreTools() {
      if (this.moreTools.length == 0) return moreToolList
      const indexMap = new Map(this.moreTools.map((item, index) => [item, index]))
      const filtered = moreToolList
        .filter((item) => indexMap.has(item.name)) // 过滤
        .sort((a, b) => indexMap.get(a.name) - indexMap.get(b.name)) // 排序
      return filtered
    },
		/**
		 * 在微信小程序端的vue2环境下无法直接使用计算属性读取editorCtx
		 * 为了统一化，只在各自需要使用编辑器实例的地方，按需重新获取
		 */
    // editorCtx() {
    //   const eid = store.actions.getEID()
    //   return store.actions.getEditor(eid)
    // },
    formats() {
      return store.actions.getFormats()
    },
    isReadOnly: {
      set(newVal) {
        store.actions.setReadOnly(newVal)
      },
      get() {
        return store.actions.getReadOnly()
      }
    },
    isShowPanel() {
      let show = this.showPanel
      /**
       * 规则：
       * 1. 当键盘弹出时，必须折叠面板
       * 2. 当点击有面板的工具栏时，必须展开面板
       * 3. 展开工具栏时，可以点击fold进行展开折叠切换
       */
      if (this.keyboardHeight !== 0) {
        show = this.showMorePop ? true : false
      } else {
        if (!this.curTool) {
          show = false
        }
      }
      return show
    }
  },
  watch: {
    curTool(newVal) {
      this.$emit('changeTool', newVal)
    }
  },
  mounted() {
    this.curTool = this.allTools[0].name // 当前工具（头部工具栏）默认第一个

    uni.$on('E_EDITOR_STATUSCHANGE', (e) => {
      this.curTextColor = e.detail.color || ''
      this.curBgColor = e.detail.backgroundColor || ''
    })

    // #ifndef H5
    uni.onKeyboardHeightChange(this.keyboardChange)
    // #endif
  },
  destroyed() {
    // #ifndef H5
    uni.offKeyboardHeightChange(this.keyboardChange)
    // #endif
    uni.$off('E_EDITOR_STATUSCHANGE')
  },
  unmounted() {
    // #ifndef H5
    uni.offKeyboardHeightChange(this.keyboardChange)
    // #endif
    uni.$off('E_EDITOR_STATUSCHANGE')
  },
  methods: {
		getEditorCtx() {
			const eid = store.actions.getEID()
			return store.actions.getEditor(eid)
		},
    onTool(e) {
			this.editorCtx = this.getEditorCtx() // 按需重新获取编辑器实例
      if (!this.editorCtx) return console.warn('editor is null')
      const { name, value } = e.target.dataset

      this.$emit('tapTool', { name, value })

      switch (name) {
        case 'style':
        case 'emoji':
        case 'more':
        case 'setting':
          this.curTool = name
          this.showPanel = true
          break
        case 'undo':
          noKeyboardEffect(() => {
            this.editorCtx.undo()
          })
          break
        case 'redo':
          noKeyboardEffect(() => {
            this.editorCtx.redo()
          })
          break
        case 'fold':
          this.showPanel = value == '1' ? true : false
          break
      }
      // 点击toolbar需要主动聚焦
      // #ifdef H5
      noKeyboardEffect(() => {
        this.editorCtx.focus()
      })
      // #endif
      // #ifdef APP
      if (!this.isIOS) {
        noKeyboardEffect(() => {
          this.editorCtx.focus()
        })
      }
      // #endif
    },
    onToolStyleItem(e) {
      const { name, value } = e.target.dataset
      this.$emit('tapStyle', { name, value })

			this.editorCtx = this.getEditorCtx() // 按需重新获取编辑器实例
      switch (name) {
        case 'divider':
          // 分割线单独使用insertDivider处理
          noKeyboardEffect(() => {
            this.editorCtx.insertDivider()
          })
          break
        case 'color':
          this.colorType = name
          this.curColor = this.curTextColor
          this.showColorPicker = true
          break
        case 'backgroundColor':
          this.colorType = name
          this.curColor = this.curBgColor
          this.showColorPicker = true
          break
        case 'removeformat':
          // 清除当前选区的样式
          uni.showModal({
            title: '系统提示',
            content: '是否清除当前选区样式',
            success: ({ confirm }) => {
              if (confirm) {
                noKeyboardEffect(() => {
                  this.editorCtx.removeFormat()
                })
              }
            }
          })
          break
        case 'bold':
        case 'italic':
        case 'underline':
        case 'strike':
        case 'script':
          // 部分格式需要弹出键盘，若禁止弹出键盘，则会使格式丢失
          this.editorCtx.format(name, value)
          break
        default:
          noKeyboardEffect(() => {
            this.editorCtx.format(name, value)
          })
          break
      }
    },
    onToolEmojiItem(e) {
      const { name, value } = e.target.dataset

      this.$emit('tapEmoji', { name, value })

			this.editorCtx = this.getEditorCtx() // 按需重新获取编辑器实例
      noKeyboardEffect(() => {
        this.editorCtx.insertText({
          text: name
        })
      })
    },
    onToolMoreItem(e) {
      const { name, value } = e.target.dataset
      this.curMoreTool = name
      if (value == 'popup') this.openMorePop()
      this.$emit('toolMoreItem', { name, value })
    },
    moreItemConfirm(e) {
      this.$emit('moreItemConfirm', e)
    },
    // 打开内置更多功能弹窗
    openMorePop() {
      this.showMorePop = true
      this.$emit('changeMorePop', this.showMorePop)
    },
    // 关闭内置更多功能弹窗
    closeMorePop() {
      this.showMorePop = false
      this.$emit('changeMorePop', this.showMorePop)
    },
    /**
     * 键盘相关方法
     */
    keyboardChange(e) {
      this.keyboardHeight = e.height

      this.$emit('keyboardChange', e)

      if (this.showMorePop) return

      // #ifdef H5
      if (this.keyboardHeight > 0) {
        this.showPanel = false
      }
      // #endif

      // 可能存在秒闪的情况, 因此需要短暂延后判断
      const timerHandler = () => {
        if (this.timer) {
          // 清除已有的计时器
          clearTimeout(this.timer)
          this.timer = null
        }
        this.timer = setTimeout(() => {
          if (this.keyboardHeight > 0) {
            this.showPanel = false
          }
          this.timer = null
        }, 50)
      }

      // #ifdef APP
      if (this.isIOS) {
        timerHandler()
      } else {
        if (this.keyboardHeight > 0) {
          this.showPanel = false
        }
      }
      // #endif

      // #ifdef MP-WEIXIN
      timerHandler()
      // #endif
    },
    // 退格
    onBackSpace() {
      this.$emit('backspace')
      // #ifdef H5 || APP
			this.editorCtx = this.getEditorCtx() // 按需重新获取编辑器实例
      noKeyboardEffect(() => {
        this.editorCtx.backspace()
      })
      // #endif
    },
    // 调色板确认
    selectColor(color, type) {
      this.curColor = color
      this.showColorPicker = false
      if (type == 'color') {
        this.curTextColor = color
      } else {
        this.curBgColor = color
      }
      // 确认颜色选择后不要noKeyboardEffect取消键盘，会造成颜色格式丢失
			this.editorCtx = this.getEditorCtx() // 按需重新获取编辑器实例
      this.editorCtx.format(type, color)
    }
  }
}
</script>

<style lang="scss">
@import '../icons/iconfont.css';

.sv-editor-toolbar {
  --editor-toolbar-height: 88rpx;
  --editor-toolbar-bgcolor: #ffffff;
  --editor-toolbar-bordercolor: #eeeeee;
  --editor-toolbar-iconsize: 32rpx;
  --tool-panel-height: auto;
  --tool-panel-bgcolor: #ffffff;
  --tool-panel-max-height: 400rpx;
  --tool-style-columns: 3;
  --tool-style-iconsize: 32rpx;
  --tool-style-titlesize: 28rpx;
  --tool-emoji-columns: 8;
  --tool-more-columns: 4;
  --tool-more-iconsize: 60rpx;
  --tool-more-titlesize: 24rpx;
  --tool-item-bgcolor: #f1f1f1;
  --editor-backspace-bgcolor: #ffffff;
  --editor-backspace-shadow: 0 0 8px 6px rgba(0, 0, 0, 0.08);

  .editor-tools {
    width: 100%;
    height: var(--editor-toolbar-height);
    background-color: var(--editor-toolbar-bgcolor);
    border-top: 1rpx solid var(--editor-toolbar-bordercolor);
    border-bottom: 1rpx solid var(--editor-toolbar-bordercolor);
    display: flex;
    align-items: center;
    justify-content: space-around;
    box-sizing: border-box;

    .iconfont {
      width: 100%;
      height: 100%;
      font-size: var(--editor-toolbar-iconsize);
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .tool-panel {
    height: var(--tool-panel-height);
    max-height: var(--tool-panel-max-height);
    overflow: auto;
    padding: 30rpx;
    box-sizing: border-box;
    // position: relative;
    background-color: var(--tool-panel-bgcolor);

    .editor-backspace {
      width: 80rpx;
      height: 60rpx;
      position: absolute;
      bottom: 30rpx;
      right: 30rpx;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 50rpx;
      border-radius: 20rpx;
      background-color: var(--editor-backspace-bgcolor);
      box-shadow: var(--editor-backspace-shadow);

      &:active {
        opacity: 0.8;
        bottom: 32rpx;
        right: 32rpx;
      }
    }

    .panel-grid {
      width: 100%;
      display: grid;
      align-items: center; /* 垂直居中 */
      justify-items: center; /* 水平居中 */
      gap: 30rpx;
      box-sizing: border-box;

      &.panel-style {
        grid-template-columns: repeat(var(--tool-style-columns), 1fr);
      }

      &.panel-emoji {
        grid-template-columns: repeat(var(--tool-emoji-columns), 1fr);
      }

      &.panel-more {
        grid-template-columns: repeat(var(--tool-more-columns), 1fr);
      }
    }

    .panel-style-item {
      width: 100%;
      height: 80rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      background-color: var(--tool-item-bgcolor);
      padding: 0 20rpx;
      box-sizing: border-box;

      .tool-item-title {
        font-size: var(--tool-style-titlesize);
      }
      .iconfont {
        font-size: var(--tool-style-iconsize);
        margin-right: 10rpx;
      }
    }

    .panel-emoji-item {
    }

    .panel-more-item {
      width: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      background-color: var(--tool-item-bgcolor);
      padding: 20rpx;
      border-radius: 20rpx;
      box-sizing: border-box;

      &:active {
        opacity: 0.85;
      }

      .iconfont {
        font-size: var(--tool-more-iconsize);
      }
      .panel-more-item-title {
        font-size: var(--tool-more-titlesize);
        margin-top: 12rpx;
      }
    }
  }
}

.ql-active {
  color: #66ccff;
}
.pointer-events-none {
  pointer-events: none;
}
</style>
