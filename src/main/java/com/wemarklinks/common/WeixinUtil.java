package com.wemarklinks.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class WeixinUtil {
    
//    public static String getJsapiTicket(String access_token) {
//        String getticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s";// 接口凭据
//        String jsonData = HttpUtil.sendGet(getticket_url + access_token + "&type=jsapi", "utf-8", 30000);
//        
//        JSONObject jsonObj = JSONObject.fromObject(jsonData);
//        String errcode = jsonObj.getString("errcode");
//        String ticket = null;
//        if (errcode.equals("0")) {
//            ticket = jsonObj.getString("ticket");
//        }
//        return ticket;
//    }
    
    /***
     * 获取界面调用jsapi的所需参数
     * 
     * @param jsapi_ticket
     *            凭据
     * @param url
     *            界面请求地址
     * @return V型知识库 www.vxzsk.com
     */
    public static Map<String, Object> sign(String jsapi_ticket, String url) {
        Map<String, Object> ret = new HashMap<String, Object>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";
        
        // 注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        System.out.println(string1);
        
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        
        return ret;
    }
    
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
    
    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }
    
    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
