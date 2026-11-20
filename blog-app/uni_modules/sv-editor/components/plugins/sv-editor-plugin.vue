<template></template>

<script>
/**
 * 富文本plugin特殊扩展
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-14
 */

import config from '../common/config'
import { noAuthorization } from '../common/utils'

export default {
  props: {
    sid: {
      type: String,
      default: ''
    },
    eid: {
      type: String,
      default: ''
    }
  },
  data() {
    return {}
  },
  mounted() {},
  methods: {
    changePasteMode(e) {
      return e
    },
    editorPaste(e) {
      this.$emit('epaste', e)
    },
    createVideoThumbnail(url) {
      noAuthorization('createVideoThumbnail')
      this.getVideoThumbnail(url)
    },
    getVideoThumbnail(e) {
      uni.$emit(`E_EDITOR_GET_VIDEO_THUMBNAIL_${e}`, config.video_thumbnail)
    },
    createCoverThumbnail(url) {
      noAuthorization('createCoverThumbnail')
      this.getCoverThumbnail(url)
    },
    getCoverThumbnail(e) {
      uni.$emit(`E_EDITOR_GET_COVER_THUMBNAIL_${e}`, e)
    }
  }
}
</script>
