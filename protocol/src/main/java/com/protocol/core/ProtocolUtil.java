package com.protocol.core;

import com.protocol.annotation.IProtocolImplProvider;
import com.protocol.annotation.IProtocolProvider;

/**
 * Protocol接口、协议（Protocol注解值）、实现类的相关工具类
 * Created by ljq on 2022/2/10
 */
public class ProtocolUtil {

    // 生成的类全部在这个路径下
    public static final String PROTOCOL_PROVIDER_PACKAGE = "com.protocol.provider";

    /**
     * 获取协议地址（Protocol注解值）
     *
     * @param protocolClass Protocol接口
     */
    public static String getProtocol(Class<?> protocolClass) {
        try {
            Class<?> clz = Class.forName(PROTOCOL_PROVIDER_PACKAGE + "." + protocolClass.getSimpleName() + "$$Protocol$$Get");
            Object instance = clz.newInstance();
            IProtocolProvider protocolProvider = (IProtocolProvider) instance;
            return protocolProvider.getProtocol();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Protocol实现类
     *
     * @param protocol Protocol注解值
     */
    public static Class<?> getProtocolImplClass(String protocol) {
        if (protocol == null) return null;
        try {
            String protocolPath = PROTOCOL_PROVIDER_PACKAGE + "." + protocol + "$$ProtocolImpl$$Get";
            Class<?> providerClass = Class.forName(protocolPath);
            IProtocolImplProvider provider = (IProtocolImplProvider) providerClass.newInstance();
            return Class.forName(provider.getProtocolImpl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Protocol实现对象
     *
     * @param protocolImplClass Protocol实现类
     */
    public static Object getProtocolImpl(Class<?> protocolImplClass) {
        if (protocolImplClass == null) return null;
        try {
            return protocolImplClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
