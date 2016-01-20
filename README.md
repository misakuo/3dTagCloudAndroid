# TagCloudView
###简介
TagCloudView是一个完全基于Android ViewGroup编写的控件，支持将一组View展示为一个3D球形集合，并支持全方向滚动。
###UI效果
![screenshot](https://raw.githubusercontent.com/misakuo/3dTagCloudAndroid/master/screenshot.gif)  

###使用
##### Eclipse  
copy代码，或转向Android Studio
##### Android Studio / IDEA
1. 在`build.gradle`中添加  
```
compile 'com.moxun:tagcloudlib:1.0.2'
```

2. 在布局文件中引入  
```  
<com.moxun.tagcloudlib.view.TagCloudView/>  
```  

3. 设置Adapter
继承`TagsAdapter`，实现以下方法
  
    **public int getCount();**  
*返回Tag数量*  
**public View getView(Context context, int position, ViewGroup parent);**  
*返回每个Tag实例*  
**public Object getItem(int position);**  
*返回Tag数据*  
**public int getPopularity(int position);**  
*针对每个Tag返回一个权重值，该值与ThemeColor和Tag初始大小有关*  
**public void onThemeColorChanged(View view,int themeColor);**  
*Tag主题色发生变化时会回调该方法*  
 
4. 定制属性    

| 属性        | xml           | 代码 |值类型|
|:------------: |:-------------:| :----:|:-:
| 自动滚动      | app:autoScrollMode | setAutoScrollMode(int mode) |enum [disable,uniform,decelerate]
| 半径百分比      | app:radiusPercent      |   setRadiusPercent(float percent) |float [0,1]
| 滚动速度 | app:scrollSpeed      |    setScrollSpeed(float scrollSpeed) |float [0,+]
|起始颜色|app:lightColor|setLightColor(int color)|int
|终止颜色|app:darkColor|setDarkColor(int color)|int  


***
欢迎提交PR