<template>
	<text
		:eid="eid"
		:change:eid="quillEditor.watchEID"
		:mode="inputmode"
		:change:mode="quillEditor.watchInputMode"
		:focus="focusFlag"
		:change:focus="quillEditor.watchFocus"
		:backspace="backspaceFlag"
		:change:backspace="quillEditor.watchBackSpace"
	></text>
</template>

<script>
/**
 * 富文本renderjs扩展
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

export default {
	props: {
		eid: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			inputmode: '', // none | remove
			focusFlag: 0, // 主动聚焦标志
			backspaceFlag: 0 // 主动删除标志
		}
	},
	methods: {
		changeInputMode(mode) {
			this.inputmode = mode
		},
		focus() {
			this.focusFlag++
		},
		backspace() {
			this.backspaceFlag++
		}
	}
}
</script>

<script module="quillEditor" lang="renderjs">
export default {
  data() {
    return {
      editorID: ''
		}
  },
  methods: {
    watchEID(newValue, oldValue, ownerInstance, instance) {
      if (newValue) {
        this.editorID = newValue
      }
    },
		watchInputMode(newValue, oldValue, ownerInstance, instance) {
      if (newValue) {
        this.changeQuillInputMode(newValue)
      }
    },
    watchFocus(newValue, oldValue, ownerInstance, instance) {
      if (newValue) {
        this.changeFocus(newValue)
      }
    },
    watchBackSpace(newValue, oldValue, ownerInstance, instance) {
      if (newValue) {
        this.changeBackSpace(newValue)
      }
    },
    /**
     * 通过增加或移出inputmode属性来控制是否允许键盘弹出
     * @param {String} type none | remove
     * @tutorial https://ask.dcloud.net.cn/article/39915
     */
    changeQuillInputMode(type) {
      try {
        // 要关闭软键盘的话，需要给inputmode属性设置none
        // 如果要打开软键盘的话，需要移出inputmode属性
        const el = document.querySelector(`#${this.editorID} .ql-editor`);
        if(!el) return console.warn('==== quill dom error ====');
        if(type == 'none') el.setAttribute('inputmode', 'none')
        if(type == 'remove') el.removeAttribute('inputmode')
      } catch (err) {
        console.warn('==== changeQuillInputMode catch error :', err);
      }
    },
    /**
     * 通过quill节点实例的focus方法来主动触发编辑器聚焦
     */
    changeFocus() {
      try {
        const el = document.querySelector(`#${this.editorID} .ql-editor`);
        if(!el) return console.warn('==== quill dom error ====');
        el.focus()
      } catch (err) {
        console.warn('==== changeFocus catch error :', err);
      }
    },
    /**
     * 通过quill节点实例的deleteText方法来主动触发编辑器删除
     */
    changeBackSpace() {
      try {
        const el = document.querySelector(`#${this.editorID}`);
        const quill = Quill.find(el);
        if(!el || !quill) return console.warn('==== quill dom error ====');

        const range = quill.getSelection(); // 获取当前光标位置

        if (range && range.length === 0) {
          // 如果没有选中文本且光标存在，则删除前一个字符或 emoji
          if (range.index > 0) {
            // 获取光标前的所有文本
            const text = quill.getText(0, range.index);
            // 规范化 Unicode 字符，确保正确处理组合字符和 emoji
            const normalizedText = text.normalize('NFC');
            // 将文本转换为字符数组，确保正确处理多字节字符
            const chars = Array.from(normalizedText);
            // 计算前一个字符的索引
            const lastCharIndex = chars.length - 1;
            if (lastCharIndex >= 0) {
              // 删除前一个字符（包括多字节字符）
              const lastChar = chars[lastCharIndex];
              const lastCharLength = text.slice(-lastChar.length).length;
              quill.deleteText(range.index - lastCharLength, lastCharLength);
              quill.setSelection(range.index - lastCharLength); // 更新光标位置
            }
          }
        } else if (range && range.length > 0) {
          // 如果有选中文本，则删除选中的文本
          quill.deleteText(range.index, range.length);
          quill.setSelection(range.index); // 更新光标位置
        }

      } catch (err) {
        console.warn('==== changeBackSpace catch error :', err);
      }
    },

  }
}
</script>
