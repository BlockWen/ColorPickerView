# ColorPickerView
这是一个类似Android Studio内置的圆形取色板。

### 我们先来看一下效果图：左上角的图为触摸点区域的放大图（可选择填充颜色或放大图）
<img src="https://github.com/BlockWen/ColorPickerView/blob/master/pics_readme/last.gif?raw=true" width="30%" height="30%" />

### 使用方法：
[![](https://jitpack.io/v/BlockWen/ColorPickerView.svg)](https://jitpack.io/#BlockWen/ColorPickerView) 

添加Jitpack仓库到你Project的build.gradle中。

`maven { url 'https://jitpack.io' }`

添加依赖到app的build.gradle中。

`implementation 'com.github.BlockWen:ColorPickerView:1.0.2'`

在xml中：
```
<com.blocki.mylibrary.ColorPickerView
    android:id="@+id/colorPicker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

在Java中的配置：
```
//设置左上角圆类型 TYPE_MAGNIFY:触摸点的局部放大图 TYPE_FILL：触摸点的颜色图 默认TYPE_MAGNIFY
pickerView.setCornorCircleType(ColorPickerView.TYPE_MAGNIFY);
//设置是否绘制左上角圆的边界，true：绘制 false：不绘制 默认false。
pickerView.setDrawMagnifyBounds(false);
//设置是否绘制左上角的圆。true：绘制 false：不绘制 默认true。
pickerView.setDrawMagnifyCircle(true);
```

如何获取颜色值?
```
//获取当前触摸点的颜色值，返回值int
int color = pickerView.getCurRGBColor();
//获取当前触摸点的red，green，blue颜色值 数组[0] red 数组[1] green 数组[2]blue
int[] rgbArray = pickerView.getRGBArray();
```

计算圆盘外触摸点和圆心连线 与 圆盘边界交点：
![图片名称](https://github.com/BlockWen/ColorPickerView/blob/master/pics_readme/calculateNearestCoordinate.png?raw=true)

计算左上角局部放大图：
![图片名称](https://github.com/BlockWen/ColorPickerView/blob/master/pics_readme/calculateTopLeftCornerCircle.png?raw=true)
