# ColorPickerView
这是一个类似Android Studio内置的圆形取色板。

### 我们先来看一下效果图：
<img src="https://github.com/BlockWen/ColorPickerView/blob/master/pics_readme/last.gif?raw=true" width="30%" height="30%" />

### 使用方法：
[![](https://jitpack.io/v/BlockWen/ColorPickerView.svg)](https://jitpack.io/#BlockWen/ColorPickerView) 
1. 添加Jitpack仓库到你的build.gradle中。
2. 添加依赖到app的build.gradle中。


计算圆盘外触摸点和圆心连线 与 圆盘边界交点：
![图片名称](https://github.com/BlockWen/ColorPickerView/blob/master/pics_readme/calculateNearestCoordinate.png?raw=true)

计算左上角局部放大图：
![图片名称](https://github.com/BlockWen/ColorPickerView/blob/master/pics_readme/calculateTopLeftCornerCircle.png?raw=true)
