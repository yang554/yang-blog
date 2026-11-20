<!-- AddressCascader.vue -->
<template>
    <div class="address-cascader">
        <el-cascader v-model="selectedValue" :options="addressData" :props="cascaderProps" @change="handleChange"
            @visible-change="handleVisibleChange" :placeholder="placeholder" :disabled="disabled" clearable filterable
            class="cascader-select"></el-cascader>
        <el-input v-if="showDetail" v-model="detailAddress" placeholder="详细地址（如：XX路XX号XX小区）" class="detail-input"
            @input="handleDetailInput"></el-input>
    </div>
</template>
  
<script>
import { regionData } from 'element-china-area-data';
import { parseCodeToString } from '@/utils/addressUtil';

export default {
    name: 'AddressCascader',
    props: {
        value: {
            type: [String, Array],
            default: ''
        },
        detailValue: {
            type: String,
            default: ''
        },
        placeholder: {
            type: String,
            default: '请选择省/市/区'
        },
        disabled: {
            type: Boolean,
            default: false
        },
        showDetail: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            addressData: regionData,
            selectedValue: [],
            detailAddress: '',
            cascaderProps: {
                value: 'value',
                label: 'label',
                children: 'children',
                expandTrigger: 'hover',
                checkStrictly: false,
                emitPath: true,
                // 自定义标签显示
                renderLabel: (data) => {
                    return `${data.label} (${data.value})`;
                }
            }
        }
    },
    watch: {
        value: {
            immediate: true,
            handler(val) {
                if (val) {
                    // 如果是6位字符串编码
                    if (typeof val === 'string' && val.length === 6) {
                        this.initFromCode(val);
                    }
                    // 如果是数组
                    else if (Array.isArray(val)) {
                        this.selectedValue = val;
                    }
                } else {
                    this.selectedValue = [];
                }
            }
        },
        detailValue: {
            immediate: true,
            handler(val) {
                this.detailAddress = val || '';
            }
        }
    },
    mounted() {
        // 调试：打印所有编码
        // this.debugCodes();
    },
    methods: {
        // 根据6位编码初始化级联选择器
        initFromCode(code) {
            // 尝试直接查找匹配的区级编码
            const path = this.findPathByCode(code);
            if (path && path.length === 3) {
                this.selectedValue = path;
            } else {
                // 如果找不到，尝试模糊匹配
                this.fuzzyMatch(code);
            }
        },

        // 根据6位编码查找完整的级联路径
        findPathByCode(code, data = this.addressData) {
            for (const province of data) {
                if (!province.children) continue;

                for (const city of province.children) {
                    if (!city.children) continue;

                    for (const area of city.children) {
                        // 直接匹配6位编码
                        if (area.value === code) {
                            return [province.value, city.value, area.value];
                        }
                        // 如果区级编码格式不同，尝试其他匹配方式
                        if (area.value === code ||
                            area.value === code.substring(0, 4) + '00' ||
                            area.value === code.substring(0, 2) + '0000') {
                            return [province.value, city.value, area.value];
                        }
                    }
                }
            }
            return null;
        },

        // 模糊匹配（当精确匹配失败时）
        fuzzyMatch(code) {
            const provinceCode = code.substring(0, 2);
            const cityCode = code.substring(0, 4);

            // 查找匹配的省
            const province = this.addressData.find(p =>
                p.value === provinceCode + '0000' ||
                p.value.startsWith(provinceCode)
            );

            if (province) {

                // 查找匹配的市
                if (province.children) {
                    const city = province.children.find(c =>
                        c.value === cityCode + '00' ||
                        c.value === cityCode ||
                        c.value.startsWith(cityCode)
                    );

                    if (city) {

                        // 如果有区级数据，选择第一个
                        if (city.children && city.children.length > 0) {
                            const area = city.children[0];
                            this.selectedValue = [province.value, city.value, area.value];

                            // 触发地址变更事件
                            this.handleChange(this.selectedValue);
                        }
                    }
                }
            }
        },

        handleChange(value) {

            if (value && value.length === 3) {
                const [provinceCode, cityCode, areaCode] = value;

                // 查找对应的文本
                const province = this.addressData.find(p => p.value === provinceCode);
                const city = province?.children?.find(c => c.value === cityCode);
                const area = city?.children?.find(a => a.value === areaCode);

                const addressInfo = {
                    provinceCode,
                    cityCode,
                    areaCode,
                    province: province?.label || '',
                    city: city?.label || '',
                    area: area?.label || '',
                    fullAddress: (province?.label || '') + (city?.label || '') + (area?.label || ''),
                    codeArray: value,
                    codeString: areaCode, // 使用区级编码作为完整编码
                    // 同时提供6位编码（如果区级编码是6位）
                    sixDigitCode: areaCode && areaCode.length === 6 ? areaCode :
                        (cityCode && cityCode.length === 6 ? cityCode : provinceCode)
                };

                this.$emit('address-change', addressInfo);
            }
            this.$emit('input', value);
        },

        handleVisibleChange(visible) {
            if (visible) {
                // console.log('Cascader opened, current value:', this.selectedValue);
            }
        },

        handleDetailInput(val) {
            this.$emit('detail-change', val);
            this.$emit('update:detailValue', val);
        },

        // 调试函数
        debugCodes() {
            console.log('Available codes:');
            this.addressData.forEach(province => {
                if (province.children) {
                    province.children.forEach(city => {
                        if (city.children) {
                            city.children.forEach(area => {
                                if (area.value === '120102') {
                                    console.log('Found target:', {
                                        province: province.label,
                                        city: city.label,
                                        area: area.label,
                                        codes: [province.value, city.value, area.value]
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
</script>
  
<style lang="less" scoped>
.address-cascader {
    display: flex;
    align-items: flex-start;
    width: 100%;

    .cascader-select {
        width: 280px;
        margin-right: 10px;

        :deep(.el-input__inner) {
            background: #fdf8e7;
            border: 1px solid #dac5a5;
            border-radius: 20px;
            height: 38px;
            line-height: 38px;
            color: #8b6b4d;

            &:focus {
                border-color: #b69d12;
                box-shadow: 0 0 0 2px rgba(182, 157, 18, 0.1);
            }
        }
    }

    .detail-input {
        width: 200px;

        :deep(.el-input__inner) {
            background: #fdf8e7;
            border: 1px solid #dac5a5;
            border-radius: 20px;
            height: 38px;
            line-height: 38px;
            color: #8b6b4d;

            &:focus {
                border-color: #b69d12;
                box-shadow: 0 0 0 2px rgba(182, 157, 18, 0.1);
            }
        }
    }
}
</style>