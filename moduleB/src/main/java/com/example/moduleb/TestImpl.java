package com.example.moduleb;

import androidx.annotation.Keep;

import com.protocol.annotation.ProtocolImpl;

/**
 * Created by ljq on 2019/5/8
 */
@ProtocolImpl("ModuleBProtocol")
@Keep
public class TestImpl {

    public String getData() {
        return "来自模块B的数据";
    }

}
