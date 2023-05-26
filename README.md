Android模块化通信协议Protocol

原理：
1、通过注解+APT将接口类与实现类生成映射类
3、动态代理+反射调用

protocol -- Protocol入口

protocol-annotation -- 注解+接口module

protocol-compiler -- ksp：生成注解代码

使用姿势：
根build.gradle
```
repositories {
    google()
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
}
```
module依赖：
```
api 'com.github.Archer1347.Protocol:protocol:1.0.1'
ksp 'com.github.Archer1347.Protocol:protocol-compiler:1.0.1'
```
例子：模块1调用模块2的代码

模块1：
创建接口
```
@Protocol("通信协议字符串，保证全局唯一即可")
public interface 接口类{

   void method1(参数1,参数2...)

   返回值 method2()

}
```
模块2：
创建实现类
```
@ProtocolImpl("通信协议字符串，必须与模块1的字符串一致")
public class 实现类{

    //方法名与参数必须与模块1的方法名与参数一致
    public void method1(参数1,参数2){
        // do something
    }

    public 返回值 method2(){
        //do something
    return 返回值
    }
}
```
模块1调用模块2
```
ProtocolFactory.getInstance().invoke(接口类.class).method1(参数1,参数2)
返回值 = ProtocolFactory.getInstance().invoke(接口类.class).method2()
```
混淆配置:
Protocol生成中间代码
```
-keep class com.protocol.provider.** { *; }
```
Protocol注解的类需要过滤混淆
