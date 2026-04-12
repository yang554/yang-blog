<template>
  <view @touchmove.stop.prevent="moveStop">
    <view class="sv-editor-colorpicker" v-if="showPicker">
      <view class="editor-popup-header">
        <!-- <view class="header-left" @click="cancel">取消</view> -->
        <view class="header-left" @click="reset">重置</view>
        <view class="header-title" :style="{ backgroundColor: selectColor }" v-if="selectColor">{{ selectColor }}</view>
        <view class="header-right" @click="confirm">确认</view>
      </view>
      <view class="sv-editor-colorpicker-container">
        <view
          v-for="item in allColors"
          :key="item"
          class="color-item"
          :style="{ backgroundColor: item }"
          @click="onSelect(item)"
        ></view>
      </view>
    </view>
    <view class="mask" v-if="showPicker" @click.stop="onMask"></view>
  </view>
</template>

<script>
import { colorList } from '../common/tool-list'

export default {
  name: 'sv-editor-colorpicker',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    color: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'color'
    },
    // 点击遮罩层自动关闭弹窗
    maskClose: {
      type: Boolean,
      default: true
    }
  },
  emits: ['update:show', 'open', 'close', 'onMask', 'cancel', 'confirm'],
  // #ifdef VUE2
  model: {
    prop: 'show',
    event: 'update:show'
  },
  // #endif
  data() {
    return {
      selectColor: this.color
    }
  },
  watch: {
    color(newVal) {
      this.selectColor = newVal
    }
  },
  computed: {
    showPicker: {
      set(newVal) {
        this.$emit('update:show', newVal)
      },
      get() {
        return this.show
      }
    },
    allColors() {
      return colorList
    }
  },
  methods: {
    // 阻止滑动穿透
    moveStop() {},
    open() {
      this.showPicker = true
      this.$emit('open')
    },
    close() {
      this.showPicker = false
      this.$emit('close')
    },
    onMask() {
      if (this.maskClose) this.close()
      this.$emit('onMask')
    },
    cancel() {
      this.$emit('cancel')
      this.close()
    },
    confirm() {
      this.$emit('confirm', this.selectColor, this.type)
    },
    reset() {
      this.selectColor = ''
    },
    onSelect(e) {
      this.selectColor = e
    }
  }
}
</script>

<style lang="scss">
.sv-editor-colorpicker {
  --editor-colorpicker-bgcolor: #ffffff;
  --editor-colorpicker-radius: 30rpx 30rpx 0 0;
  --editor-colorpicker-confirm: #4d80f0;
  --editor-colorpicker-cancel: #fa4350;
  --editor-colorpicker-header-height: 50rpx;

  width: 100%;
  position: absolute;
  bottom: 0;
  z-index: 10000;
  border-radius: var(--editor-colorpicker-radius);
  padding: 30rpx;
  padding-bottom: calc(30rpx + env(safe-area-inset-bottom));
  background-color: var(--editor-colorpicker-bgcolor);
  box-sizing: border-box;

  .editor-popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    box-sizing: border-box;
    height: var(--editor-colorpicker-header-height);

    .header-left {
      color: var(--editor-colorpicker-cancel);
    }
    .header-title {
      color: #000000;
      text-shadow: 1rpx 1rpx #ffffff, -1rpx 1rpx #ffffff, 1rpx -1rpx #ffffff, -1rpx -1rpx #ffffff;
      padding: 4rpx 12rpx;
      box-shadow: 0 0 8rpx #cccccc;
      border-radius: 10rpx;
    }
    .header-right {
      color: var(--editor-colorpicker-confirm);
    }
  }

  .sv-editor-colorpicker-container {
    // max-height: 40vh;
    overflow: auto;
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    align-items: center; /* 垂直居中 */
    justify-items: center; /* 水平居中 */
    gap: 20rpx;
    box-sizing: border-box;

    .color-item {
      width: 100%;
      height: 60rpx;
      box-shadow: 0 0 8rpx #ccc;
      border-radius: 10rpx;
    }
  }
}
.mask {
  width: 100%;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>
