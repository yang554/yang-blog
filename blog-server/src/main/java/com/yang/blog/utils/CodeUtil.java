package com.yang.blog.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CodeUtil {

    public String getRandomCode(){
        String random=(int)(Math.random()*9+1)*100000+"";
        return random;
    }

    /**
     * 得到length个长度的随机字符
     * @param length
     * @return
     */
    public static String getRandomCode(int length){
        //字符集
        char[] codes=new char[]{'1','2','3','4','5','6','7','8','9','0','a','b','c','d','e','f','g','h','i','j','k','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        String randomCode="";
        for(int i=0;i<length;i++){
            int random=(int)(Math.random()*35);
            randomCode+=codes[random];
        }
        return randomCode;


    }

    public static void main(String[] args) {
        String input = "Java,Python,C++,Java Script,C#,,,,";
        List<String> result = Arrays.stream(input.split(","))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        System.out.println(result);
    }
}
