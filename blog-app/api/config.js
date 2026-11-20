// 环境配置
const env = process.env.NODE_ENV || 'development'
//http://192.168.0.100:8090
//'http://192.168.3.5:8090'
//http://hj.free.idcfengye.com  http://hj.free.idcfengye.com
const config = {
  // 开发环境
  development: {
    BASE_URL: 'http://192.168.0.110:8090',
	IMG_URL: 'http://192.168.0.110:80',
    TIMEOUT: 10000
  },
  // 生产环境
  production: {
    BASE_URL: 'http://180.76.189.135:8090',
	IMG_URL: 'http://180.76.189.135:80',
    TIMEOUT: 1500000
  }
}

export const BASE_URL = config[env].BASE_URL
export const IMG_URL = config[env].IMG_URL
export const TIMEOUT = config[env].TIMEOUT