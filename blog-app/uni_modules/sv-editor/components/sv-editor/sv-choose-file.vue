<template>
  <text :data="flag" :props="config" :change:data="fileManager.watchData" :change:props="fileManager.watchProps"></text>
</template>

<script>
/**
 * 文件选择 - APP端
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

export default {
  props: {
    /**
     * 配置项
     * @tutorial https://developer.mozilla.org/zh-CN/docs/Web/HTML/Element/input/file
     */
    config: {
      type: Object,
      default: () => {
        return {
          accept: `.doc,.docx,.xls,.xlsx,.pdf,.zip,.rar,
          application/msword,
          application/vnd.openxmlformats-officedocument.wordprocessingml.document,
          application/vnd.ms-excel,
          application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,
          application/pdf,
          application/zip,
          application/x-rar-compressed`,
          multiple: false
        }
      }
    }
  },
  data() {
    return {
      flag: 0 // 监听标志
    }
  },
  methods: {
    chooseFile() {
      this.flag++ // 修改监听标志
    },
    rawFile(file) {
      this.$emit('confirm', file)
    }
  }
}
</script>

<script module="fileManager" lang="renderjs">
import { base64ToPath } from '../common/file-handler.js';
export default {
  data() {
    return {
      configCopy: {}, // 跟随vue中props的配置
    }
  },
  methods: {
    watchData(newValue, oldValue, ownerInstance, instance) {
      if (newValue) {
        this.openFileManager()
      }
    },
    watchProps(newValue, oldValue, ownerInstance, instance) {
      if (newValue) {
        this.configCopy = newValue
      }
    },
    openFileManager() {
      try {
        const { accept, multiple } = this.configCopy
        // 创建文件选择器input
        let fileInput = document.createElement('input')
        fileInput.setAttribute('type', 'file')
        fileInput.setAttribute('accept', accept)
        // 注：是否多选不要直接赋值multiple，应当是为false时不添加multiple属性
        if(multiple) fileInput.setAttribute('multiple', multiple)
        fileInput.click()

        // 封装为Promise的FileReader读取文件
        const readFileAsDataURL = (file) => {
          return new Promise((resolve, reject) => {
            let reader = new FileReader();
            reader.readAsDataURL(file);

            reader.onload = async (event) => {
              const base64 = event.target.result
              const path = await base64ToPath(base64)
              resolve({
                name: file.name,
                type: file.type,
                size: file.size,
                base64,
                path
              });
            };
            reader.onerror = (error) => {
              reject(error);
            };
          });
        }

        fileInput.addEventListener('change', async (e) => {
          let files = e.target.files // 注：此处为FileList对象，并非常规数组

          let results = await Promise.all(
            // Array.from 方法可以将类数组对象转换为真正的数组
            Array.from(files).map(item => readFileAsDataURL(item))
          );

          // callMethod不支持流数据，无法直接传递文件流对象
          this.$ownerInstance.callMethod('rawFile', results)
        })
      } catch (err) {
        console.warn('==== openFileManager catch error :', err);
      }
    }
  }
}
</script>
