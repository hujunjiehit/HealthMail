#############################################
#
# 对于一些基本指令的添加
#
#############################################
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

-ignorewarnings

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*


-dontwarn okio.**

#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.widget.*
-keep public class com.android.vending.licensing.ILicensingService






# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-keep public class * extends androidx.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

##保留model不被混淆
#-keep public class * extends com.coinbene.manbiwang.api.base.BaseResponse{
#    *;
#}
#-keep public class * extends com.coinbene.manbiwang.common.model.BaseModel{
#    *;
#}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}


-keepattributes *JavascriptInterface*
#-keepclassmembers class * extends android.webkit.webViewClient {
#    public void *(android.webkit.webView, java.lang.String);
#    public void *(android.webkit.webView, android.webkit.SslErrorHandler, android.net.http.SslError);
#}
-keepclassmembers class * extends android.webkit.webViewClient {
  *;
}
#-keepclassmembers class * extends android.webkit.WebChromeClient{
#   *;
#}
-keep class com.show.you.WebVerfiedActivity{ *; }
-keep class android.webkit.**{ *; }
-keep class * extends android.webkit.**{ *; }

# Bugly
-dontwarn com.tencent.bugly.**
-keep class com.tencent.bugly.** {*;}

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


#novate网络请求
 -keep class com.tamic.novate.** {*;}


 #zxing
 -keep class com.google.zxing.** {*;}
 -dontwarn com.google.zxing.**

# -keep class com.growingio.** {
#     *;
# }
# -dontwarn com.growingio.**

 -keepnames class * extends android.view.View
 -keepnames class * extends android.app.Fragment
 -keepnames class * extends android.support.v4.app.Fragment
 -keepnames class * extends androidx.fragment.app.Fragment

-keep class android.support.v4.view.ViewPager{
    *;
}
-keep class android.support.v4.view.ViewPager$**{
	*;
}
-keep class androidx.viewpager.widget.ViewPager{
    *;
}
-keep class androidx.viewpager.widget.ViewPager$**{
	*;
}

#for androidx
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

 -dontwarn javax.annotation.**
 -dontwarn javax.inject.**
 # OkHttp3
 -dontwarn okhttp3.logging.**
 -keep class okhttp3.internal.**{*;}
 -dontwarn okio.**
 # Retrofit
 -dontwarn retrofit2.**
 -keep class retrofit2.** { *; }

 # RxJava RxAndroid
 -dontwarn sun.misc.**
 -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
     long producerIndex;
     long consumerIndex;
 }
 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
     rx.internal.util.atomic.LinkedQueueNode producerNode;
 }
 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
     rx.internal.util.atomic.LinkedQueueNode consumerNode;
 }

 # Gson
 -keep class com.google.gson.stream.** { *; }

 ##---------------Begin: proguard configuration for Gson  ----------
 # Gson uses generic type information stored in a class file when working with fields. Proguard
 # removes such information by default, so configure it to keep all of it.
 -keepattributes Signature

 # For using GSON @Expose annotation
 -keepattributes *Annotation*

 # Gson specific classes
 -keep class sun.misc.Unsafe { *; }
 #-keep class com.google.gson.stream.** { *; }

 # Application classes that will be serialized/deserialized over Gson
# -keep class com.coinbene.manbiwang.api.market.model.** { *; }
# -keep class com.coinbene.manbiwang.api.bookOrder.model.** { *; }
# -keep class com.coinbene.manbiwang.api.coinaddress.model.** { *; }
# -keep class com.coinbene.manbiwang.api.coininfo.model.** { *; }
# -keep class com.coinbene.manbiwang.api.other.model.** { *; }
# -keep class com.coinbene.manbiwang.api.user.model.** { *; }
# -keep class com.coinbene.manbiwang.apiModel.** { *; }
# -keep class com.coinbene.manbiwang.common.model.**{ *; }
# -keep class com.coinbene.manbiwang.function.placeOrder.popup.**{ *; }


#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

-keep class com.wordplat.ikvstockchart.** { *; }

-keep class mpchart.com.coinbene.patternlocker.** {*;}

-keep class com.wei.android.lib.fingerprintidentify.** {*;}

# MeiZuFingerprint
-keep class com.fingerprints.service.** { *; }
# SmsungFingerprint
-keep class com.samsung.android.** { *; }
#-keep class com.samsung.android.** { *; }


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class com.taobao.securityjni.**{*;}
-keep class com.taobao.wireless.security.**{*;}
-keep class com.ut.secbody.**{*;}
-keep class com.taobao.dp.**{*;}
-keep class com.alibaba.wireless.security.**{*;}
-keep class com.alibaba.verificationsdk.**{*;}
-keep interface com.alibaba.verificationsdk.ui.IActivityCallback

#---------------- React Native start----------
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

-keepclassmembers @com.facebook.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}
-keep class com.facebook.react.devsupport.** { *; }
-dontwarn com.facebook.react.devsupport.**

-keep class * extends com.facebook.react.bridge.JavaScriptModule { *; }
-keep class * extends com.facebook.react.bridge.NativeModule { *; }
-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.UIProp <fields>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactProp <methods>; }
-keepclassmembers class *  { @com.facebook.react.uimanager.annotations.ReactPropGroup <methods>; }

-dontwarn com.facebook.react.**

# okhttp

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# okio

-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
#-------------React Native end-----------------


#shareSDK

-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-keep class m.framework.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**
-dontwarn **.R$*


#个推

-keep class com.igexin.**{*;}
#-keep org.json.**{*;}
#-dontwarn class com.igexin.**
#-dontwarn com.coinbene.manbiwang.notification.**

#udesk
-keep class udesk.** {*;}
-keep class cn.udesk.**{*; }

#七牛
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

#smack
-keep class org.jxmpp.** {*;}
-keep class de.measite.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.xmlpull.** {*;}
-dontwarn org.xbill.**
-keep class org.xbill.** {*;}

#eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#freso
-keep class com.facebook.** {*; }
-keep class com.facebook.imagepipeline.** {*; }
-keep class com.facebook.animated.gif.** {*; }
-keep class com.facebook.drawee.** {*; }
-keep class com.facebook.drawee.backends.pipeline.** {*; }
-keep class com.facebook.imagepipeline.** {*; }
-keep class bolts.** {*; }
-keep class me.relex.photodraweeview.** {*; }

-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}
# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**



# agora
-keep class io.agora.**{*;}

-dontwarn io.realm.**



-dontwarn android.support.**
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}

#保持使用了Keep注解的方法以及类不被混淆
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
#保持使用了Keep注解的成员域以及类不被混淆
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}


#web需更改
-keep class com.coinbene.manbiwang.modules.common.web.**{*;}
-keep class com.coinbene.common.database.**{ *; }
#-keep class com.coinbene.manbiwang.model.http.**{ *; }
#-keep class com.coinbene.manbiwang.model.websocket.**{ *; }
-keep class com.coinbene.manbiwang.model.**{ *; }

# -keep class com.coinbene.common.websocket.model.**{ *; }
-keep class com.coinbene.manbiwang.modules.common.share.**{*;}
-keep class com.coinbene.manbiwang.push.**{*;}



#k线
-keep public class * extends com.github.mikephil.coinbene.components.MarkerView
-keep public class * extends com.github.mikephil.coinbene.charts.CombinedChart
-keep public class * implements com.github.mikephil.coinbene.listener.ChartTouchListener
-keep public class * implements com.github.mikephil.coinbene.listener.OnChartGestureListener

-keep public class * extends com.github.mikephil.coinbene.listener.ChartTouchListener
-keep public class * extends com.github.mikephil.coinbene.listener.OnChartGestureListener
-keep public class * extends com.github.mikephil.charting.listener.ChartTouchListener
-keep public class * extends com.github.mikephil.charting.listener.OnChartGestureListener
#-keep class com.coinbene.manbiwang.modules.option.CBoptionSdkActivity
-keep class com.coinbene.manbiwang.modules.option.IOptionManager
-keep class com.github.mikephil.coinbene.** { *; }
-keep class com.github.mikephil.charting.** { *; }
#-keep class com.coinbene.manbiwang.modules.kline.bean.**{ *; }

#route
-keep class * implements com.coinbene.common.router.UIRouter {*;}

#信鸽
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}
-keep class com.tencent.bigdata.** {* ;}

#华为推送
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}

#小米推送
-keep class com.xiaomi.**{*;}
-keep public class * extends com.xiaomi.mipush.sdk.PushMessageReceiver

#魅族推送
-dontwarn com.meizu.cloud.pushsdk.**
-keep class com.meizu.cloud.pushsdk.**{*;}

#Banner
-keep class com.youth.banner.** { *;}

-keep public class com.coinbene.manbiwang.push.PushReceiver


#keep住所有以com.coinbene.common.aspect.annotation包名下的注解标注的方法和类,Aspect需要用到
#keepclassmembers 混淆方法不包含类名
-keepclasseswithmembers class * {
    @com.coinbene.common.aspect.annotation.* <methods>;
}

-keep class me.drakeet.multitype.Items{*;}

#Arouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider

-keep public class com.coinbene.common.websocket.core.**{*;}
-keep public class io.objectbox.**{*;}

-keep public class com.coinbene.common.network.**{*;}

# keep所有的okhttp拦截器
-keep class * implements okhttp3.Interceptor
