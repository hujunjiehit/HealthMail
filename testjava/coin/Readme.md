# bugly账号
    bugly，https://bugly.qq.com/v2/
    账户：926312668
    密码：MANbiwang@2018

# Mob分享账号
    http://www.mob.com/
    账号：test1@xatom.io
    密码：COINbene2017

# 推送平台
    腾讯信鸽推送平台
    账户：huyong@xatom.io
    密码：coinbene2019

# 图片转webp格式:
    ./pngToWebp.sh      该命令会自动将png转成同名的webp格式，同时删除原来的png图片

# 查看项目依赖的命令
    ./gradlew -q :app:dependencies


# 测试打包命令
    生成提测包: ./gradlew clean assembleCoinbenePreRelease
    打包成功之后，会自动上传


# 上线打包流程
   1、打release包
    
        ./gradlew clean assembleCoinbeneRelease  
        
        生成release包和对应的mapping文件，自动拷贝到channels目录
        
   2、加固，采用爱加密加固
        
        爱加密地址：http://www.ijiami.cn/
        账户：lbai@xatom.io
        密码：MANbiwang
        
        勾选多渠道打包，加固完成会自动生成对应的渠道包，然后用爱加密的签名工具签名
        
  


# 热修复，修复bug流程

   1、找到对应的线上包（加固之前的release包）和mapping文件，在channels文件夹下面
    
   2、用线上release包对应的mapping文件覆盖app目录下的mapping.txt文件，文件名不能修改
    
   3、修改app目录下的混淆文件proguard-rules.pro，主要修改两处
        
        注释掉这一行：-printmapping mapping.txt
        取消掉这一行的注释：  #-applymapping mapping.txt
        
        applymapping表示用线上的mapping文件来生成新的release包
    
   4、修改代码，修复bug
    
   5、执行 ./gradlew clean assembleRelease 命令生成新的release包
    
   6、用阿里SOPHIX热更新补丁工具，通过新的release包和旧的reealse包对比差异，生成patch文件 sophix-patch.jar
    
      注意事项：
        > 生成patch，需要添加 AES KEY: coinbenecoinbene
        > 生成的patch文件sophix-patch.jar，不能重新命名
        
   > [Mac版本生成patch工具地址](http://ams-hotfix-repo.oss-cn-shanghai.aliyuncs.com/SophixPatchTool_macos.zip)
         
   > [生成补丁文档](https://help.aliyun.com/document_detail/93826.html?spm=a2c4g.11186623.2.10.22f75b84fSXcfN)
    
   7、调试补丁文件，验证修复是否生效
    
   > [调试工具下载地址](http://ams-hotfix-repo.oss-cn-shanghai.aliyuncs.com/hotfix_debug_tool-release.apk)
        
   > [调试补丁文档](https://help.aliyun.com/document_detail/93827.html?spm=a2c4g.11186623.6.583.3ba85abd6mCmph)
    
   8、通过阿里云平台发布补丁 目前用的是军杰的个人账号，需要扫码登陆
    
   