# TagCloudView    
[![Download](https://api.bintray.com/packages/misakuo/maven/tagcloudview/images/download.svg) ](https://bintray.com/misakuo/maven/tagcloudview/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-TagCloudAndroid-brightgreen.svg?style=flat)](http://www.android-arsenal.com/details/1/3060)
[![Build Status](https://travis-ci.org/misakuo/3dTagCloudAndroid.svg?branch=master)](https://travis-ci.org/misakuo/3dTagCloudAndroid)
[![Android Gems](http://www.android-gems.com/badge/misakuo/3dTagCloudAndroid.svg)](http://www.android-gems.com/lib/misakuo/3dTagCloudAndroid)    
[中文](https://github.com/misakuo/3dTagCloudAndroid/blob/master/README.md)
###Sample
![Sample APP](http://7fvfii.com1.z0.glb.clouddn.com/sample_qrcode.png)    
Scaning to download the sample APK.
###Summary
TagCloudView is a component based Android ViewGroup, it support to showing group of Views as a 3D sphere, and the sphere can scroll in all directions.
###UI Style
[Image](http://7fvfii.com1.z0.glb.clouddn.com/screenshot.gif)    
![screenshot](https://raw.githubusercontent.com/misakuo/3dTagCloudAndroid/master/screenshot.gif)  

###Useage
##### Eclipse  
copy source code or using maven plugin for Eclipse.
##### Android Studio / IDEA
- Place code in your `build.gradle`  
```
dependencies {
    compile 'com.moxun:tagcloudlib:1.2.0'
}
```

- Using in xml  
```  
<com.moxun.tagcloudlib.view.TagCloudView/>  
```  

- Set Adapter    
Extending class `TagsAdapter` and implement following methods:     
**public int getCount();**  
*Return the number of tags*  
**public View getView(Context context, int position, ViewGroup parent);**  
*Return the View instance of each tag*  
**public Object getItem(int position);**  
*Return the data of each tag(could be null)*  
**public int getPopularity(int position);**  
*Assign a propularity value for each Tag, this value is relation to tag's theme color*  
**public void onThemeColorChanged(View view,int themeColor);**  
*This method will be called when tag's theme color changed*  
 
- Custom Properties    

| Properties        | In XML           | In Code |Value Type|
|:------------: |:-------------:| :----:|:-:
| Auto Scroll      | app:autoScrollMode | setAutoScrollMode(int mode) |enum [disable,uniform,decelerate]
| Radius      | app:radiusPercent      |   setRadiusPercent(float percent) |float (0,1)
| Scroll Speed | app:scrollSpeed      |    setScrollSpeed(float scrollSpeed) |float (0,+]
|Start Color|app:lightColor|setLightColor(int color)|int
|End Color|app:darkColor|setDarkColor(int color)|int  


***
Welcome to Pull Request and Issues.
