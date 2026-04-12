<template>
    <div class="upload-container">
        <div class="upload-card">
            <h2 class="title">App 升级包上传</h2>

            <el-form :model="form" :rules="rules" ref="uploadForm" label-width="120px">
                <!-- wgt 文件上传 -->
                <el-form-item label="升级包文件" prop="file">
                    <el-upload class="upload-demo" drag action="#" :auto-upload="false" :on-change="handleFileChange"
                        :file-list="fileList" accept=".wgt">
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">将 wgt 文件拖到此处，或<em>点击上传</em></div>
                        <div class="el-upload__tip" slot="tip">只能上传 wgt 文件</div>
                    </el-upload>
                </el-form-item>

                <!-- 版本信息展示（自动读取） -->
                <el-form-item label="版本信息" v-if="versionInfo.versionName">
                    <div class="version-info">
                        <div class="info-item">
                            <span class="label">APP名称：</span>
                            <span class="value">{{ versionInfo.appName }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">APP说明：</span>
                            <span class="value">{{ versionInfo.note }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">版本名称：</span>
                            <span class="value">{{ versionInfo.versionName }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">版本代码：</span>
                            <span class="value">{{ versionInfo.versionCode }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">文件大小：</span>
                            <span class="value">{{ versionInfo.fileSize }}</span>
                        </div>
                    </div>
                </el-form-item>

                <!-- 更新标题 -->
                <el-form-item label="更新标题" prop="updateTitle">
                    <el-input v-model="form.updateTitle" placeholder="例如：发现新版本"></el-input>
                </el-form-item>

                <!-- 更新内容 -->
                <el-form-item label="更新内容" prop="updateContent">
                    <el-input type="textarea" :rows="4" v-model="form.updateContent" placeholder="请输入更新内容，每行一条"></el-input>
                </el-form-item>

                <!-- 是否强制更新 -->
                <el-form-item label="强制更新">
                    <el-switch v-model="form.forceUpdate"></el-switch>
                    <span class="switch-tip">开启后用户必须更新才能继续使用</span>
                </el-form-item>

                <!-- 操作按钮 -->
                <el-form-item>
                    <el-button type="primary" @click="submitUpload" :loading="loading">
                        {{ loading ? '上传中...' : '提交上传' }}
                    </el-button>
                    <el-button @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
  
<script>
import { _uploadWgtFile } from "@/api/api.js";

export default {
    name: 'AppUpload',
    data() {
        return {
            loading: false,
            fileList: [],
            versionInfo: {
                appName:'',
                note:'',
                versionName: '',
                versionCode: 0,
                fileSize: ''
            },
            form: {
                file: null,
                updateTitle: '发现新版本',
                updateContent: '1. 修复已知问题\n2. 优化用户体验\n3. 新增功能',
                forceUpdate: false
            },
            rules: {
                file: [
                    { required: true, message: '请选择 wgt 文件', trigger: 'change' }
                ],
                updateTitle: [
                    { required: true, message: '请输入更新标题', trigger: 'blur' }
                ],
                updateContent: [
                    { required: true, message: '请输入更新内容', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        // 处理文件选择
        handleFileChange(file, fileList) {
            if (fileList.length > 0) {
                this.fileList = [fileList[fileList.length - 1]]; // 只保留最后一个文件
            }

            const selectedFile = file.raw;
            if (selectedFile) {
                this.form.file = selectedFile;
                this.extractVersionInfo(selectedFile);
            }
        },

        // 从 wgt 文件中提取版本信息
        async extractVersionInfo(file) {
            try {
                // 创建 JSZip 实例
                const JSZip = await import('jszip');
                const zip = new JSZip.default();

                // 读取文件内容
                const arrayBuffer = await this.readFileAsArrayBuffer(file);
                const zipFile = await zip.loadAsync(arrayBuffer);

                // 查找 manifest.json
                const manifestFile = zipFile.file('manifest.json');
                if (!manifestFile) {
                    this.$message.error('wgt 文件中未找到 manifest.json');
                    return;
                }

                // 解析 manifest.json
                const manifestContent = await manifestFile.async('text');
                const manifest = JSON.parse(manifestContent);
                // 更新版本信息
                this.versionInfo = {
                    versionName: manifest.version.name || '',
                    versionCode: manifest.version.code || 0,
                    appName: manifest.name || '',
                    note: manifest.description || '',
                    fileSize: this.formatFileSize(file.size)
                };
                // 自动填充表单
                if (!this.form.updateTitle) {
                    this.form.updateTitle = `发现新版本 v${manifest.versionName}`;
                }

            } catch (error) {
                console.error('解析 wgt 文件失败:', error);
                this.$message.error('解析 wgt 文件失败，请检查文件格式');
            }
        },

        // 将文件读取为 ArrayBuffer
        readFileAsArrayBuffer(file) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = (e) => resolve(e.target.result);
                reader.onerror = reject;
                reader.readAsArrayBuffer(file);
            });
        },

        // 格式化文件大小
        formatFileSize(bytes) {
            if (bytes === 0) return '0 B';
            const k = 1024;
            const sizes = ['B', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        },

        // 提交上传
        async submitUpload() {
            try {
                await this.$refs.uploadForm.validate();

                if (!this.form.file) {
                    this.$message.error('请选择 wgt 文件');
                    return;
                }

                this.loading = true;

                // 创建 FormData
                const formData = new FormData();
                formData.append('file', this.form.file);
                formData.append('updateTitle', this.form.updateTitle);
                formData.append('updateContent', this.form.updateContent);
                formData.append('forceUpdate', this.form.forceUpdate);
                formData.append('versionInfo', JSON.stringify(this.versionInfo));

                // 调用上传接口
                const response = await _uploadWgtFile(formData);

                if (response.data.status === 200) {
                    this.$message.success('上传成功');
                    this.resetForm();
                } else {
                    this.$message.error(response.message || '上传失败');
                }

            } catch (error) {
                console.error('上传失败:', error);
                this.$message.error('上传失败，请重试');
            } finally {
                this.loading = false;
            }
        },

        // 重置表单
        resetForm() {
            this.$refs.uploadForm.resetFields();
            this.fileList = [];
            this.versionInfo = {
                versionName: '',
                versionCode: 0,
                fileSize: ''
            };
            this.form.file = null;
        }
    }
};
</script>
  
<style scoped>
.upload-container {
    padding: 20px;
    background: #f5f7fa;
    min-height: 100vh;
}

.upload-card {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
    margin-bottom: 24px;
    color: #303133;
    border-bottom: 1px solid #e4e7ed;
    padding-bottom: 16px;
}

.version-info {
    background: #f8f9fa;
    padding: 16px;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
}

.info-item {
    margin-bottom: 8px;
    display: flex;
}

.info-item:last-child {
    margin-bottom: 0;
}

.info-item .label {
    color: #606266;
    min-width: 80px;
}

.info-item .value {
    color: #303133;
    font-weight: 500;
}

.switch-tip {
    margin-left: 8px;
    color: #909399;
    font-size: 12px;
}

.upload-demo {
    width: 100%;
}

::v-deep .el-upload-dragger {
    width: 100%;
}
</style>