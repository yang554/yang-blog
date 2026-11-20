## 基于官方 uni-editor 的富文本编辑器 [sv-editor]

### 一、前言
首先，你需要了解 uni-editor 相关注意事项，以及api

传送门：

1. [editor 组件概况](https://uniapp.dcloud.net.cn/component/editor.html)
2. [editorContext api详情](https://uniapp.dcloud.net.cn/api/media/editor-context.html)
3. 仔细阅读 [HTML 标签和 style 内联样式支持情况](https://uniapp.dcloud.net.cn/component/editor.html#html-%E6%A0%87%E7%AD%BE%E5%92%8C-style-%E5%86%85%E8%81%94%E6%A0%B7%E5%BC%8F%E6%94%AF%E6%8C%81%E6%83%85%E5%86%B5)
4. 仔细了解 [注意事项](https://uniapp.dcloud.net.cn/component/editor.html#%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)


### 二、本插件在官方 uni-editor 基础上做了什么

1. 提供插入视频的api
2. 提供插入链接的api
3. 在插入链接的基础上扩展了 @某人、#话题#、以及 添加附件 的api
4. 支持插入emoji表情包，可自定义表情包面板
5. 解决了在app端插入内容后，编辑器聚焦后自动弹出键盘的问题，提供api可在聚焦的同时取消键盘反复弹出带来的影响！！！
6. 工具栏toolbar与编辑器editor分离式写法，让你的代码更加自由
7. 插件内部大部分样式由css变量控制，更方便你使用样式穿透去自定义，对有暗黑主题的需求更加友好
8. 所有组件添加了 styleIsolation: 'shared' 配置项，再也不用怕小程序端的样式隔离穿透不了
9. 部分扩展基于renderjs，因此小程序端无法使用，可见下列关键功能概况详情
10. App与H5端关键扩展api如下：
	- noKeyboardEffect：取消键盘影响，不想让富文本聚焦后总是自动弹出键盘？这个api可以完美解决你的问题
	- focus：主动聚焦，你可以直接通过 editorCtx 实例调用此api，以便直接主动使富文本聚焦
	- backspace：主动退格(删除)，希望可以模拟键盘上的退格键？这个api如同键盘的 backspace 键一样，删除光标前一个单位，或者删除所选区域
	- 等等其他api，详见下文

### 三、兼容性

✅已兼容，❌未兼容 

|  VUE2	|  VUE3	|  APP(Android)	| APP(iOS)|  H5						|  微信小程序	|  其他小程序			|
|  :---:|  :---:|     :---:			|  :---:	|  :---:				|  :---:			|  :---:					|
|  ✅ ️|  ✅️	|       ✅				|  ✅			|  ✅       ️️	|    ✅️️		|  ❌(没测过)️️	|

1. 实际请以真机效果为准，并不能保证所有机型都兼容，如遇到问题还请加群讨论
2. 注意：因为部分api基于renderjs，而小程序无法使用renderjs，故部分api和功能并不适配小程序，更多详情会在各api中说明
3. 特别注意：**在微信小程序中，生成的 a 标签的 href 属性会被自动抹去，因此在微信小程序中是无法点击超链接跳转的，这点目前微信小程序官方固件不支持，暂时也没啥好的办法**

### 四、关键功能概况

✅完美支持，☑可用但或有副作用，❌不支持

|  功能																			|  VUE2	|  VUE3	|  H5		|  APP(Android)	|  APP(iOS)	|  微信小程序	|
|  :---:																		|  :---:|  :---:|  :---:|  :---:				|  :---:		|  :---:		|
|  插入图片																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|  ✅				|
|  插入视频																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|  ✅				|
|  插入链接																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|   ☑️			|
|  插入提及																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|   ☑️			|
|  插入话题																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|   ☑️			|
|  插入附件																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|   ☑️			|
|  主动聚焦																	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|   ❌️			|
|  主动退格																	|  ✅		|  ✅		|  ✅		|  ✅						|  ❌				|  ❌				|
|  多编辑器实例															|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|  ✅				|
|  消除键盘影响															|  ✅		|  ✅		|  ✅		|  ✅						|  ☑				|   ☑️			|
|  粘贴保留格式 <br/> [(*特殊扩展)](#特殊扩展)	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|  ❌				|
|  粘贴事件监听 <br/> [(*特殊扩展)](#特殊扩展)	|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|  ❌				|
|  视频截取封面															|  ✅		|  ✅		|  ✅		|  ✅						|  ☑️			|  ☑				|
|  视频回显解析															|  ✅		|  ✅		|  ✅		|  ✅						|  ✅				|  ✅				|
|  待补充 ...																|				|				|				|								|						|						|


### 五、开始

1. 因为本插件不依赖其他第三方插件，因此直接点击右上角 `下载并导入HBuilderX` 导入至你的项目中即可
2. 强烈建议你先 `使用HBuilderX导入示例项目` ，跑一下示例看亿下先，部分写法可以直接抄示例
3. 因为本插件提供除 [editorContext](https://uniapp.dcloud.net.cn/api/media/editor-context.html) 官方api外，额外扩展的api，需要你对js有着基本的掌握，特别是Promise和异步处理
4. 本插件仅为富文本编辑器，如要解析回显还请自行寻找富文本解析插件（不推荐rich-text）

### 六、插件目录结构

```
uni_modules
└─ sv-editor 
   ├─ components 
   │  ├─ common 
   │  │  ├─ config.js			// 配置文件
   │  │  ├─ file-handler.js		// 文件处理方法
   │  │  ├─ parse.js			// 富文本解析工具
   │  │  ├─ store.js			// 插件内全局状态管理
   │  │  ├─ tool-list.js		// 工具栏工具列表
   │  │  └─ utils.js			// 通用工具api
   │  ├─ icons 
   │  │  ├─ iconfont.css		// 字体图标样式
   │  │  └─ iconfont.ttf		// 字体图标
   │  └─ sv-editor 
   │     ├─ sv-choose-file.vue		// 文件选择器
   │     ├─ sv-editor-popup-more.vue	// 更多工具弹窗面板
   │     ├─ sv-editor-render.vue	// renderjs组件
   │     ├─ sv-editor-toolbar.vue	// 内置工具栏
   │     └─ sv-editor.vue		// 编辑器主体
   ├─ changelog.md 
   ├─ package.json 
   └─ readme.md 
```

### 七、基本使用

#### sv-editor 编辑器主体

`符合uni_modules规范，无需引入直接使用`

1. props

| 属性名			| 类型			| 默认值					| 说明																				|
| :---				| :---		| :---					| :---																			|
| eid					| String	|	'sv-editor'		| 编辑器id，唯一，禁止重复，多编辑器实例时必填	|
|	placeholder	| String	| '写点什么吧 ~'	|	占位字样																		|
|	readOnly		|	Boolean	|	false					|	是否只读																		|
|	maxlength		|	Number	| -1						|	最大字数限制，<=0时表示不限									|
|	hideMax			|	Boolean	|	false					|	是否关闭最大字数显示												|

2. emits

| 事件名																| 参数											| 说明																													|兼容性		|
| :---																	| :---										| :---																												| :---		|
| ready																	| ctx 当前编辑器上下文实例	| 编辑器初始化完成时触发																				|	通用			|
| input																	| { ctx, html, text }			| 编辑器内容改变时触发																					|	通用			|
| focus																	| { ctx, event }					| 编辑器聚焦时触发																							|	通用			|
| blur																	| { ctx, event }					| 编辑器失去焦点时触发																					|	通用			|
| statuschange													| { ctx, event }					| 通过 Context 方法改变编辑器内样式时触发，返回选区已设置的样式	|	通用			|
| overmax																|	{ ctx }									| 超过最大字数限制时回调																				|	通用			|
| epaste <br/> [(*特殊扩展)](#特殊扩展)	|	{ ctx, id, text, html, range }	| 粘贴回调事件																									|	H5、APP	|

- statuschange 事件还提供 uni.$emit('E_EDITOR_STATUSCHANGE', { ctx, event }) 抛出，你可以通过 uni.$on('E_EDITOR_STATUSCHANGE') 进行监听，但是不要忘记在适当的地方off关掉
- epaste [(*特殊扩展)](#特殊扩展) 事件还提供 uni.$emit('E_EDITOR_PASTE', { ctx, id, text, html, range }) 抛出，你可以通过 uni.$on('E_EDITOR_PASTE') 进行监听，但是不要忘记在适当的地方off关掉

#### sv-editor-toolbar 编辑器工具栏

`与编辑器本体分离，按需引入使用`

1. props

| 属性名		| 类型		| 默认值																														| 说明															|
| :---			| :---	| :---																														| :---														|
| tools			|	Array	| [] 默认空数组即为全工具，可选 [详见 toolList](#toolList)						|	工具栏列表，例如 ['style', ...]		|
| styleTools|	Array	|	[] 默认空数组即为全工具，可选 [详见 styleToolList](#styleToolList)	|	样式工具列表，例如 ['header', ...]	|
| moreTools	|	Array	|	[] 默认空数组即为全工具，可选 [详见 moreToolList](#moreToolList)		|	更多功能列表，例如 ['image', ...]	|

注意：
  - 此处 toolList 等为全列表，详见 `uni_modules/sv-editor/components/common/tool-list.js` 文件。
  - 若只想使用部分工具以及修改顺序，则给组件对应的props属性例如 `:tools="['style', 'undo', 'redo']"` 即可只使用该三项工具且顺序以该数组顺序排序。
  - 关于图标，本插件内置了 [阿里巴巴矢量图标库](https://www.iconfont.cn/) 的字体图标，如需使用其他图标，请自行替换。

2. emits

|  事件名					|  参数									|  说明															|
|  :---						| :---									| :---															|
| toolMoreItem		|	{ name, value }				|	点击更多功能面板子项									|
|	moreItemConfirm	|	{ link, text, file }	|	点击更多功能弹窗确认后回调						|
|	keyboardChange	|	{ height }						| 键盘高度变化												|
|	changeMorePop		|	true 打开 / false	关闭	|	更多功能弹窗打开/关闭								|
|	tapTool					|	{ name, value }				|	点击工具栏													|
|	changeTool			|	工具name								|	工具栏改变													|
|	tapStyle				|	{ name, value }				|	点击样式工具												|
|	tapEmoji				|	{ name, value }				|	点击Emoji表情											|
|	backspace				|												|	触发编辑器实例主动使用backspace后回调	|

##### toolList 

| title	| name		| value	| icon							|
| :---	| :---		| :---	| :---							|
|	 样式	|	style		|				|	icon-zitiyanse		|
|	 表情	|	emoji		|				|	icon-xiaolian			|
|	 撤销	|	undo		|				|	icon-shangyibu1		|
|	 重做	|	redo		|				|	icon-xiayibu1			|
|	 更多	|	more		|				|	icon-icon_tianjia	|
|	 扩展	|	setting	|				|	icon-bianji				|

##### styleToolList

| title			|  name						| value		| icon								|
| :---			|  :---						| :---		| :---								|
|	 标题			| header					|	2				|	icon-zitibiaoti			|
|	 分割线		| divider					|					|	icon-fengexian			|
|	 粗体			| bold						|					|	icon-zitijiacu			|
|	 斜体			|	italic					|					|	icon-zitixieti			|
|	 下划线		| underline				|					|	icon-zitixiahuaxian	|
|	 删除线		| strike					|					|	icon-zitishanchuxian|
|	 左对齐		| align						|	left		|	icon-zuoduiqi				|
|	 居中			| align						|	center	|	icon-juzhongduiqi		|
|	 右对齐		| align						|	right		|	icon-youduiqi				|
|	 有序列表	|	list						|	ordered	|	icon-youxupailie		|
|	 无序列表	|	list						|	bullet	|	icon-wuxupailie			|
|	 上标			|	script					|	super		|	icon-zitishangbiao	|
|	 左缩进		|	indent					|	+1			|	icon-zuosuojin			|
|	 右缩进		|	indent					|	-1			|	icon-yousuojin			|
|	 下标			|	script					|	sub			|	icon-ziti-xiabiao		|
|	 文字颜色	|	color						|					|	icon-wenziyanse			|
|	 背景颜色	|	backgroundColor	|					|	icon-beijingyanse'	|
|	 清除格式	|	removeformat		|					|	icon-qingchugeshi		|

- 以上为插件内置样式工具，更多详见 [支持设置的样式列表](https://uniapp.dcloud.net.cn/api/media/editor-context.html#editorcontext-format)
- 缩进时，需要在解析插件（此处以mp-html为例）中添加如下缩进样式以供识别：
    ```
    // uni_modules/mp-html/components/mp-html/node/node.vue
    
    // 不要管插件内原始的样式代码
    <style>...</style>
    
    // 直接在该vue文件最底下添加如下scss样式
    <style lang="scss">
    @for $i from 1 through 10 {
      .ql-indent-#{$i} {
        // 默认一个缩进为2个em单位，此处对应
        text-indent: #{$i * 2}em;
      }
    }
    </style>
    ```

##### moreToolList

| title			|  name			| value	| icon							|
| :---			|  :---			| :---	| :---							|
|	 添加图片	|	image			|	popup	|	icon-charutupian	|
|	 添加视频	|	video			|	popup	|	icon-shexiangji		|
|  添加链接	|	link			|	popup	|	icon-charulianjie	|
|  添加附件	|	attachment|	popup	|	icon-huixingzhen	|
|  提及			|	at				|	popup	|	icon-at						|
|  话题			|	topic			|	popup	|	icon-huati				|
|  清空			|	clear			|	button|	icon-shanchu			|

*在微信小程序中，生成的 a 标签的 href 属性会被自动抹去，因此在微信小程序中是无法点击超链接跳转的，这点目前微信小程序官方固件不支持，暂时也没啥好的办法*


##### emojiToolList

emoji默认列表

##### colorList

调色板默认颜色列表

#### api 合集

1. [editorContext 官方api](https://uniapp.dcloud.net.cn/api/media/editor-context.html)

2. 本插件编辑器实例 `editorCtx` 中，你可以直接通过富文本实例调用

|  方法名																						|  参数																											| 返回值															|  说明																																						| 兼容性						|
|  :---																							|  :---																											| :---															| :---																																						| :---						|
| focus																							|																														|																		| 主动聚焦																																					|	H5、App					|
|	backspace																					|																														|																		|	退格，会触发sv-editor-toolbar的backspace回调函数																		|	H5、App(Android)	|
|	createVideoThumbnail <br/> [(*特殊扩展)](#特殊扩展)| url:string 视频地址																				|	封面图地址	Promise									|	以视频资源地址，直接生成视频封面图（需要保证视频资源正常可以播放）											|	H5、App(Android)	|
|	createCoverThumbnail <br/> [(*特殊扩展)](#特殊扩展)| url:string 图片资源																				|	封面图地址	Promise									|	若后端返回视频封面但是没有播放图标，可以用此方法在图片中央叠加播放图标，用于作为视频封面	|	通用							|
|	changeInputMode																		|	type:string 模式，可选：none/remove													|																		|	修改输入模式，该api是取消键盘闪烁的关键，none时将禁止键盘弹出，remove时将移除该限制			|	H5、App					|
|	changeInput																				|																														|																		|	主动触发input回调事件																															|	通用							|
|	getLastContent																		|																														|	{ html, text... } 内容对象 Promise	|	获取富文本当前最新内容																															|	通用							|
|	exportHtml																				|	html:string 要导出的富文本																	|	处理后的富文本 String								|	富文本导出，若富文本携带视频，则会自动解析为video标签																	|	通用							|
|	initHtml																					|	html:string 初始化的富文本 <br/> customCallback 详见补充说明	|																		|	富文本内容初始化，若富文本携带video标签，将会自动进行解析转换													|	通用							|

- initHtml 在微信小程序端会导致聚焦滚动，建议先将编辑器 v-show=false，待 initHtml 内容初始化完成后再 true。也正是因为微信小程序端会聚焦滚动，所以 editorEID 在初始阶段会默认保持最后一个实例 eid，需要手动重新聚焦
- initHtml 第二个参数 customCallback 和 api: replaceVideoWithImageRender 一致，customCallback 为自定义处理封面回调，自带参数为视频地址，需要return封面图片资源，若无有效返回则走默认封面处理，建议配合后端生成视频封面以兼容各端。 

3. `uni_modules/sv-editor/components/common/store.js` 文件中，插件内全局状态仓库，你可以按需引入后通过 store.state 与 store.actions 来访问变量

|  方法名			|  参数							| 返回值						|  说明									| 兼容性	|
|  :---				|  :---							| :---						| :---									| :---	|
|	getEditor		|	 eid							|	eid编辑器实例		|	获取指定eid的编辑器实例	|	通用		|
|	setEditor		|	 eid, ctx					|									|	设置eid编辑器实例			|	通用		|
|	getEID			|										|	当前编辑器eid		|	获取当前编辑器eid			|	通用		|
|	setEID			|	 当前编辑器eid			|									|	设置当前编辑器eid			|	通用		|
|	getFormats	|										|	编辑器样式格式		|	获取编辑器样式格式			|	通用		|
|	setFormats	|	 编辑器样式格式		|									|	设置编辑器样式格式			|	通用		|
|	getReadOnly	|										|	是否只读 Boolean	|	获取编辑器是否只读			|	通用		|
|	setReadOnly	|	 是否只读 Boolean	|									|	设置编辑器是否只读			|	通用		|

4. `uni_modules/sv-editor/components/common/utils.js` 文件中，需要按需引入，实用工具

|  方法名						|  参数																|  返回值										| 说明																																					|  兼容性	|
|  :---							|   :---															|  :---											|  :---																																				|  :---		|
|  addImage					|	(uploadFunc必填, options)						|	Array/Promise 上传的文件		|	添加图片																																			|	通用			|
|	 addVideo					|	(uploadFunc必填, options)						|	Array/Promise 上传的文件		|	添加视频																																			|	通用			|
|	 addLink					|	(options, callback)									|														|	添加链接																																			|	通用			|
|	 addAttachment		|	(uploadFunc必填, options, callback)	|	Object/Promise 上传的文件	|	添加附件																																			|	通用			|
|	 addAt						|	(options, callback)									|														|	添加提及																																			|	通用			|
|	 addTopic					|	(options, callback)									|														|	添加话题																																			|	通用			|
|	 insertLink				|	(editorCtx必填, options, callback)		|														|	插入链接母本：添加链接、添加附件、添加提及、添加话题均基于此										|	通用			|
|	 noKeyboardEffect	|	(callback必填, options)							|														|	核心：消除键盘影响，但是微信小程序只能通过编辑器失焦的方式关闭键盘(依然会闪一下)	|	通用			|

5. `uni_modules/sv-editor/components/common/parse.js` 文件中，需要按需引入，正则解析工具

|  方法名											|  参数																																																												|  返回值										|  说明																															|  兼容性	|
|  :---												|  :---																																																												|  :---											|  :---																															|  :---		|
|	 replaceVideoWithImageRender|	richText:string 要进行处理的富文本字符串 <br/> customCallback 自定义处理封面回调，需要return处理后的封面图片，自带参数为视频地址	|	 处理结果	Promise					|	带有视频的富文本逆向转换，可通过customCallback回调函数自定义处理封面	| 通用			|
|	 parseHtmlWithVideo					|	richText:string 要进行处理的富文本字符串																																											|	 处理结果	String					|	将含有封面占位图形式的视频富文本转换成正常视频的富文本								|	通用			|
|	 parseImagesAndVideos				|	richText:string 要进行处理的富文本字符串																																											|	 处理结果	Array < Object >|	解析当前富文本中所有图片和视频																			|	通用			|
|	 parseImages								|	richText:string 要进行处理的富文本字符串																																											|	 处理结果	Array < Object >|	解析当前富文本中所有图片																						|	通用			|
|	 parseVideos								|	richText:string 要进行处理的富文本字符串																																											|	 处理结果	Array < Object >|	解析当前富文本中所有视频																						|	通用			|

6. `uni_modules/sv-editor/components/common/config.js` 配置文件

|  参数						|  说明																																					|
|  :---						|  :---																																					|
|	video_thumbnail	|	视频默认封面，默认封面图可能会失效，原图在示例工程根目录下static文件夹中，建议自行替换	|
|	video_playicon	|	视频封面播放图标（默认三角播放图标）																								|

7. 具体使用代码案例请  `使用HBuilderX导入示例项目` 导入示例工程参考

### 八、特殊扩展

**本插件提供部分额外特殊扩展功能，具体如下：**

|  功能								|  说明																																						| 类型			| 兼容			|
|  :---								|  :---																																						| :---		| :---		|
|	粘贴保留格式					|	粘贴时尽可能的保留原有格式（并非完全复制）																						|	固有功能	|	H5、APP	|
|	epaste							|	粘贴回调事件																																			|	事件			|	H5、APP	|
|	createVideoThumbnail|	以视频资源地址，直接生成视频封面图（需要保证视频资源正常可以播放）											|	api			|	H5、APP	|
|	createCoverThumbnail|	若后端返回视频封面但是没有播放图标，可以用此方法在图片中央叠加播放图标，用于作为视频封面	|	api			|	通用			|
|	待补充 ...					|																																									|					|					|

- createCoverThumbnail 在iOS端可能会报 `the operation is insecure` 的错，这是iOS更加严格的安全策略导致的，本地file://协议也会导致跨域，从而污染了画布

制作不易，特殊扩展功能限时免费开放，感谢支持Thanks♪(･ω･)ﾉ
使用方式：将插件内 backup 文件夹下的文件复制并粘贴进 plugins 文件夹下并覆盖原文件

### 九、结语

本插件免费开源（除特殊扩展外），如若借鉴源码还请注明出处，未经授权禁止转载售卖等侵犯版权行为，谢谢！

如若商用，望您可以联系作者本人，留下您的项目名，我希望能以方式此推广，谢谢！

感谢您使用本插件，如果在使用过程中遇到任何问题，欢迎在评论区留言，或在 [Gitee](https://gitee.com/Sonve/sv-editor) 上提交issue，我会尽快回复您。

制作不易，还望五星好评，若能在 [Gitee](https://gitee.com/Sonve/sv-editor) 上点个 ⭐star，不胜感激Thanks♪(･ω･)ﾉ

欢迎加群讨论，Q群：
① [852637893](https://qm.qq.com/cgi-bin/qm/qr?k=R7DHSqqDI4-xRCfwdUB2e3NrTytHpcVe&jump_from=webapi&authKey=2IpufavBOSPOLdncCt7EFnbmbWrUHg1c8iqNEdTzG8zCvnKb8/0aaLXF4HJzlp2R) 
② [816646292](https://qm.qq.com/cgi-bin/qm/qr?k=ndZIUqx0xctbq8oDQVTiDir7AUO5jq9X&jump_from=webapi&authKey=fgk45wWObUUvig7FIuFUuM+0IFLvOJI7LMc1d4qNbWAIfehakai/ZfckYfAGLPne)
