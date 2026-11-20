// version-update.js
const fs = require('fs');
const path = require('path');

// 获取当前版本号
const packagePath = path.resolve(__dirname, 'package.json');
const packageJson = JSON.parse(fs.readFileSync(packagePath, 'utf8'));
const oldVersion = packageJson.version;

// 递增版本号（示例使用最后一位自增）
const versionParts = oldVersion.split('.').map(Number);
versionParts[2] += 1; // 修改第三位版本号
const newVersion = versionParts.join('.');

// 更新 package.json
packageJson.version = newVersion;
fs.writeFileSync(packagePath, JSON.stringify(packageJson, null, 2) + '\n');

// 生成版本文件
const versionFilePath = path.resolve(__dirname, 'src/version.json');
fs.writeFileSync(versionFilePath, JSON.stringify({ version: newVersion }));

console.log(`Version updated: ${oldVersion} → ${newVersion}`);