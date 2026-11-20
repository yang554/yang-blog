/// <reference types="vite/client" />
declare module "*.vue" {
    import { DefineComponent } from "vue"
    const component: DefineComponent<{}, {}, any>
    export default component
}
declare module '*.svg' {
  const content: string
  export default content
}
declare module 'vue3-particles'
declare module '@/store/types'
declare module '@vue/tsconfig/tsconfig.dom.json'
declare module '*.svg'
declare module '*.png'
declare module '*.jpg'
declare module '*.jpeg'
declare module '*.gif'
declare module '*.webp'
declare module '*.bmp'
declare module '*.tiff'

declare module '*.ts'
declare module '*.js'

// 创建类型声明文件 src/types/echarts.d.ts
import type { ECharts } from 'echarts'

declare module 'vue' {
  interface ComponentCustomProperties {
    $echarts_l: (el: HTMLElement) => ECharts
  }
}
