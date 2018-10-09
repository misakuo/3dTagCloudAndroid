# TagCloudView    

### 简介
TagCloudView是一个完全基于Android ViewGroup编写的控件，支持将一组View展示为一个3D球形集合，并支持全方向滚动,自适应不同高度，并实现开始时扩散动画以及手势释放后速度衰减及点击tag放大效果，参考[DBSphereTagCloud](https://github.com/dongxinb/DBSphereTagCloud)效果。
### UI效果
![Image](https://github.com/guojilong/3dTagCloudAndroid/blob/master/demo.gif)    
 

### 使用

##### Android Studio / IDEA
- 在`build.gradle`中添加  
```
 implementation 'com.github.guojilong:3dTagCloudAndroid:1.4.0'
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

- 开始滚动

    **public void start()**  
*开始*  
**public void startWithAnimation()**  
*带有展开动画的开始*
 
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

感谢[misakuo](https://github.com/misakuo/3dTagCloudAndroid)
