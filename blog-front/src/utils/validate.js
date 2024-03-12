/**
 * @description 判断是否是手机号
 * @param value
 * @returns {boolean}
 */
export function isPhone(value) {
    const reg = /^1\d{10}$/
    return reg.test(value)
}

// 电话号码验证
export function validatePhone(rule, value, callback) {
    const mobilereg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/ // 手机
    const phonereg = /^0\d{2,3}-?\d{7,8}$/ // 固话
    const noAreaPhonereg = /^[1-9]\d{6,7}$/ // 无区号固话
    rule.phonetype = rule.phonetype || 'mobile'

    if (rule.phonetype === 'mobile') {
        if (value && !mobilereg.test(value))
            callback(new Error("请填写正确的手机号码"))
        else callback()
    }

    if (rule.phonetype === 'phone') {
        if (value && !phonereg.test(value))
            callback(new Error("请填写正确的固话"))
        else callback()
    }

    if (rule.phonetype === 'all') {
        if (value && !phonereg.test(value) && !mobilereg.test(value))
            callback(new Error("号码异常"))
        else callback()
    }

    if (rule.phonetype === 'allAndNoArea') {
        if (
            value &&
            !phonereg.test(value) &&
            !mobilereg.test(value) &&
            !noAreaPhonereg.test(value)
        )
            callback(new Error("号码异常"))
        else callback()
    }
}

/**
 * @description 判断是否是身份证号(第二代)
 * @param value
 * @returns {boolean}
 */
export function isIdCard(value) {
    const reg =
        /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
    return reg.test(value)
}

/**
 * @description 判断是否是邮箱
 * @param value
 * @returns {boolean}
 */
// 邮箱验证
export function validateEmail(rule, value, callback) {
    const reg = /^([a-zA-Z]|[0-9])(\w|\-|\.)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/
    // if (!value) return callback(new Error('请输入收件人邮箱'))
    if (value && !reg.test(value)) {
        callback(new Error("请输入正确的邮箱"))
    } else {
        callback()
    }
}


/**
 * validate IDCard 身份证
 * @returns
 */
export function validateIDCard(val) {
    if (checkCode(val)) {
        var date = val.substring(6, 14)
        if (checkDate(date)) {
            if (checkProv(val.substring(0, 2))) {
                return true
            }
        }
    }
    return false
}
/**
 * check Prov
 */
var checkProv = function (val) {
    var pattern = /^[1-9][0-9]/
    var provs = {
        11: '北京',
        12: '天津',
        13: '河北',
        14: '山西',
        15: '内蒙古',
        21: '辽宁',
        22: '吉林',
        23: '黑龙江 ',
        31: '上海',
        32: '江苏',
        33: '浙江',
        34: '安徽',
        35: '福建',
        36: '江西',
        37: '山东',
        41: '河南',
        42: '湖北 ',
        43: '湖南',
        44: '广东',
        45: '广西',
        46: '海南',
        50: '重庆',
        51: '四川',
        52: '贵州',
        53: '云南',
        54: '西藏 ',
        61: '陕西',
        62: '甘肃',
        63: '青海',
        64: '宁夏',
        65: '新疆',
        71: '台湾',
        81: '香港',
        82: '澳门',
    }
    if (pattern.test(val)) {
        if (provs[val]) {
            return true
        }
    }
    return false
}
/**
 * check Date
 */
var checkDate = function (val) {
    var pattern = /^(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)$/
    if (pattern.test(val)) {
        var year = val.substring(0, 4)
        var month = val.substring(4, 6)
        var date = val.substring(6, 8)
        var date2 = new Date(year + '-' + month + '-' + date)
        if (date2 && date2.getMonth() == parseInt(month) - 1) {
            return true
        }
    }
    return false
}
// 校验码校验
var checkCode = function (val) {
    var p =
        /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/
    var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
    var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2]
    var code = val.substring(17)
    if (p.test(val)) {
        var sum = 0
        for (var i = 0; i < 17; i++) {
            sum += val[i] * factor[i]
        }
        if (parity[sum % 11] == code.toUpperCase()) {
            return true
        }
    }
    return false
}

/**
* @description 判读是否为外链
* @param path
* @returns {boolean}
*/
export function isExternal(path) {
    return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @description 判断是否为IP
 * @param ip
 * @returns {boolean}
 */
export function isIP(ip) {
    const reg =
        /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
    return reg.test(ip)
}

/**
 * @description 判断是否是传统网站
 * @param url
 * @returns {boolean}
 */
export function isUrl(url) {
    const reg =
        /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
    return reg.test(url)
}

/**
 * @description 判断是否是端口号
 * @param value
 * @returns {boolean}
 */
export function isPort(value) {
    const reg =
        /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/
    return reg.test(value)
}

/**
* @description 判断是否是小写字母
* @param value
* @returns {boolean}
*/
export function isLowerCase(value) {
    const reg = /^[a-z]+$/
    return reg.test(value)
}

/**
 * @description 判断是否是大写字母
 * @param value
 * @returns {boolean}
 */
export function isUpperCase(value) {
    const reg = /^[A-Z]+$/
    return reg.test(value)
}

/**
 * @description 判断是否是大写字母开头
 * @param value
 * @returns {boolean}
 */
export function isAlphabets(value) {
    const reg = /^[A-Za-z]+$/
    return reg.test(value)
}

/**
 * @description 判断是否是字符串
 * @param value
 * @returns {boolean}
 */
export function isString(value) {
    return typeof value === 'string' || value instanceof String
}

/**
 * @description 判断是否是数组
 * @param arg
 */
export function isArray(arg) {
    if (typeof Array.isArray === 'undefined') {
        return Object.prototype.toString.call(arg) === '[object Array]'
    }
    return Array.isArray(arg)
}

/**
* @description 判断经度 -180.0～+180.0（整数部分为0～180，必须输入1到5位小数）
* @param value
* @returns {boolean}
*/
export function isLongitude(value) {
    const reg = /^[-|+]?(0?\d{1,2}\.\d{1,5}|1[0-7]?\d\.\d{1,5}|180\.0{1,5})$/
    return reg.test(value)
}

/**
 * @description 判断纬度 -90.0～+90.0（整数部分为0～90，必须输入1到5位小数）
 * @param value
 * @returns {boolean}
 */
export function isLatitude(value) {
    const reg = /^[-|+]?([0-8]?\d\.\d{1,5}|90\.0{1,5})$/
    return reg.test(value)
}

/**
* @description rtsp校验，只要有rtsp://
* @param value
* @returns {boolean}
*/
export function isRTSP(value) {
    const reg =
        /^rtsp:\/\/([a-z]{0,10}:.{0,10}@)?(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
    const reg1 =
        /^rtsp:\/\/([a-z]{0,10}:.{0,10}@)?(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5]):[0-9]{1,5}/
    const reg2 =
        /^rtsp:\/\/([a-z]{0,10}:.{0,10}@)?(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\//
    return reg.test(value) || reg1.test(value) || reg2.test(value)
}

// 只允许输入数字或字母
export function validateIntOrLet(rule, value, callback) {
    const reg = /^[0-9a-zA-Z]+$/
    if (!reg.test(value)) {
        callback(new Error('只允许输入数字或字母'))
    } else {
        callback()
    }
}

// 只允许输入数字
export function validateInt(rule, value, callback) {
    const reg = /^[0-9]+$/
    if (value && !reg.test(value)) {
        callback(new Error("请输入正确的数字"))
    } else if (value && 10 < value.length) {
        callback(new Error("长度不能超过10位"))
    } else {
        callback()
    }
}


// 只允许输入正整数
export function validateIntNumber(rule, value, callback) {
    const reg = /^[1-9]\d*$/
    if (value && !reg.test(value)) {
        callback(new Error("只能输入正整数"))
    } else {
        callback()
    }
}


// 只允许输入正整数或者含有1到4位小数的数字  0/0.0001/123
export function validateFloatNumbertoFixed(rule, value, callback) {
    value = `${value}`.trim()
    let n = Number(value)
    if (isNaN(n)) {
        return callback(new Error('请输入数字'))
    }

    if (n === 0) {
        return callback()
    }

    // 可见小数位
    let { decimalPlaces, message } = rule
    decimalPlaces = decimalPlaces || 4
    // console.log('validateIntNumber:', rule)
    // var reg = /^[1-9]\d*(\.\d{1,4})?$/ //1到4位小数
    const reg = new RegExp(`^\\d+(\\.\\d{1,${decimalPlaces}})?$`)
    if (value && !reg.test(value)) {
        callback(new Error(message || '只允许输入正整数或者含有1到4位小数的数字'))
    } else {
        return callback()
    }
}

