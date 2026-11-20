<template>
  <view @touchmove.stop.prevent="moveStop">
    <view class="sv-editor-popup" v-if="showPop">
      <view class="editor-popup-header">
        <view class="header-left" @click="cancel">取消</view>
        <view class="header-title">{{ title }}</view>
        <view class="header-right" @click="confirm">确认</view>
      </view>
      <view class="sv-editor-popup-container">
        <!-- 添加图片 -->
        <view class="popup-image" v-if="toolName == 'image'">
          <view class="popup-form-input">
            <text class="form-label">网络图片</text>
            <input
              v-model="imageForm.link"
              type="text"
              class="form-input"
              placeholder="请输入图片地址"
            />
          </view>
          <view class="popup-form-input">
            <text class="form-label">本地图片</text>
            <button size="mini" class="form-button" @click="selectImage">选择文件</button>
            <view class="form-thumbnail">
              <image
                class="form-thumbnail-item form-thumbnail-image"
                v-for="(item, index) in imageForm.file"
                :key="item.path || item.tempFilePath"
                :src="item.path || item.tempFilePath"
                @click="deleteImage(index)"
              ></image>
            </view>
          </view>
        </view>
        <!-- 添加视频 -->
        <view class="popup-video" v-if="toolName == 'video'">
          <view class="popup-form-input">
            <text class="form-label">网络视频</text>
            <input
              v-model="videoForm.link"
              type="text"
              class="form-input"
              placeholder="请输入视频地址"
            />
          </view>
          <view class="popup-form-input">
            <text class="form-label">本地视频</text>
            <button size="mini" class="form-button" @click="selectVideo">选择文件</button>
            <view class="form-thumbnail" v-if="videoForm.file.tempFilePath">
              <view class="form-thumbnail-item form-thumbnail-icon" @click="deleteVideo">
                <text class="iconfont icon-video"></text>
              </view>
            </view>
          </view>
        </view>
        <!-- 添加链接 -->
        <view class="popup-link" v-if="toolName == 'link'">
          <view class="popup-form-input">
            <text class="form-label">链接地址</text>
            <input
              v-model="linkForm.link"
              type="text"
              class="form-input"
              placeholder="请输入链接地址 (必填)"
            />
          </view>
          <view class="popup-form-input">
            <text class="form-label">链接文本</text>
            <input
              v-model="linkForm.text"
              type="text"
              class="form-input"
              placeholder="请输入链接文本 (可选)"
            />
          </view>
        </view>
        <!-- 添加附件 -->
        <view class="popup-attachment" v-if="toolName == 'attachment'">
          <view class="popup-form-input">
            <text class="form-label">附件地址</text>
            <input
              v-model="attachmentForm.link"
              type="text"
              class="form-input"
              placeholder="请输入附件地址"
            />
          </view>
          <view class="popup-form-input">
            <text class="form-label">附件描述</text>
            <input
              v-model="attachmentForm.text"
              type="text"
              class="form-input"
              placeholder="请输入附件描述"
            />
          </view>
          <view class="popup-form-input">
            <text class="form-label">本地文件</text>
            <button size="mini" class="form-button" @click="selectAttachment">
              选择文件
            </button>
            <view class="form-thumbnail" v-if="attachmentForm.file.path">
              <view
                class="form-thumbnail-item form-thumbnail-icon"
                @click="deleteAttachment"
              >
                <text class="iconfont icon-huixingzhen"></text>
              </view>
            </view>
          </view>
        </view>
        <!-- 提及 -->
        <view class="popup-at" v-if="toolName == 'at'">
          <slot name="at"></slot>
        </view>
        <!-- 话题 -->
        <view class="popup-topic" v-if="toolName == 'topic'">
          <slot name="topic"></slot>
        </view>
      </view>
    </view>
    <view class="mask" v-if="showPop" @click.stop="onMask"></view>
    <!-- #ifdef APP -->
    <sv-choose-file ref="chooseFileRef" @confirm="selectAppFile"></sv-choose-file>
    <!-- #endif -->
  </view>
</template>

<script>
/**
 * 扩展工具面板弹窗
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

import { moreToolList } from '../common/tool-list.js'
import SvChooseFile from './sv-choose-file.vue'

export default {
  name: 'sv-editor-popup-more',
  // #ifdef MP-WEIXIN
  // 微信小程序特殊配置
  options: {
    addGlobalClass: true,
    virtualHost: true,
    styleIsolation: 'shared'
  },
  // #endif
  components: {
    SvChooseFile
  },
  props: {
    show: {
      type: Boolean,
      default: false
    },
    toolName: {
      type: [String, null],
      default: 'image'
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
      imageForm: {
        link: '',
        file: []
      },
      videoForm: {
        link: '',
        file: {}
      },
      linkForm: {
        link: '',
        text: ''
      },
      attachmentForm: {
        link: '',
        text: '',
        file: {}
      }
    }
  },
  computed: {
    showPop: {
      set(newVal) {
        this.$emit('update:show', newVal)
      },
      get() {
        return this.show
      }
    },
    title() {
      return moreToolList.find((item) => item.name == this.toolName)?.title
    }
  },
  methods: {
    // 阻止滑动穿透
    moveStop() {},
    open() {
      this.showPop = true
      this.$emit('open')
    },
    close() {
      this.showPop = false
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
      let params = {}
      params.name = this.toolName
      switch (this.toolName) {
        case 'image':
          Object.assign(params, this.imageForm)
          break
        case 'video':
          Object.assign(params, this.videoForm)
          break
        case 'link':
          Object.assign(params, this.linkForm)
          break
        case 'attachment':
          Object.assign(params, this.attachmentForm)
          break
      }
      this.$emit('confirm', params)
    },
    /**
     * 业务方法
     */
    // 选择图片
    selectImage() {
      // #ifdef APP || H5
      uni.chooseImage({
        count: 5, // 默认9，此处限制为5
        success: (res) => {
          this.imageForm.file = res.tempFiles
        },
        fail: () => {
          uni.showToast({
            title: '未授权访问相册权限，请授权后使用',
            icon: 'none'
          })
        }
      })
      // #endif

      // #ifdef MP-WEIXIN
      uni.chooseMedia({
        count: 5, // 默认9，此处限制为5
        mediaType: ['image'],
        success: (res) => {
          this.imageForm.file = res.tempFiles
        },
        fail: () => {
          uni.showToast({
            title: '未授权访问相册权限，请授权后使用',
            icon: 'none'
          })
        }
      })
      // #endif
    },
    // 删除指定图片
    deleteImage(index) {
      this.imageForm.file.splice(index, 1)
    },
    // 选择视频
    selectVideo() {
      uni.chooseVideo({
        sourceType: ['camera', 'album'],
        success: (res) => {
          this.videoForm.file = res
        },
        fail: () => {
          uni.showToast({
            title: '未授权访问媒体权限，请授权后使用',
            icon: 'none'
          })
        }
      })
    },
    // 删除选择的本地视频
    deleteVideo() {
      this.videoForm.file = {}
    },
    // 选择附件
    selectAttachment() {
      // #ifdef H5
      uni.chooseFile({
        count: 1, // 默认100，此处限制为1
        extension: ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.zip', '.rar'],
        success: (res) => {
          this.attachmentForm.file = res.tempFiles[0]
        },
        fail: () => {
          uni.showToast({
            title: '未授权访问文件权限，请授权后使用',
            icon: 'none'
          })
        }
      })
      // #endif

      // #ifdef APP
      this.$refs.chooseFileRef.chooseFile()
      // 选择文件完成后触发selectAppFile方法
      // #endif

      // #ifdef MP-WEIXIN
      wx.chooseMessageFile({
        count: 1, // 最多可以选择的文件个数，可以 0～100，此处限制为1
        type: 'file', // 可以选择除了图片和视频之外的其它的文件
        extension: ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.zip', '.rar'],
        success: (res) => {
          this.attachmentForm.file = res.tempFiles[0]
        },
        fail: () => {
          uni.showToast({
            title: '未授权访问文件权限，请授权后使用',
            icon: 'none'
          })
        }
      })
      // #endif
    },
    // 选择文件完成后触发
    selectAppFile(files) {
      this.attachmentForm.file = files[0]
    },
    // 删除指定附件
    deleteAttachment() {
      this.attachmentForm.file = {}
    }
  }
}
</script>

<style lang="scss">
@import '../icons/iconfont.css';

.sv-editor-popup {
  --editor-popup-radius: 30rpx 30rpx 0 0;
  --editor-popup-bgcolor: #ffffff;
  --editor-popup-confirm: #4d80f0;
  --editor-popup-cancel: #fa4350;
  --thumbnail-icon-bgcolor: #cccccc;
  --editor-popup-header-height: 50rpx;

  width: 100%;
  position: absolute;
  bottom: 0;
  z-index: 10000;
  border-radius: var(--editor-popup-radius);
  padding: 30rpx;
  padding-bottom: calc(30rpx + env(safe-area-inset-bottom));
  background-color: var(--editor-popup-bgcolor);
  box-sizing: border-box;

  .editor-popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
    box-sizing: border-box;
    height: var(--editor-popup-header-height);

    .header-left {
      color: var(--editor-popup-cancel);
    }
    .header-right {
      color: var(--editor-popup-confirm);
    }
  }

  .sv-editor-popup-container {
    box-sizing: border-box;

    .popup-form-input {
      display: flex;
      align-items: center;
      margin-bottom: 30rpx;
      box-sizing: border-box;

      .form-label {
        margin-right: 20rpx;
        flex-shrink: 0;
      }

      .form-input {
        flex: 1;
        padding: 12rpx;
        border: 1rpx solid #eeeeee;
        border-radius: 8rpx;
        line-height: unset;
        height: unset;
        min-height: unset;
        box-sizing: border-box;

        .uni-input-placeholder {
          color: #dddddd;
        }
      }

      .form-button {
        margin-left: unset;
        margin-right: unset;
      }

      .form-thumbnail {
        .form-thumbnail-item {
          width: 25px;
          height: 25px;
          margin-left: 20rpx;
          position: relative;
          border: 1rpx solid #eeeeee;

          &:active {
            border-color: #d83b01;

            &::after {
              content: 'X';
              font-size: 25px;
              font-weight: bold;
              color: #d83b01;
              width: 100%;
              height: 100%;
              position: absolute;
              top: 0;
              left: 0;
              display: flex;
              align-items: center;
              justify-content: center;
            }
          }
        }

        .form-thumbnail-image {
          vertical-align: bottom; // 取消image标签底部留白
        }

        .form-thumbnail-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: var(--form-thumbnail-icon-bgcolor);
        }
      }
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
