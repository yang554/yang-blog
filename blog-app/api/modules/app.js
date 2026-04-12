import {
	postRequest,
	postRequestJson,
	getRequest,
	deleteRequest
} from '@/api/request.js'
import {
	BASE_URL,
	TIMEOUT
} from '@/api/config.js'

export const appApi = {
	// 获取app版本信息
	_getVersionLis: (data) => {
	  return getRequest("/app/list?"+data)
	},

}

