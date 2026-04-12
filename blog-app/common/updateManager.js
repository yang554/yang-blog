import {
	userApi
} from "@/api/index.js";

class UpdateManager {
	constructor() {
		this.downloadTask = null;
		this.isUpdating = false;
		this.updateCallback = null; // 用于回调到页面组件
	}

	/**
	 * 设置更新回调函数
	 */
	setUpdateCallback(callback) {
		this.updateCallback = callback;
	}

	/**
	 * 检查更新
	 */
	async checkUpdate() {
		try {
			// 获取当前版本信息
			const currentVersion = await this.getCurrentVersion();
			console.log("获取当前版本信息", currentVersion.name)

			// 请求服务器获取最新版本信息
			const latestVersion = await this.getLatestVersionInfo(currentVersion.name);
			console.log("请求服务器获取最新版本信息", latestVersion)

			if (this.compareVersion(latestVersion.versionCode, currentVersion.versionCode)) {
				// 发现新版本，通过回调通知页面显示更新弹窗
				if (this.updateCallback) {
					this.updateCallback({
						hasUpdate: true,
						versionInfo: latestVersion,
						isForceUpdate: latestVersion.forceUpdate === true
					});
				}
				return {
					hasUpdate: true,
					versionInfo: latestVersion,
					isForceUpdate: latestVersion.forceUpdate === true
				};
			} else {
				if (this.updateCallback) {
					this.updateCallback({
						hasUpdate: false,
						isLatest: true
					});
				}
				return {
					hasUpdate: false,
					isLatest: true
				};
			}
		} catch (error) {
			console.error('检查更新失败:', error);
			if (this.updateCallback) {
				this.updateCallback({
					hasUpdate: false,
					error: error.message
				});
			}
			throw error;
		}
	}

	/**
	 * 获取当前App版本信息
	 */
	getCurrentVersion() {
		return new Promise((resolve, reject) => {
			plus.runtime.getProperty(plus.runtime.appid, (inf) => {
				resolve({
					version: inf.version,
					versionCode: parseInt(inf.versionCode),
					name: inf.name
				});
			}, reject);
		});
	}

	/**
	 * 获取服务器最新版本信息
	 */
	async getLatestVersionInfo(appName) {
		const params = `versionName=${appName}&versionCode=&startTime=&endTime=`
		const response = await userApi._getLatestVersionInfo(params);
		if (response.status === 200) {
			return response.data[response.data.length -1];
		} else {
			throw new Error('获取版本信息失败');
		}
	}

	/**
	 * 版本比较 
	 */
	compareVersion(newVersion, currentVersion) {
		return newVersion > currentVersion;
	}

	/**
	 * 下载并安装更新
	 */
	async downloadAndInstall(versionInfo) {
		if (this.isUpdating) {
			uni.showToast({
				title: '正在更新中...',
				icon: 'none'
			});
			return;
		}

		this.isUpdating = true;

		try {
			// 通知页面显示下载进度
			if (this.updateCallback) {
				this.updateCallback({
					downloading: true,
					progress: 0
				});
			}

			// 下载 wgt 文件
			const filePath = await this.downloadWgt(versionInfo.downloadUrl);

			// 安装 wgt 文件
			await this.installWgt(filePath);

			// 安装成功
			if (this.updateCallback) {
				this.updateCallback({
					downloadComplete: true
				});
			}

		} catch (error) {
			console.error('更新失败:', error);
			if (this.updateCallback) {
				this.updateCallback({
					downloadError: true,
					error: error.message
				});
			}
			throw error;
		} finally {
			this.isUpdating = false;
		}
	}

	/**
	 * 下载 wgt 文件
	 */
	downloadWgt(downloadUrl) {
		return new Promise((resolve, reject) => {
			// 创建下载任务
			this.downloadTask = uni.downloadFile({
				url: downloadUrl,
				success: (downloadResult) => {
					if (downloadResult.statusCode === 200) {
						resolve(downloadResult.tempFilePath);
					} else {
						reject(new Error(`下载失败，状态码: ${downloadResult.statusCode}`));
					}
				},
				fail: (error) => {
					reject(new Error(`下载失败: ${error.errMsg}`));
				}
			});

			// 监听下载进度
			this.downloadTask.onProgressUpdate((res) => {
				if (this.updateCallback) {
					this.updateCallback({
						downloading: true,
						progress: res.progress
					});
				}
			});
		});
	}

	/**
	 * 安装 wgt 文件
	 */
	installWgt(filePath) {
		return new Promise((resolve, reject) => {
			plus.runtime.install(
				filePath, {
					force: false
				},
				() => {
					console.log('安装 wgt 文件成功');
					resolve();
				},
				(error) => {
					console.error('安装 wgt 文件失败:', error);
					reject(new Error(`安装失败: ${error.message}`));
				}
			);
		});
	}

	/**
	 * 取消下载
	 */
	cancelDownload() {
		if (this.downloadTask) {
			this.downloadTask.abort();
			this.downloadTask = null;
		}
		this.isUpdating = false;
	}
}

// 创建单例
const updateManager = new UpdateManager();
export default updateManager;