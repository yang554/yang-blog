package com.yang.blog.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import sun.applet.Main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PinYinUtil {
    /**
     * @param china (字符串 汉字)
     * @return 汉字转拼音 其它字符不变
     */
    public static String getPinyin(String china){
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = china.trim().toCharArray();

        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR)+"";
        String month = (now.get(Calendar.MONTH) + 1) + "";
        String day = now.get(Calendar.DAY_OF_MONTH)+"";
        String result = "";
        try {
            for (int i=0;i<arrays.length;i++) {
                char ti = arrays[i];
                if(Character.toString(ti).matches("[\\u4e00-\\u9fa5]")){ //匹配是否是中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ti,formart);
                    result += temp[0];
                }else{
                    result += ti;
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return result;//+year+month+day;
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = sdf.parse("2021-11-14");//2021-11-14
        Date endDate = sdf.parse("2021-11-25");//2021-11-25
        String nowDate = sdf.format(new Date());
        Date date = sdf.parse(nowDate);//2021-11-25

        if((endDate.after(date) || endDate.equals(date)) && (date.after(beginDate) || date.equals(beginDate))){
            System.out.println(beginDate+"---"+date+"----"+endDate+"正常");
        }else if(beginDate.after(date) && (endDate.after(beginDate) || endDate.equals(beginDate))){
            System.out.println(beginDate+"---"+date+"----"+endDate+"未到时间");
        }else if(date.after(endDate) && !date.equals(endDate)){
            System.out.println(beginDate+"---"+date+"----"+endDate+"已过期");
        }else{
            System.out.println(beginDate+"---"+date+"----"+endDate+"未知");
        }
    }
}
