package com.wemarklinks.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemarklinks.common.CommonUtil;
import com.wemarklinks.common.HttpUtil;
import com.wemarklinks.common.JsonResult;
import com.wemarklinks.common.JsonUtil;
import com.wemarklinks.common.JsonUtils;
import com.wemarklinks.common.RedisUtil;
import com.wemarklinks.common.ResultCode;
import com.wemarklinks.common.WeixinUtil;
import com.wemarklinks.component.AesException;
import com.wemarklinks.component.SHA1;
import com.wemarklinks.config.IOTWeChatConfig;
import com.wemarklinks.pojo.BluetoothLock;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController()
@RequestMapping("/wechat")
public class WeChatController {
    
    private static final Logger log = LoggerFactory.getLogger(WeChatController.class);
    
    public static final int PRODUCT_ID = 46041;
    
    @Autowired
    private RedisUtil redis;
    
    @Autowired
    private IOTWeChatConfig IOTWeChatConfig;
    
    @RequestMapping(value = "/validateToken", method = RequestMethod.GET)
    @ApiOperation(value = "一 验证服务器")
    public String validateToken(@ApiParam(value = "加密签名", required = true) @RequestParam String signature,
            @ApiParam(value = "随机字符", required = true) @RequestParam String echostr,
            @ApiParam(value = "时间戳", required = true) @RequestParam String timestamp,
            @ApiParam(value = "随机数", required = true) @RequestParam String nonce) {
        try {
            String msg_signature = SHA1.getSHA1(IOTWeChatConfig.getToken(), timestamp, nonce, null);
            if (msg_signature.equals(signature)) {
                return echostr;
            }
        } catch (AesException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 该接口用于接收微信的各种通知
     */
    @RequestMapping(value = "/validateToken", method = RequestMethod.POST)
    @ApiOperation(value = "接受微信回调")
    public String validateToken(String signature, String timestamp, String nonce, String openid,
            HttpServletRequest request, HttpServletResponse response) {
        String xmlFromWechatServer = getXmlFromWechatServer(request);
        System.out.println(xmlFromWechatServer);
        Map<String, String> map = CommonUtil.convertXmlToMap(xmlFromWechatServer);
        System.out.println(map);
        return null;
    }
    
    private String getXmlFromWechatServer(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            request.getReader();
            br = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("cannot get encrypted xml from wechat server, detail: {}", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    @RequestMapping(value = "/getAccessToken", method = RequestMethod.GET)
    @ApiOperation(value = "二 获取accessToken")
    public String getAccessToken() {
        Object object = redis.get("access_token");
        if (object != null) {
            return object.toString();
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        String format = String.format(url, IOTWeChatConfig.getAppID(), IOTWeChatConfig.getAppsecret());
        String string = HttpUtil.get(format);
        Map map = JsonUtils.convertToT(string, Map.class);
        String accessToken = (String) map.get("access_token");
        if (accessToken != null) {
            redis.set("access_token", accessToken, 7200L);
            return accessToken.toString();
        } else {
            log.warn("fail to get access_token : {}", map);
            return null;
        }
    }
    
    @RequestMapping(value = "/accessQrUrl", method = RequestMethod.POST)
    @ApiOperation(value = "三 获取deviceId和二维码tiket")
    public Map<String, Object> accessQrUrl() {
        // if (redis.get("deviceid") != null) {
        // Map<String, Object> map = new HashMap<String, Object>();
        // map.put("deviceid", redis.get("deviceid"));
        // map.put("qrticket", redis.get("qrticket"));
        // return map;
        // }
        
        String url = "https://api.weixin.qq.com/device/getqrcode?access_token=%s&product_id=%s";
        String format = String.format(url, getAccessToken(), PRODUCT_ID);
        String string = HttpUtil.get(format);
        Map<String, Object> map = JsonUtil.jsonToMap(string);
        System.out.println(map);
        Map<String, Object> base_resp = (Map<String, Object>) map.get("base_resp");
        if (base_resp != null) {
            Integer errcode = (Integer) base_resp.get("errcode");
            if (errcode != 0) {
                return base_resp;
            }
        }
        redis.set("deviceid", (String) map.get("deviceid"));
        redis.set("qrticket", map.get("qrticket"));
        
        return map;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/authDevice", method = RequestMethod.GET)
    @ApiOperation(value = "四  设备授权")
    public String authDevice() {
        
        String url = "https://api.weixin.qq.com/device/authorize_device?access_token=%s";
        String format = String.format(url, getAccessToken());
        
        BluetoothLock lock = new BluetoothLock();
        lock.setId((String) redis.get("deviceid"));
        // lock.setId("gh_a397d76571e4_3ac425b69a3a9e92");
        lock.setMac("3CA30891204A");
        List<BluetoothLock> list = new ArrayList<>();
        list.add(lock);
        
        ObjectMapper om = new ObjectMapper();
        String asString = "";
        try {
            asString = om.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(asString);
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("access_token", getAccessToken());
        map.put("device_num", 1);
        map.put("device_list", list);
        // 请求操作的类型，限定取值为：0：设备授权（缺省值为0） 1：设备更新（更新已授权设备的各属性值）
        map.put("op_type", 1);
        // 当 op_type 为 1 时，不要填写 product_id 字段。
        // map.put("product_id", PRODUCT_ID);
        
        String jsonStr = "";
        try {
            jsonStr = om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(jsonStr);
        // String[] segement = { "device", "authorize_device" };
        // String post = HttpUtil.post(HttpUtil.Schema.HTTPS,
        // "api.weixin.qq.com", 443, segement, null, map);
        
        String post = HttpUtil.postJson(format, jsonStr);
        System.out.println(post);
        if (post == null) {
            return "post is null";
        }
        
        Map<String, Object> responseMap = JsonUtil.jsonToMap(post);
        
        // 错误回复
        if (responseMap.get("errcode") != null) {
            return responseMap.toString();
        }
        
        List<Map<String, Object>> resp = (List<Map<String, Object>>) responseMap.get("resp");
        Integer errcode = (Integer) resp.get(0).get("errcode");
        if (errcode != 0) {
            return resp.toString();
        }
        
        return responseMap.toString();
    }
    
    // V型知识库 www.vxzsk.com
    @RequestMapping(value = "/goReadCardAnniu", method = RequestMethod.GET)
    @ApiOperation(value = "五 获取jsapi_ticket")
    public Map<String, Object> goReadCardAnniu2(HttpServletRequest request, HttpServletResponse response) {
        String jsapi_ticket = "" ;
        if(redis.get("jsapi_ticket") != null) {
            jsapi_ticket = (String)redis.get("jsapi_ticket");
        }else {
            // 1,获取access_token
            String access_token = getAccessToken();
            // 2,获取调用微信jsapi的凭证, jsapi_ticket的有效期为7200秒，通过access_token来获取
            String getticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";// 接口凭据
            String format = String.format(getticket_url, access_token);
            String resStr = HttpUtil.get(format);
            Map<String, Object> resMap = JsonUtil.jsonToMap(resStr);
            System.out.println(resMap);
            if(((Integer)resMap.get("errcode")).equals(0)) {
                jsapi_ticket = (String)resMap.get("ticket");
                redis.set("jsapi_ticket", (String)resMap.get("ticket"), Long.valueOf((Integer)resMap.get("expires_in")-300));
            }else {
                return JsonResult.RetJsone(ResultCode.EXCEPTION, "未能获取jsapi_token", "");
            }
        }

        
        String appId = IOTWeChatConfig.getAppID();// 应用id
//        String appsecret = IOTWeChatConfig.getAppsecret();// 应用秘钥
        String url = "http://lock.dpdaidai.top/test.html";
        String ur = request.getServerName();
        System.out.println(ur);
        System.out.println(request.getContextPath());
        System.out.println(request.getServletPath());
        Map<String, Object> map = WeixinUtil.sign(jsapi_ticket , url);
        map.put("appId",appId);
        return JsonResult.RetJsone(ResultCode.SUCCESS, "success", map);
    }
    
    @RequestMapping(value = "/creatButton", method = RequestMethod.GET)
    @ApiOperation(value = "新建菜单")
    public String creatButton() {
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
        String format = String.format(url, getAccessToken());
        
        String button = "{\r\n" + 
                "    \"button\": [\r\n" + 
                "        {\r\n" + 
                "            \"name\": \"扫码\", \r\n" + 
                "            \"sub_button\": [\r\n" + 
                "                {\r\n" + 
                "                    \"type\": \"scancode_waitmsg\", \r\n" + 
                "                    \"name\": \"扫码带提示\", \r\n" + 
                "                    \"key\": \"rselfmenu_0_0\", \r\n" + 
                "                    \"sub_button\": [ ]\r\n" + 
                "                }, \r\n" + 
                "                {\r\n" + 
                "                    \"type\": \"scancode_push\", \r\n" + 
                "                    \"name\": \"扫码推事件\", \r\n" + 
                "                    \"key\": \"rselfmenu_0_1\", \r\n" + 
                "                    \"sub_button\": [ ]\r\n" + 
                "                }\r\n" + 
                "            ]\r\n" + 
                "        }, \r\n" + 
                "        {\r\n" + 
                "            \"name\": \"发图\", \r\n" + 
                "            \"sub_button\": [\r\n" + 
                "                {\r\n" + 
                "                    \"type\": \"pic_sysphoto\", \r\n" + 
                "                    \"name\": \"系统拍照发图\", \r\n" + 
                "                    \"key\": \"rselfmenu_1_0\", \r\n" + 
                "                   \"sub_button\": [ ]\r\n" + 
                "                 }, \r\n" + 
                "                {\r\n" + 
                "                    \"type\": \"pic_photo_or_album\", \r\n" + 
                "                    \"name\": \"拍照或者相册发图\", \r\n" + 
                "                    \"key\": \"rselfmenu_1_1\", \r\n" + 
                "                    \"sub_button\": [ ]\r\n" + 
                "                }, \r\n" + 
                "                {\r\n" + 
                "                    \"type\": \"pic_weixin\", \r\n" + 
                "                    \"name\": \"微信相册发图\", \r\n" + 
                "                    \"key\": \"rselfmenu_1_2\", \r\n" + 
                "                    \"sub_button\": [ ]\r\n" + 
                "                }\r\n" + 
                "            ]\r\n" + 
                "        }, \r\n" + 
                "        {\r\n" + 
                "            \"name\": \"蓝牙控制\", \r\n" + 
                "            \"type\": \"view\", \r\n" + 
                "            \"url\": \"http://lock.dpdaidai.top/test.html\"\r\n" + 
                "        }\r\n" + 
                "    ]\r\n" + 
                "}";
        String postJson = HttpUtil.postJson(format, button);
        return postJson;
    }
    
    @RequestMapping(value = "/uploadWxImg",method = RequestMethod.GET)
    public Map<String, Object> uploadWxImg(String serverId) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";
        String format = String.format(url, getAccessToken(),serverId);
        System.out.println(format);
        
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("serverId", serverId);
        map.put("token", getAccessToken());
        
        return JsonResult.RetJsone(ResultCode.SUCCESS, "", map);
    }
    
}
