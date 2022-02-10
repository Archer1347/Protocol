package com.protocol.core;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ljq on 2019/5/8
 */
public final class ProtocolFactory {

    private static class Instance {
        private final static ProtocolFactory instance = new ProtocolFactory();
    }

    public static ProtocolFactory getInstance() {
        return Instance.instance;
    }

    /**
     * 动态代理+反射
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(Class<T> protocolClass) {
        Class<?> protocolImplClass = ProtocolUtil.getProtocolImplClass(ProtocolUtil.getProtocol(protocolClass));
        Object protocol = ProtocolUtil.getProtocolImpl(protocolImplClass);
        if (protocolImplClass == null || protocol == null) {
            Log.e("ProtocolFactory", "未找到实现类: " + protocolClass.getCanonicalName());
        }
        return (T) Proxy.newProxyInstance(protocolClass.getClassLoader(), new Class<?>[]{protocolClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (protocolImplClass == null || protocol == null) {
                    return null;
                }
                return protocolImplClass.getMethod(method.getName(), method.getParameterTypes()).invoke(protocol, args);
            }
        });
    }
}
