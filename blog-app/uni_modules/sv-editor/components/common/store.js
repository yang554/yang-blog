/**
 * 插件内全局状态管理
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

// #ifdef VUE3
import { reactive } from 'vue';
// #endif

// #ifdef VUE2
import Vue from 'vue';
// #endif

// 定义state状态
let state = null

// #ifdef VUE3
// 定义响应式状态
state = reactive({
  curEID: '',
  formats: {},
  isReadOnly: false,
  firstInstanceFlag: '' // 首次实例化标志，禁止手动更改
})
// #endif

// #ifdef VUE2
// 定义响应式状态
state = Vue.observable({
  curEID: '',
  formats: {},
  isReadOnly: false,
  firstInstanceFlag: '' // 首次实例化标志，禁止手动更改
})
// #endif

// 定义方法
function getEditor(eid) {
  return state[`${eid}-ctx`];
};

function setEditor(eid, ctx) {
  state[`${eid}-ctx`] = ctx
  // #ifdef MP-WEIXIN
  state[`${eid}-ctx`].id = eid
  // #endif
}

function getEID() {
  return state.curEID
};

function setEID(eid) {
  state.curEID = eid
}

function getFormats() {
  return state.formats
}

function setFormats(formats) {
  state.formats = formats
}

function getReadOnly() {
  return state.isReadOnly
}

function setReadOnly(readOnly) {
  state.isReadOnly = readOnly
}

// 销毁：重置所有状态
function destroy() {
  // state = {} // 禁止直接赋值，会导致响应式失效
  state.curEID = ''
  state.formats = {}
  state.isReadOnly = false
  state.firstInstanceFlag = '' // 首次实例化标志，禁止手动更改
  // 删掉其他属性
  for (let key in state) {
    if (key !== 'curEID' && key !== 'formats' && key !== 'isReadOnly' && key !== 'firstInstanceFlag') {
      delete state[key]
    }
  }
}

// 定义options对象
const options = {
  state,
  actions: {
    getEditor,
    setEditor,
    getEID,
    setEID,
    getFormats,
    setFormats,
    getReadOnly,
    setReadOnly,
    destroy
  }
}

// 导出 
export default options