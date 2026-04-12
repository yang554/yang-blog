import {
	postRequest,
	getRequest,
	deleteRequest,
	postRequestJson
} from '../request.js'

export const tagApi = {
	_getTagAll: () => {
		return getRequest("/tag/all");
	},
	_getTagByName: (data) => {
		return getRequest("/tag/getTagByName?name=" + data);
	},
	_addTag: (data) => {
		return postRequestJson("/tag/addTag", data);
	},
	_updTagById: (data) => {
		return postRequestJson("/tag/updTagById", data);
	},
	_delTagById: (data) => {
		return deleteRequest("/tag/delTagById?id=" + data);
	}
}