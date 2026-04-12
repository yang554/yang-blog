# lime-dialer 转盘抽奖组件
一个功能丰富的转盘抽奖组件，用于营销活动场景。支持自定义奖品、指针、边框等多种配置，可用于抽奖活动、营销推广、游戏互动等多种场景。组件提供了丰富的自定义选项，可以满足各种复杂的抽奖界面设计需求。

> 插件依赖：无

## 文档链接
📚 组件详细文档请访问以下站点：
- [转盘抽奖组件文档 - 站点1](https://limex.qcoon.cn/components/dialer.html)
- [转盘抽奖组件文档 - 站点2](https://limeui.netlify.app/components/dialer.html)
- [转盘抽奖组件文档 - 站点3](https://limeui.familyzone.top/components/dialer.html)

## 安装方法
1. 在uni-app插件市场中搜索并导入`lime-dialer`
2. 导入后可能需要重新编译项目
3. 在页面中使用`l-dialer`组件

## 代码演示

### 基础用法
最简单的转盘抽奖组件用法，设置奖品列表并通过ref调用run方法开始抽奖。

```html
<l-dialer :prizeList="prizeList" @click="onClick" @done="onDone" ref="dialer" />
```

```js
export default {
    data() {
        return {
            // 奖品列表
            prizeList: [
                {
                    id: 'coupon88',
                    name: '8.8折',
                    img: 'https://img11.360buyimg.com/pop/jfs/t1/175718/35/12595/5477/60b660c6Eb850717b/a1cfe750dcdb5b78.png',
                },
                {
                    id: 'coupon900',
                    Color: 'rgb(251, 219, 216)',
                    name: '900',
                    img: 'https://img11.360buyimg.com/pop/jfs/t1/190845/9/6092/4489/60b65fe8Ebb8f8284/955da889f6d1c13e.png',
                },
                // 更多奖品...
            ]
        };
    },
    methods: {
        onDone(index) {
            const prize = this.prizeList[index]
            uni.showModal({
                title: prize.id == 'thanks' ? '很遗憾': '恭喜您',
                content: (prize.id != 'thanks' ? `获得`:'') + prize.name
            })
        },
        onClick() {
            // 奖品的索引
			// uniapp
            this.$refs.dialer.run(5)
			
			// uniapp x
			const dialerRef = this.$refs['dialer'] as LDialerComponentPublicInstance
			// 奖品的索引
			dialerRef.run(5)
        }
    }
}
```

### 自定义转盘边框
可以通过两种方式设置转盘边框：

```html
<!-- 方式1：通过样式设置背景图片 -->
<l-dialer dial-style="color: rgba(60,48,158,0.7); padding: 32rpx;background-image: url(http://example.com/static/dialer/lottery-bg.png)"/>

<!-- 方式2：通过插槽设置 -->
<l-dialer>
    <image slot="border" src="static/dialer/lottery-bg.png"/>
</l-dialer>
```

### 自定义指针
可以通过多种方式设置指针：

```html
<!-- 方式1：通过样式设置背景图片 -->
<l-dialer pointer-style="background-image: url(http://example.com/static/dialer/pointer.png)"/>

<!-- 方式2：通过插槽设置 -->
<l-dialer>
    <image slot="pointer" src="static/dialer/pointer.png" />
</l-dialer>
```

### 自定义奖品样式
可以通过插槽自定义奖品的展示方式：

```html
<l-dialer>
    <template #prize="{item}">
        <image style="width: 72rpx; height: 72rpx;" :src="item.img" />
    </template>
</l-dialer>
```

> 注意：微信小程序最好使用HX3.7.12+并且在`manifest.json`设置`slotMultipleInstance: true`

## 快速预览
导入插件后，可以直接使用以下标签查看演示效果：

```html
<!-- 代码位于 uni_modules/lime-dialer/components/lime-dialer -->
<lime-dialer />
```

## 插件标签说明

| 标签名 | 说明 | 
| --- | --- | 
| `l-dialer` | 组件标签 |
| `lime-dialer` | 演示标签 |

## API文档

### Props 属性说明

| 属性名 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| size | 转盘直径，默认单位为 `rpx` | _number \| string_ | `300` |
| prizeList | 奖品列表 | _Array_ | `[]` |
| turns | 旋转圈数 | _number_ | `10` |
| duration | 旋转过程时间，单位为 `s` | _number_ | `3` |
| styleOpt | 转盘中的样式配置 | _object_ | 见下方说明 |
| customStyle | 外容器的自定义样式 | _string_ | - |
| dialStyle | 转盘自定义样式 | _string_ | - |
| pointerStyle | 指针自定义样式 | _string_ | `width: 30%` |

### styleOpt 配置项

| 属性名 | 说明 | 类型 | 默认值 |
| --- | --- | --- | --- |
| prizeBgColors | 每一块扇形的背景色 | _Array_ | `['#fff0a3', '#fffce6']` |
| borderColor | 每一块扇形的外边框颜色 | _string_ | `#ffd752` |

### Events 事件

| 事件名 | 说明 | 回调参数 |
| --- | --- | --- |
| click | 点击指针时触发 | - |
| done | 转盘旋转结束时触发 | _index: number_ (中奖奖品的索引) |

### Methods 方法

通过 ref 可以获取到组件实例并调用实例方法

| 方法名 | 说明 | 参数 |
| --- | --- | --- |
| run | 开始旋转到指定索引位置 | _index: number_ (奖品的索引) |
| done   | 旋转结束，该事件是通过标签接收的方法 |
| click   | 点击指针，该事件是通过标签接收的方法 |

### Slots 插槽

| 名称 | 说明 |
| --- | --- |
| border | 自定义转盘边框 |
| prize | 自定义奖品展示，提供 item 参数表示当前奖品数据 |
| pointer | 自定义指针 |

### prizeList 奖品配置

奖品列表的每一项配置：

```typescript
interface Prize {
  id: string;        // 奖品ID
  name: string;      // 奖品名称
  img?: string;      // 奖品图片
  Color?: string;    // 奖品背景色（可选，优先级高于styleOpt.prizeBgColors）
}
```


 `--dialer_prize_image_size` | `36px` | 奖品图片大小 |

## 支持与赞赏

如果你觉得本插件解决了你的问题，可以考虑支持作者：

| 支付宝赞助 | 微信赞助 |
|------------|------------|
| ![](https://testingcf.jsdelivr.net/gh/liangei/image@1.9/alipay.png) | ![](https://testingcf.jsdelivr.net/gh/liangei/image@1.9/wpay.png) |