import { regionData } from 'element-china-area-data';

/**
 * 根据6位行政区划编码解析地址
 * @param {string} code - 6位编码，如 "120102"
 * @param {Array} data - 地址数据，默认使用 regionData
 * @returns {Object} 包含省、市、区名称的对象
 */
export function parseCodeToString(code, data = regionData) {
    if (!code || typeof code !== 'string' || code.length !== 6) {
        console.warn('无效的地址编码:', code);
        return { province: '', city: '', area: '' };
    }

    let result = { province: '', city: '', area: '' };

    // 遍历所有省级数据
    for (const province of data) {
        // 检查省级编码是否匹配（前2位相同）
        if (province.value && province.value.startsWith(code.substring(0, 2))) {
            result.province = province.label;

            // 如果有市级数据
            if (province.children && province.children.length > 0) {
                // 遍历市级数据
                for (const city of province.children) {
                    // 检查市级编码是否匹配（前4位相同）
                    if (city.value && city.value.startsWith(code.substring(0, 4))) {
                        result.city = city.label;

                        // 如果有区级数据
                        if (city.children && city.children.length > 0) {
                            // 遍历区级数据
                            for (const area of city.children) {
                                // 检查区级编码是否完全匹配
                                if (area.value === code) {
                                    result.area = area.label;
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            break;
        }
    }

    return result;
}

/**
 * 根据6位编码获取完整的地址文本
 * @param {string} code - 6位编码
 * @param {string} detail - 详细地址
 * @returns {string} 完整地址
 */
export function getFullAddressByCode(code, detail = '', data = regionData) {
    const { province, city, area } = parseCodeToString(code, data);
    const baseAddress = province + city + area;
    return baseAddress + (detail || '');
}

/**
 * 从完整地址中提取详细地址（去掉省市区部分）
 * @param {string} fullAddress - 完整地址，如 "天津市天津市河东区向阳楼街道128号"
 * @param {string} areaCode - 区级编码，如 "120102"
 * @returns {string} 详细地址，如 "向阳楼街道128号"
 */
export function extractDetailAddress(fullAddress, province,city,area) {
    if (!fullAddress || !province || !city || !area) return fullAddress || '';

    // 构建省市区组合（可能有多种组合方式）
    const addressPrefixes = [
        province + city + area,     // "天津市天津市河东区"
        province + area,            // "天津市河东区"（如果城市名和区名有重叠）
        city + area,                // "天津市河东区"
        area,                       // "河东区"
        province + city,            // "天津市天津市"
        province,                   // "天津市"
        city,                       // "天津市"
    ].filter(Boolean); // 过滤掉空字符串

    // 尝试匹配并移除前缀
    let detailAddress = fullAddress;
    let matchedPrefix = '';

    for (const prefix of addressPrefixes) {
        if (fullAddress.startsWith(prefix)) {
            matchedPrefix = prefix;
            detailAddress = fullAddress.substring(prefix.length);
            break;
        }
    }

    // 如果没有匹配到，尝试模糊匹配（可能是地址格式不一致）
    if (!matchedPrefix) {
        // 按顺序尝试匹配每个部分
        let remaining = fullAddress;
        let matched = '';

        // 先匹配省
        if (province && remaining.includes(province)) {
            const index = remaining.indexOf(province);
            remaining = remaining.substring(index + province.length);
            matched += province;
        }

        // 再匹配市
        if (city && remaining.includes(city)) {
            const index = remaining.indexOf(city);
            remaining = remaining.substring(index + city.length);
            matched += city;
        }

        // 最后匹配区
        if (area && remaining.includes(area)) {
            const index = remaining.indexOf(area);
            remaining = remaining.substring(index + area.length);
            matched += area;
        }

        if (matched) {
            detailAddress = remaining;
        }
    }

    // 清理详细地址（去掉开头可能的空格、逗号等）
    detailAddress = detailAddress.replace(/^[,，、\s]+/, '');

    return detailAddress || ""; // 如果提取失败，返回原地址
}

/**
 * 合并地址（用于保存时）
 * @param {string} areaCode - 区级编码
 * @param {string} detailAddress - 详细地址
 * @returns {string} 完整地址
 */
export function combineFullAddress(province,city,area, detailAddress) {
    if (!province) return detailAddress || '';
    if (!detailAddress) return '';

    const baseAddress = province + city + area;

    return baseAddress + detailAddress;
}

/**
 * 调试函数：打印所有编码以帮助排查
 */
export function debugAddressCodes(data = regionData) {
    console.log('地址数据调试:');
    data.forEach(province => {
        console.log(`省: ${province.label} (${province.value})`);
        if (province.children) {
            province.children.forEach(city => {
                console.log(`  市: ${city.label} (${city.value})`);
                if (city.children) {
                    city.children.forEach(area => {
                        console.log(`    区: ${area.label} (${area.value})`);
                    });
                }
            });
        }
    });
}