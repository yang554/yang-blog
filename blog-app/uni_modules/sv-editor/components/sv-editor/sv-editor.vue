<template>
  <view class="sv-editor-wrapper" @longpress="eLongpress">
    <slot name="header"></slot>
    <editor
      :id="eid"
      class="sv-editor-container"
      show-img-size
      show-img-toolbar
      show-img-resize
      :placeholder="placeholder"
      :read-only="isReadOnly"
      @statuschange="onStatusChange"
      @ready="onEditorReady"
      @input="onEditorInput"
      @focus="onEditorFocus"
      @blur="onEditorBlur"
    ></editor>
    <view class="maxlength-tip" v-if="maxlength > 0 && !hideMax">{{ textlength }}/{{ maxlength }}</view>
    <slot name="footer"></slot>
    <!-- renderjs辅助插件 -->
    <!-- #ifdef APP || H5 -->
    <sv-editor-render ref="editorRenderRef" :eid="editorEID"></sv-editor-render>
    <sv-editor-plugin ref="editorPluginRef" :sid="startID" :eid="editorEID" @epaste="ePaste"></sv-editor-plugin>
    <!-- #endif -->
  </view>
</template>

<script>
/**
 * sv-editor
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

import store from '../common/store.js'
import { linkFlag, copyrightPrint } from '../common/utils.js'
import { parseHtmlWithVideo, replaceVideoWithImageRender } from '../common/parse.js'
import SvEditorRender from './sv-editor-render.vue'
import SvEditorPlugin from '../plugins/sv-editor-plugin.vue'
import wxplugin from '../plugins/sv-editor-wxplugin.js'

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
    SvEditorRender,
    SvEditorPlugin
  },
  props: {
    // 编辑器id可传入，以便循环组件使用，防止id重复
    eid: {
      type: String,
      default: 'sv-editor' // 唯一，禁止重复
    },
    placeholder: {
      type: String,
      default: '写点什么吧 ~'
    },
    // 是否只读
    readOnly: {
      type: Boolean,
      default: false
    },
    // 最大字数限制，<=0时表示不限
    maxlength: {
      type: Number,
      default: -1
    },
    // 是否关闭最大字数显示
    hideMax: {
      type: Boolean,
      default: false
    },
    // 粘贴模式，可选 text 纯文本(默认) | origin 尽可能保持原格式
    pasteMode: {
      type: String,
      default: 'text'
    }
  },
  emits: ['ready', 'input', 'statuschange', 'focus', 'blur', 'overmax', 'epaste'],
  data() {
    return {
      textlength: 0, // 当前字数统计
      startID: '',
      // #ifdef VUE2
      // #ifdef MP-WEIXIN
      editorIns: null // 仅vue2环境下的微信小程序需要声明实例变量，否则报错，属实逆天
      // #endif
      // #endif
    }
  },
  computed: {
    editorEID: {
      set(newVal) {
        store.actions.setEID(newVal)
      },
      get() {
        return store.actions.getEID()
      }
    },
    editorCtx: {
      set(newVal) {
        store.actions.setEditor(newVal.eid, newVal.ctx)
        // #ifdef VUE2
        this.editorIns = newVal.ctx
        this.editorIns.id = newVal.eid
        // #endif
      },
      get() {
        let instance = store.actions.getEditor(this.eid)
        // #ifdef VUE2
        instance = store.actions.getEditor(this.eid) || this.editorIns
        // #endif
        return instance
      }
    },
    isReadOnly: {
      set(newVal) {
        store.actions.setReadOnly(newVal)
      },
      get() {
        return store.actions.getReadOnly()
      }
    }
  },
  watch: {
    readOnly(newVal) {
      this.isReadOnly = newVal
    }
  },
  mounted() {
    // 首个实例初始化时执行
    if (!store.state.firstInstanceFlag) {
      this.editorEID = this.eid
      store.state.firstInstanceFlag = this.eid
      copyrightPrint()
    }
  },
  destroyed() {
    store.actions.destroy()
  },
  unmounted() {
    store.actions.destroy()
  },
  methods: {
    onEditorReady() {
      this.$nextTick(() => {
        uni
          .createSelectorQuery()
          .in(this)
          .select('#' + this.eid)
          .context((res) => {
            // 存储上下文
            this.editorCtx = { eid: this.eid, ctx: res.context }
            // 挂载实例api
            this.bindMethods()
            // 初始化完成
            this.$emit('ready', this.editorCtx)

            // #ifdef APP || H5
            if (this.pasteMode == 'origin') this.editorCtx.changePasteMode('origin')
            // #endif
          })
          .exec()
      })
    },
    /**
     * 挂载实例api
     */
    bindMethods() {
      // ===== renderjs相关扩展api =====

      // #ifdef APP || H5
      /**
       * 主动聚焦
       * @returns {void}
       */
      this.editorCtx.focus = this.$refs.editorRenderRef.focus

      /**
       * 退格
       * @returns {void}
       */
      this.editorCtx.backspace = this.$refs.editorRenderRef.backspace

      /**
       * 键盘输入模式
       * @param {String} type 模式，可选：none | remove
       * @returns {void}
       */
      this.editorCtx.changeInputMode = this.$refs.editorRenderRef.changeInputMode

      /**
       * 粘贴模式
       * @param {String} type 模式，可选：text纯文本(默认) | origin尽可能保持原格式
       * @returns {void}
       */
      this.editorCtx.changePasteMode = (type) => {
        // 告知plugin启动
        this.startID = this.eid
        if (this.$refs.editorPluginRef?.changePasteMode) {
          this.$refs.editorPluginRef.changePasteMode(type)
        }
      }

      /**
       * 生成视频封面图
       * @param {String} url 封面图片地址
       * @returns {Promise} 携带播放图标的封面图地址
       */
      this.editorCtx.createCoverThumbnail = (url) => {
        return new Promise((resolve) => {
          if (this.$refs.editorPluginRef?.createCoverThumbnail) {
            // 事件名必须唯一，否则会覆盖
            uni.$once(`E_EDITOR_GET_COVER_THUMBNAIL_${url}`, (res) => {
              resolve(res.cover)
            })
            setTimeout(() => {
              this.$refs.editorPluginRef?.createCoverThumbnail(url)
            })
          }
        })
      }

      /**
       * 生成视频封面图
       * @param {String} url 视频地址
       * @returns {Promise} 封面图地址
       */
      this.editorCtx.createVideoThumbnail = (url) => {
        return new Promise((resolve) => {
          if (this.$refs.editorPluginRef?.createVideoThumbnail) {
            // 事件名必须唯一，否则会覆盖
            uni.$once(`E_EDITOR_GET_VIDEO_THUMBNAIL_${url}`, (res) => {
              resolve(res.cover)
            })
            setTimeout(() => {
              this.$refs.editorPluginRef?.createVideoThumbnail(url)
            })
          }
        })
      }

      // #endif

      // ===== 微信小程序扩展api =====

      // #ifdef MP-WEIXIN

      /**
       * 生成视频封面图
       * @param {String} url 视频地址
       * @returns {Promise} 封面图地址
       */
      this.editorCtx.createCoverThumbnail = wxplugin?.wxCreateCoverThumbnail

      // #endif

      // ===== 通用扩展api =====

      /**
       * 主动触发input回调事件
       * @returns {void}
       */
      this.editorCtx.changeInput = () => {
        this.editorCtx.getContents({
          success: (res) => {
            this.$emit('input', { ctx: this.editorCtx, html: res.html, text: res.text })
          }
        })
      }

      /**
       * 获取最新内容
       * @returns {Promise} 内容对象 { html, text... }
       */
      this.editorCtx.getLastContent = async () => {
        return new Promise((resolve) => {
          this.editorCtx.getContents({
            success: (res) => {
              resolve(res)
            }
          })
        })
      }

      /**
       * 富文本内容初始化
       * 注意：微信小程序会导致聚焦滚动，建议先将编辑器v-show=false，待initHtml内容初始化完成后再true
       * 也正是因为微信小程序端会聚焦滚动，所以editorEID在初始阶段会默认保持最后一个实例eid，需要手动重新聚焦
       * @param {String} html 初始化的富文本
       * @param {Function<Promise>} customCallback 自定义处理封面回调，需要以Promise形式返回封面图片资源
       * @returns {void}
       */
      this.editorCtx.initHtml = async (html, customCallback) => {
		  console.log('html',html)
        let transHtml = await replaceVideoWithImageRender(html, customCallback)
        // #ifdef APP || H5
        this.editorCtx.changePasteMode('text') // text模式下可以防止初始化时对格式的影响
        // #endif
        setTimeout(() => {
          this.editorCtx.setContents({
            html: transHtml,
            success: () => {
              // 主动触发一次input回调事件
              this.editorCtx.changeInput()
              // #ifdef APP || H5
              if (this.pasteMode == 'origin') this.editorCtx.changePasteMode('origin')
              // #endif
            }
          })
        })
      }

      /**
       * 导出处理
       * @param {String} html 要导出的富文本
       * @returns {String} 处理后的富文本
       */
      this.editorCtx.exportHtml = (html) => {
        return parseHtmlWithVideo(html)
      }
    },
    onEditorInput(e) {
      // 注意不要使用getContents获取html和text，会导致重复触发onStatusChange从而失去toolbar工具的高亮状态
      // 复制粘贴的时候detail会为空，此时应当直接return
      if (Object.keys(e.detail).length <= 0) return
      const { html, text } = e.detail

      // 识别到链接特殊标识立即return
      if (text.indexOf(linkFlag) !== -1) return

      /**
       * 因为uni-editor不提供最大字符限制，故需要手动进行以下特殊处理
       */
      const maxlength = parseInt(this.maxlength)
      const textStr = text.replace(/[ \t\r\n]/g, '')
      this.textlength = textStr.length // 当前字符数
      if (this.textlength >= maxlength && maxlength > 0) {
        this.textlength = maxlength // 因为editor特性，需要手动赋阈值
        if (!this.lockHtmlFlag) {
          this.lockHtml = html // 锁定最后一次超出字数前的html
          this.lockHtmlFlag = true // 锁定标志
          // 首次到达最大限制时还需最后回调一次input事件
          this.$emit('input', { ctx: this.editorCtx, html, text })
        } else {
          // 在超过字数时锁定，若再编辑则抛出超出事件
          this.$emit('overmax', { ctx: this.editorCtx })
        }
        // 超过字数时，锁定最后一次超出字数前的html
        this.editorCtx.setContents({ html: this.lockHtml })
      } else {
        // 正常输入
        this.$emit('input', { ctx: this.editorCtx, html, text })
        this.lockHtmlFlag = false // 锁定标志
      }
    },
    /**
     * 样式格式改变时触发
     * 注意：微信小程序端在多编辑器实例下切换编辑器后可能不会及时触发onStatusChange
     */
    onStatusChange(e) {
      store.actions.setFormats(e.detail)
      this.$emit('statuschange', { ...e, ctx: this.editorCtx })
      uni.$emit('E_EDITOR_STATUSCHANGE', { ...e, ctx: this.editorCtx })
    },
    onEditorFocus(e) {
      this.editorEID = this.eid
      this.$emit('focus', { ...e, ctx: this.editorCtx })
    },
    onEditorBlur(e) {
      this.$emit('blur', { ...e, ctx: this.editorCtx })
    },
    ePaste(e) {
      this.$emit('epaste', { ...e, ctx: this.editorCtx })
      uni.$emit('E_EDITOR_PASTE', { ...e, ctx: this.editorCtx })
    },
    /**
     * 微信小程序官方editor的长按事件有bug，需要重写覆盖，不需做任何逻辑，可见下面小程序社区问题链接
     * @tutorial https://developers.weixin.qq.com/community/develop/doc/000c04b3e1c1006f660065e4f61000
     */
    eLongpress() {}
  }
}
</script>

<style lang="scss">
.sv-editor-wrapper {
  --maxlength-text-color: #666666;

  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .sv-editor-container {
    flex: 1;
  }

  .maxlength-tip {
    position: absolute;
    bottom: 0;
    right: 0;
    font-size: 24rpx;
    color: var(--maxlength-text-color);
    opacity: 0.6;
  }
}

// placeholder字样
.sv-editor-container ::v-deep .ql-blank::before {
  font-style: normal;
  color: #cccccc;
}

// 图片工具样式
::v-deep .ql-container {
  min-height: unset;

  .ql-image-overlay {
    pointer-events: none;

    .ql-image-size {
      right: 28px !important;
    }
    .ql-image-toolbar {
      // 删除按钮
      pointer-events: auto;
    }
    .ql-image-handle {
      // 四角缩放按钮
      width: 30px;
      height: 30px;
      pointer-events: auto;
    }
  }
}
</style>
