package com.protocol.core;

import com.protocol.annotation.IProtocolImplProvider;
import com.protocol.annotation.IProtocolProvider;

/**
 * Created by ljq on 2019/5/8
 */
class ProtocolUtil {

    private static final String PROTOCOL_PROVIDER_PACKAGE = "com.protocol.provider";

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
