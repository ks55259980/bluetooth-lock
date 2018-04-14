package com.wemarklinks.controller;

public class Controller {
    public static void main(String[] args) {
        
        byte[] PRIVATE_AES = new byte[] { 0x3A, 0x60, 0x43, 0x2A, 0x5C, 0x01, 0x21, 0x1F, 0x29, 0x1E, 0x0F, 0x4E, 0x0C,
                0x13, 0x28, 0x25 };
        String str = "[";
        for(int i = 0 ; i<PRIVATE_AES.length ;i++) {
            if(i != PRIVATE_AES.length -1) {
                str += PRIVATE_AES[i] + ",";
            }else {
                str += PRIVATE_AES[i];
            }
            
        }
        str += "]";
        System.out.println(str);
    }
}
