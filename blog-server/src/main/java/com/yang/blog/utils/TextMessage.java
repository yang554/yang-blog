package com.yang.blog.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TextMessage {
    //短信通知的接口请求地址
    protected final String  URL="https://openapi.danmi.com/textSMS/sendSMS/batch/v1";
    //自己的SID
    protected final String ACCOUNT_SID="a828343ffae6ac1018583d87286940a6";
    //自己的AUTH TOKEN
    protected final String AUTH_TOKEN="9854907b03e85df80c8e47e97707224e";

    /**
     * 手机验证码接口
     * @param phone
     * @return
     */
    public TestMsgError getSmsContent(String phone){
        String code=new CodeUtil().getRandomCode();
        //短信模板
        String SMSContent = "验证码为"+code+"，请于3分钟内正确输入，如非本人操作，请忽略次短信。";
        //创建http协议对象
        CloseableHttpClient closeableHttpClient= HttpClients.createDefault();
        // 请求方式
        HttpPost httpPost=new HttpPost(URL);
        //获取时间戳
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp=sdf.format(new Date());
        //获取签名
        String sigString=DigestUtils.md5Hex(ACCOUNT_SID+AUTH_TOKEN+timestamp);
        //定义参数对象
        List<NameValuePair> params=new ArrayList<NameValuePair>();

        BasicNameValuePair account=new BasicNameValuePair("accountSid",ACCOUNT_SID);
        BasicNameValuePair smsContent=new BasicNameValuePair("smsContent",SMSContent);
        BasicNameValuePair to=new BasicNameValuePair("to",phone);
        BasicNameValuePair time=new BasicNameValuePair("timestamp",timestamp);

        BasicNameValuePair sig=new BasicNameValuePair("sig",sigString);
        //添加参数
        params.add(account);
        params.add(smsContent);
        params.add(to);
        params.add(time);
        params.add(sig);
        TestMsgError md=new TestMsgError();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            CloseableHttpResponse response= closeableHttpClient.execute(httpPost);
            HttpEntity entity=response.getEntity();
        } catch (Exception e) {
            e.printStackTrace();
            return md;
        }
        md.setPhone(phone);
        md.setCode(code);
        return md;
    }

    public static void main(String[] args) {
        TextMessage p=new TextMessage();
        System.out.println(p.getSmsContent("15723084621") + "'========'");
    }

}
