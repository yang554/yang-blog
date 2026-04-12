/**
 * 富文本plugin微信小程序特殊扩展
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-17
 */
import config from '../common/config.js'

/**
 * 微信小程序特有的OffscreenCanvas方法
 * @param {String} coverImageUrl 封面资源地址
 * @returns {Promise<String>} 处理后的封面图片的临时文件路径
 */
export function wxCreateCoverThumbnail(coverImageUrl) {
  const loadImage = () => {
    return new Promise((resolve, reject) => {
      uni.getImageInfo({
        src: coverImageUrl,
        success: (info) => {
          resolve(info)
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  }
  return new Promise(async (resolve, reject) => {
    try {
      const imageInfo = await loadImage()

      // 创建离屏 Canvas
      const canvas = uni.createOffscreenCanvas({
        type: '2d',
        width: imageInfo.width,
        height: imageInfo.height
      })
      const ctx = canvas.getContext('2d')

      // 创建一个图片
      const coverImg = canvas.createImage()
      // 等待图片加载
      await new Promise((resolve) => {
        coverImg.onload = resolve
        coverImg.src = coverImageUrl // 要加载的图片 url
      })

      // 绘制封面图片到离屏 Canvas
      ctx.drawImage(coverImg, 0, 0, canvas.width, canvas.height)

      // 加载播放按钮图标
      const playIcon = canvas.createImage()
      // 等待图片加载
      await new Promise((resolve) => {
        playIcon.onload = resolve
        playIcon.src = config.video_playicon // 要加载的图片 url
      })

      // 计算播放按钮的位置和大小
      // const playButtonSize = Math.min(canvas.width, canvas.height) * 0.15
      const playButtonSize = canvas.width * 0.15
      const playButtonX = (canvas.width - playButtonSize) / 2
      const playButtonY = (canvas.height - playButtonSize) / 2

      // 确保播放按钮图标按比例缩放
      const iconAspectRatio = playIcon.width / playIcon.height
      const iconWidth = playButtonSize
      const iconHeight = iconWidth / iconAspectRatio

      // 绘制播放按钮图标到离屏 Canvas
      ctx.drawImage(playIcon, playButtonX, playButtonY, iconWidth, iconHeight)

      // 获取画完后的数据
      uni.canvasToTempFilePath({
        canvas: canvas,
        destWidth: canvas.width,
        destHeight: canvas.height,
        fileType: 'png',
        success: (res) => {
          resolve(res.tempFilePath)
        },
        fail: (err) => {
          reject(new Error('Failed to convert canvas to image.'))
        }
      })
    } catch (error) {
      reject(error)
    }
  })
}

export default {
  wxCreateCoverThumbnail
}