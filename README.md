# TagCloudView    
[![Download](https://api.bintray.com/packages/misakuo/maven/tagcloudview/images/download.svg) ](https://bintray.com/misakuo/maven/tagcloudview/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-TagCloudAndroid-brightgreen.svg?style=flat)](http://www.android-arsenal.com/details/1/3060)
[![Build Status](https://travis-ci.org/misakuo/3dTagCloudAndroid.svg?branch=master)](https://travis-ci.org/misakuo/3dTagCloudAndroid)
[![Android Gems](http://www.android-gems.com/badge/misakuo/3dTagCloudAndroid.svg)](http://www.android-gems.com/lib/misakuo/3dTagCloudAndroid)    
[English](https://github.com/misakuo/3dTagCloudAndroid/blob/master/README.en.md)
###Sample
![Sample APP](http://7fvfii.com1.z0.glb.clouddn.com/sample_qrcode.png)    
扫码下载示例APK
###简介
TagCloudView是一个完全基于Android ViewGroup编写的控件，支持将一组View展示为一个3D球形集合，并支持全方向滚动。
###UI效果
[Image](http://7fvfii.com1.z0.glb.clouddn.com/screenshot.gif)    
![screenshot](https://raw.githubusercontent.com/misakuo/3dTagCloudAndroid/master/screenshot.gif)  

###使用
##### Eclipse  
copy代码，或使用ADT的maven插件
##### Android Studio / IDEA
- 在`build.gradle`中添加  
```
compile 'com.moxun:tagcloudlib:1.2.0'
```

- 在布局文件中引入  
```  
<com.moxun.tagcloudlib.view.TagCloudView/>  
```  

- 设置Adapter    
继承`TagsAdapter`，实现以下方法
  
    **public int getCount();**  
*返回Tag数量*  
**public View getView(Context context, int position, ViewGroup parent);**  
*返回每个Tag实例*  
**public Object getItem(int position);**  
*返回Tag数据*  
**public int getPopularity(int position);**  
*针对每个Tag返回一个权重值，该值与ThemeColor和Tag初始大小有关；一个简单的权重值生成方式是对一个数N取余或使用随机数*  
**public void onThemeColorChanged(View view,int themeColor);**  
*Tag主题色发生变化时会回调该方法*  
 
- 定制属性    

| 属性        | xml           | 代码 |值类型|
|:------------: |:-------------:| :----:|:-:
| 自动滚动      | app:autoScrollMode | setAutoScrollMode(int mode) |enum [disable,uniform,decelerate]
| 半径百分比      | app:radiusPercent      |   setRadiusPercent(float percent) |float [0,1]
| 滚动速度 | app:scrollSpeed      |    setScrollSpeed(float scrollSpeed) |float [0,+]
|起始颜色|app:lightColor|setLightColor(int color)|int
|终止颜色|app:darkColor|setDarkColor(int color)|int  


***
欢迎提交PR
