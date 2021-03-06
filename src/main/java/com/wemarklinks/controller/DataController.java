package com.wemarklinks.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wemarklinks.common.AESUtil;
import com.wemarklinks.common.JsonResult;
import com.wemarklinks.common.ResultCode;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

@RestController
@RequestMapping("/data")
public class DataController {
    
    // password
    private byte[] password = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06};
    private byte[] new_password = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06};

    //init token
    private byte[] token = new byte[4];

    //openlock byte[]
    private byte[] openlock = new byte[]{0x05,0x01,0x06,0x30,0x30,0x30,0x30,0x30,
                                         0x30,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};
    //获取token的byte[]
    byte[] access_token = new byte[]{0x06,0x01,0x01,0x01,0x5C,0x01,0x21,0x1F,
                                     0x29,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};

    //获取lock status的byte[]
    byte[] lock_status = new byte[]{0x05,0x0E,0x01,0x01,0x5C,0x01,0x21,0x1F,
                                    0x29,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};

    //开仓的byte[]
    byte[] open_store = new byte[]{0x10,0x01,0x06,0x30,0x30,0x30,0x30,0x30,
                                   0x30,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};

    //获取电量的byte[]
    byte[] access_power = new byte[]{0x02,0x01,0x01,0x01,0x30,0x30,0x30,0x30,
                                     0x30,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};

    //修改密码
    byte[] change_password_1 = new byte[]{0x05,0x03,0x06,0x01,0x30,0x30,0x30,0x30,
                                          0x30,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};
    byte[] change_password_2 = new byte[]{0x05,0x04,0x06,0x01,0x30,0x30,0x30,0x30,
                                          0x30,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};

    // 查询工作状态
    byte[] full_status = new byte[]{0x05,0x22,0x01,0x00,0x30,0x30,0x30,0x30,
                                    0x30,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};
    
    //秘钥
    public final static byte[] PRIVATE_AES = new byte[]{0x3A,0x60,0x43,0x2A,0x5C,0x01,0x21,0x1F,
            0x29,0x1E,0x0F,0x4E,0x0C,0x13,0x28,0x25};
    
    @RequestMapping(value = "/accessToken", method = RequestMethod.GET)
    @ApiOperation(value = "获取令牌")
    public Map<String,Object> accessToken(){
        byte[] encrypt = AESUtil.Encrypt(access_token, PRIVATE_AES);
        return JsonResult.RetJsone(ResultCode.SUCCESS, "success", bytesToString(encrypt));
    }
    
    @RequestMapping(value = "/openLock",method = RequestMethod.POST)
    @ApiOperation(value = "开锁")
    public Map<String, Object> openLock(@RequestBody WxData data){
        byte[] bytes = stringToBytes(data.byteArray);
        byte[] decrypt = AESUtil.Decrypt(bytes, PRIVATE_AES);
        
        access_token[0] = decrypt[3];
        access_token[1] = decrypt[4];
        access_token[2] = decrypt[5];
        access_token[3] = decrypt[6];
        
        openlock[3] = password[0];
        openlock[4] = password[1];
        openlock[5] = password[2];
        openlock[6] = password[3];
        openlock[7] = password[4];
        openlock[8] = password[5];
        openlock[9] = access_token[0];
        openlock[10] = access_token[1];
        openlock[11] = access_token[2];
        openlock[12] = access_token[3];
        
        byte[] encrypt = AESUtil.Encrypt(openlock, PRIVATE_AES);
        String bytesToString = bytesToString(encrypt);
        return JsonResult.RetJsone(ResultCode.SUCCESS, "success", bytesToString);
    }
    
    public byte[] stringToBytes(String byteArray) {
        String[] split = byteArray.split(",");
        byte[] bytes = new byte[16];
        for(int i = 0 ; i < split.length ; i++) {
            bytes[i] = Byte.valueOf(split[i]);
        }
        return bytes;
    }
    
    public String bytesToString(byte[] bytes) {
        String str = "[";
        for(int i = 0 ; i<bytes.length ;i++) {
            if(i != bytes.length -1) {
                str += bytes[i] + ",";
            }else {
                str += bytes[i];
            }
        }
        str += "]";
        return str;
    }
    
}

@Data
class WxData{
    public String byteArray;
}
