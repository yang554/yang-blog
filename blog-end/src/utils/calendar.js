import { getLunar } from 'chinese-lunar-calendar'

// 获取该月的天数
export const getDaysInMonth = (year, month) => {
    const day = new Date(year, month + 1, 0)
    return day.getDate()
}

// 创建日期 yyyy-MM-dd 格式， 用于创建非当前月的日期
export const handleCrateDate = (year, month, start, end, type) => {
    const arr = []
    if (type === 'prev') { // 上一月
        if (start === end) return []
        const daysInLastMonth = getDaysInMonth(year, month - 1) // 获取上一个月有多少天
        for (let i = daysInLastMonth - end + 2; i <= daysInLastMonth; i++) {
            arr.push({
                // date: `${month === 0 ? year - 1 : year}-${(month + 1) < 10 ? month === 0 ? 12 : `0${month}` : month}-${i < 10 ? `0${i}` : i}`,
                date: parseTime(new Date(year, month - 1, i)),
                nl:washoldDay(new Date(year, month-1, i)),
                day: i,
                month: month-1,
                isCurMonth: false,
                isSelected: false,
                isRangeSelected: false,
                isCurrentDay: false,
                isCurDay: false,
                flag: "prev"
            })
        }
    } else if (type === 'rear') { // 下一月
        for (let i = start; i <= end; i++) {
            arr.push({
                // date: `${month === 11 ? year + 1 : year}-${(month + 1) < 9 ? `0${month + 2}` : month + 2 <= 12 ? month + 2 : (month + 2) % 12 < 10 ? `0${(month + 2) % 12}` : (month + 2) % 12}-${i < 10 ? `0${i}` : i}`,
                date: parseTime(new Date(year, month + 1, i)),
                nl:washoldDay(new Date(year, month+1, i)),
                day: i,
                month: month+1,
                isCurMonth: false,
                isSelected: false,
                isRangeSelected: false,
                isCurrentDay: false,
                isCurDay: false,
                flag: "rear"
            })
        }
    } else { // 本月
        for (let i = start; i <= end; i++) {
            arr.push({
                date: parseTime(new Date(year, month, i)),
                nl: washoldDay(new Date(year, month, i)),
                day: i,
                month: month,
                isCurMonth: true,
                isSelected: false,
                isRangeSelected: false,
                isCurrentDay: false,
                isCurDay: false,
                flag: "cur"
            })
        }
    }
    return arr
}

export const handleCreateDatePicker = () => {
    const years = []
    const months = []
    for (let i = 1970; i <= 2099; i++) {
        years.push({
            label: `${i}年`,
            value: i
        })
    }
    for (let i = 0; i <= 11; i++) {
        months.push({
            label: `${i + 1}月`,
            value: i
        })
    }
    return {
        years,
        months
    }
}

export function washoldDay(time) {
    let date = new Date(time)
    let y = date.getFullYear()
    let m = date.getMonth() + 1
    let d = date.getDate()
    let result = getLunar(y,m,d).dateStr.split("月")[1]
    // if(intDay == 6 || intDay == 0){
    //   console.log("============")
    //   this.result = "休"
    // }
    return result
}

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string | null}
 */
export function parseTime(time, cFormat) {
    if (arguments.length === 0 || !time) {
        return null
    }
    const format = cFormat || '{y}-{m}-{d}'
    let date
    if (typeof time === 'object') {
        date = time
    } else {
        if ((typeof time === 'string')) {
            if ((/^[0-9]+$/.test(time))) {
                // support "1548221490638"
                time = parseInt(time)
            } else {
                // support safari
                // https://stackoverflow.com/questions/4310953/invalid-date-in-safari
                time = time.replace(new RegExp(/-/gm), '/')
            }
        }

        if ((typeof time === 'number') && (time.toString().length === 10)) {
            time = time * 1000
        }
        date = new Date(time)
    }
    const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay()
    }
    const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
        const value = formatObj[key]
        // Note: getDay() returns 0 on Sunday
        if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
        return value.toString().padStart(2, '0')
    })

    return time_str
}

export function parseDateTime(time, pattern) {
    if (arguments.length === 0 || !time) {
      return null
    }
    const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
    let date
    if (typeof time === 'object') {
      date = time
    } else {
      if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
        time = parseInt(time)
      } else if (typeof time === 'string') {
        time = time
          .replace(new RegExp(/-/gm), '/')
          .replace('T', ' ')
          .replace(new RegExp(/\.[\d]{3}/gm), '')
      }
      if (typeof time === 'number' && time.toString().length === 10) {
        time = time * 1000
      }
      date = new Date(time)
    }
    const formatObj = {
      y: date.getFullYear(),
      m: date.getMonth() + 1,
      d: date.getDate(),
      h: date.getHours(),
      i: date.getMinutes(),
      s: date.getSeconds(),
      a: date.getDay()
    }
    const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
      let value = formatObj[key]
      // Note: getDay() returns 0 on Sunday
      if (key === 'a') {
        return ['日', '一', '二', '三', '四', '五', '六'][value]
      }
      if (result.length > 0 && value < 10) {
        value = '0' + value
      }
      return value || 0
    })
    return time_str
  }