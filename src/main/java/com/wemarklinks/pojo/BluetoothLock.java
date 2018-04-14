package com.wemarklinks.pojo;

import lombok.Builder.Default;
import lombok.Data;

@Data
public class BluetoothLock {
    
    private String id;
    private String mac;
    
    // android classic bluetooth – 1 , ios classic bluetooth – 2 ,  ble – 3 , wifi -- 4 
    private int connect_protocol = 3;
    
    //"3A60432A5C01211F291E0F4E0C132825"
    private String auth_key = "";
    
    //断开策略，目前支持： 1：退出公众号页面时即断开连接 2：退出公众号之后保持连接不断开
    private int close_strategy = 1;
    //连接策略，32位整型，按bit位置位
    private int conn_strategy = 1;
    //auth加密方法，目前支持两种取值： 0：不加密 1：AES加密（CBC模式，PKCS7填充方式）
    private int crypt_method = 0;
    //设备和微信进行auth时，会根据该版本号来确认auth buf和auth key的格式,  0：不加密的version 1：version 1
    private int auth_ver = 0;
    //表示mac地址在厂商广播manufature data里含有mac地址的偏移，取值如下： -1：在尾部、 -2：表示不包含mac地址 其他：非法偏移
    private int manu_mac_pos = -1;
    //表示mac地址在厂商serial number里含有mac地址的偏移，取值如下： -1：表示在尾部 -2：表示不包含mac地址 其他：非法偏移
    private int ser_mac_pos = -2;
    //
    private int ble_simple_protocol;
    
}
