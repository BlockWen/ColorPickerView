package com.blocki.colorpickerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Blocki on 2019/7/19 13:43
 */

public class ColorPickerView extends View {

    private static final String TAG = "ColorPickerView";

    //view的宽高值
    private int height = 0;
    private int width = 0;

    private float circleX = 0;
    private float circleY = 0;
    private float circleRadius = 0;

    private float pointX = 0;
    private float pointY = 0;
    private float pointRadius = 0;

    private Paint paint = new Paint();
    private Paint pointPaint = new Paint();
    private Paint magnifyPaint = new Paint();
    private Paint magnifyBoundsPaint = new Paint();
    //缩放倍数，默认2倍，具体需要计算（倍数 = 左上角圆的半径/触摸点圆的半径）
    private float scaleMultiple = 2;
    //绘制完成后的取色盘bitmap
    private Bitmap mBitmap = null;
    //放大的Bitmap
    private Bitmap magnifyBitmap = null;
    //圆形剪切图
    private ShapeDrawable shapeDrawable = null;
    //矩阵平移用到的matrix
    private Matrix matrix = new Matrix();
    private Canvas mCanvas;

    //局部放大图的坐标xy和圆半径
    private float magnifyCircleX = 0;
    private float magnifyCircleY = 0;
    private float magnifyCircleRadius = 0;

    //drawable相对于canvas的四点位置
    private int magnifyCircleTop = 0,magnifyCircleLeft = 0,magnifyCircleRight = 0,magnifyCircleBottom = 0;
    //当前触摸点的颜色值
    private int curRGBColor = 0;
    private int[] rgb = new int[3];
    //是否画放大图 true为画 false不画
    private boolean drawMagnifyCircle = true;
    //是否画放大图的边界 true为画 false 不画
    private boolean drawMagnifyBounds = false;

    private int cornorCircleType = 0;

    public final static int TYPE_MAGNIFY = 0;
    public final static int TYPE_FILL = 1;

    private Context context;

    public ColorPickerView(Context context) {
        super(context);
        init();
        this.context = context;
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        this.context = context;
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        this.context = context;
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: ");
        height = h;
        width = w;
        Log.d(TAG, "onSizeChanged: view height:" + h + " width:" + w);
        //根据view的宽高设定取色盘的原点坐标和半径（取色盘半径位于view的中心）
        if (w == h){
            circleX = w/2;
            circleY = h/2;
            circleRadius = w/2;
        }else if (w > h){
            circleX = w/2;
            circleY = h/2;
            circleRadius = h/2;
        } else if (w < h){
            circleX = w/2;
            circleY = h/2;
            circleRadius = w/2;
        }
        //初始触摸点位置设为圆盘中心
        pointX = circleX;
        pointY = circleY;
        //初始触摸点的圆半径为圆盘的1/25
        pointRadius = circleRadius/25;
        //设置彩色圆盘半径为24/25
        circleRadius = circleRadius/25*24;
        //创建圆盘bitmap，用于取色
        mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        Log.d(TAG, "onSizeChanged: circle X:" + circleX + " Y:" + circleY + " radius:" + circleRadius + " pointRadius:" + pointRadius);

        calculateTopLeftCornerCircle();
        //计算放大倍数
        scaleMultiple = magnifyCircleRadius / pointRadius;
        Log.d(TAG, "onSizeChanged: scaleMultiple:" + scaleMultiple);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }



    /**
     * 初始化paint
     * */
    public void init(){
        Log.d(TAG, "init: ");
        
        pointPaint.setColor(Color.BLACK);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(1);
        pointPaint.setAntiAlias(true);


        Log.d(TAG, "init: paint-- TYPE_MAGNIFY ");
        magnifyPaint.setColor(Color.BLACK);
        magnifyPaint.setStyle(Paint.Style.STROKE);
        magnifyPaint.setStrokeWidth(1);
        magnifyPaint.setAntiAlias(true);

        magnifyBoundsPaint.setColor(Color.BLACK);
        magnifyBoundsPaint.setStyle(Paint.Style.STROKE);
        magnifyBoundsPaint.setStrokeWidth(1);
        magnifyBoundsPaint.setAntiAlias(true);



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");
        drawSweepGradinent(canvas);
        drawRadialGradient(canvas);
        drawPoint(canvas);
        if (drawMagnifyCircle){
            drawMagnifyCircle(canvas);
        }
    }

    /**
     *  绘制扫描渐变
     */
    private void drawSweepGradinent(Canvas canvas){
        SweepGradient sweepGradient = new SweepGradient(circleX,circleY,
                new int[]{Color.RED,Color.MAGENTA,Color.BLUE,Color.CYAN,Color.GREEN,Color.YELLOW,Color.RED},
                null);
        paint.setShader(sweepGradient);
        paint.setAntiAlias(true);
        canvas.drawCircle(circleX,circleY,circleRadius,paint);
        mCanvas.drawCircle(circleX,circleY,circleRadius,paint);
    }

    /**
     * 绘制一个从白色到透明的径向渐变
     * */
    private void drawRadialGradient(Canvas canvas){
        RadialGradient radialGradient = new RadialGradient(circleX,circleY,circleRadius,new int[]{Color.WHITE,Color.TRANSPARENT},null,Shader.TileMode.CLAMP);
        paint.setShader(radialGradient);
        canvas.drawCircle(circleX,circleY,circleRadius,paint);
        mCanvas.drawCircle(circleX,circleY,circleRadius,paint);
    }

    /**
     * 绘制跟随触摸点选颜色的圆圈
     * */
    private void drawPoint(Canvas canvas){
        canvas.drawCircle(pointX,pointY,pointRadius,pointPaint);
    }

    /**
     * 绘制局部放大的左上角图
     * */
    private void drawMagnifyCircle(Canvas canvas){

        if (cornorCircleType == TYPE_MAGNIFY){
            if (magnifyBitmap == null){
                //创建放大后的bitmap，长宽在float计算后转为int，避免差异过大
                magnifyBitmap = Bitmap.createScaledBitmap(mBitmap,(int)(width * scaleMultiple),(int)(height * scaleMultiple),true);
                BitmapShader shader = new BitmapShader(magnifyBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                shapeDrawable = new ShapeDrawable(new OvalShape());
                shapeDrawable.getPaint().setShader(shader);
                //设置drawable相对于canvas的位置
                shapeDrawable.setBounds(magnifyCircleLeft, magnifyCircleTop,magnifyCircleRight,magnifyCircleBottom);
                translate();
            }
            shapeDrawable.draw(canvas);
        } else if (cornorCircleType == TYPE_FILL){
            if (magnifyBitmap == null){
                magnifyPaint.setColor(curRGBColor);
                magnifyPaint.setStyle(Paint.Style.FILL);
                magnifyPaint.setStrokeWidth(1);
                magnifyPaint.setAntiAlias(true);
            }
            magnifyPaint.setColor(curRGBColor);
            canvas.drawCircle(magnifyCircleX,magnifyCircleY,magnifyCircleRadius,magnifyPaint);
        }

        if (drawMagnifyBounds){
            //画个边界 半径加1避免覆盖边界颜色
            canvas.drawCircle(magnifyCircleX,magnifyCircleY,magnifyCircleRadius+1,magnifyBoundsPaint);
        }
    }

    /**
     * 判断触摸点是否在绘制的圆内
     * （使用直角三角形求出第三边长，勾股定理）
     * */
    private boolean isInside(float x,float y){
        boolean isInside = false;
        //触摸点到取色盘圆点距离
        float p2plength = 0;
        if (x == circleX && y != circleY){
            //触摸点的横坐标与原点横坐标相等但是纵坐标不等
            p2plength = Math.abs(y - circleY);
        } else if (x != circleX && y == circleY){
            //触摸点横坐标与原点不等，纵坐标相等
            p2plength = Math.abs(y - circleX);
        } else if (x == circleX && y == circleY){
            //触摸点在原点上时
        } else if (x != circleX && y != circleY){
            //触摸点的横纵坐标与原点都不相等
            float bottomLength = Math.abs(x - circleX);
            float uprightLength = Math.abs(y - circleY);
            p2plength = (float) Math.sqrt((bottomLength*bottomLength) + (uprightLength*uprightLength));
        }
        Log.d(TAG, "isInside: p2pLength " + p2plength);
        if (p2plength < circleRadius){
            isInside = true;
        }
        return isInside;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent DOWN: x:" + event.getX() + " Y:" + event.getY() +" rawX:" + event.getRawX() + " rawY:" + event.getRawY());
                if (isInside(event.getX(),event.getY())){
                    pointX = event.getX();
                    pointY = event.getY();
                    Log.d(TAG, "onTouchEvent DOWN: 触摸点在圆的里面");

                } else {
                    Log.d(TAG, "onTouchEvent DOWN: 触摸点在圆的外面");
                    calculateNearestCoordinate(event.getX(),event.getY());
                }
                if (cornorCircleType == TYPE_MAGNIFY){
                    translate();
                }else{
                    curRGBColor = getPointColor();
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent MOVE: x:" + event.getX() + " Y:" + event.getY() +" rawX:" + event.getRawX() + " rawY:" + event.getRawY());
                if (isInside(event.getX(),event.getY())){
                    pointX = event.getX();
                    pointY = event.getY();
                    Log.d(TAG, "onTouchEvent MOVE: 触摸点在圆的里面");

                } else {
                    Log.d(TAG, "onTouchEvent MOVE: 触摸点在圆的外面");
                    calculateNearestCoordinate(event.getX(),event.getY());
                }
                if (cornorCircleType == TYPE_MAGNIFY){
                    translate();
                }else{
                    curRGBColor = getPointColor();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent UP: x:" + event.getX() + " Y:" + event.getY() +" rawX:" + event.getRawX() + " rawY:" + event.getRawY());
                //手指抬起时获取当前点的颜色
                curRGBColor = getPointColor();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 计算圆盘左上角外切于取色盘的圆点坐标以及top left right bottm 四点
     * 具体计算详见github图文介绍
     * */
    private void calculateTopLeftCornerCircle(){
        magnifyCircleRadius = (float) ((3 - 2*Math.sqrt(2)) * circleRadius);
        //勾股求出取色盘圆点到放大图圆点连线组成三角形的两底边长度
        float bottomLength = (float) ((circleRadius + magnifyCircleRadius) / Math.sqrt(2));
        magnifyCircleX = circleX - bottomLength;
        magnifyCircleY = circleY - bottomLength;
        //由于跟随触摸点移动的小球会出现在圆盘外，所以要减去小球半径让放大图和小球不重合在一起,要在算完放大圆中心点坐标后减去小球的半径
        magnifyCircleRadius = magnifyCircleRadius - pointRadius;
        //计算drawable的位置
        magnifyCircleLeft = (int) (magnifyCircleX - magnifyCircleRadius);
        magnifyCircleTop = (int) (magnifyCircleY - magnifyCircleRadius);
        magnifyCircleRight = (int) (magnifyCircleX + magnifyCircleRadius);
        magnifyCircleBottom = (int) (magnifyCircleY + magnifyCircleRadius);
    }

    /**
     * 计算触摸点距离最近的圆盘点的坐标
     * 具体计算详见github图文介绍
     * */
    public void calculateNearestCoordinate(float touchX,float touchY){

        float om = Math.abs(touchX - circleX);
        float am = Math.abs(touchY - circleY);
        //勾股定理计算斜边长
        float oa = (float) Math.sqrt((om*om)+(am*am));
        //相似三角形三边等比
        float on = (circleRadius/oa)*om;
        float bn = (circleRadius/oa)*am;
        if (touchX == circleX){
            //触摸点与原点横坐标相等
            if (touchY > (circleX + circleRadius)){
                //在下方时
                pointX = circleX;
                pointY = circleY + circleRadius;
            }else{
                //在上方时
                pointX = circleX;
                pointY = circleY - circleRadius;
            }
        } else if (touchY == circleY){
            //触摸点与原点纵坐标相等
            if (touchX > (circleY + circleRadius)){
                //在右侧时
                pointY = circleY;
                pointX = circleX + circleRadius;
            }else{
                //在左侧时
                pointY = circleY;
                pointX = circleX - circleRadius;
            }
        } else if (touchX < circleX && touchY < circleY){
            //触摸点在左上角时
            pointX = circleX - on;
            pointY = circleY - bn;
        } else if (touchX < circleX && touchY > circleY){
            //触摸点在左下角
            pointX = circleX - on;
            pointY = circleY + bn;
        } else if (touchX > circleX && touchY > circleY){
            //触摸点在右下角
            pointX = circleX + on;
            pointY = circleY + bn;
        } else if (touchX > circleX && touchY < circleY){
            //触摸点在右上角
            pointX = circleX + on;
            pointY = circleY - bn;
        }
    }

    /**
     * 移动画好的bigmap达到放大图跟随移动的效果
     * */
    private void translate(){
        //使用matrix进行对canvas进行平移
        matrix.setTranslate(magnifyCircleRadius - pointX * scaleMultiple, magnifyCircleRadius - pointY * scaleMultiple);
        shapeDrawable.getPaint().getShader().setLocalMatrix(matrix);
    }



    /**
     * 获取触摸点的颜色值
     * */
    private int getPointColor(){
        Log.d(TAG, "getColor: ");
        int pixelColor = mBitmap.getPixel((int)pointX,(int)pointY);
        return pixelColor;
    }

    /**
     * 返回大小为3的int数组，数组位置0：Red值 1：Green值 2：Blue值
     * */
    public int[] getRGBArray(){
        rgb[0] = Color.red(curRGBColor);
        rgb[1] = Color.green(curRGBColor);
        rgb[2] = Color.blue(curRGBColor);
        return rgb;
    }

    //获取当前触摸点坐标的像素点颜色
    public int getCurRGBColor(){
        return this.curRGBColor;
    }

    public boolean isDrawMagnifyCircle() {
        return drawMagnifyCircle;
    }

    public void setDrawMagnifyCircle(boolean drawMagnifyCircle) {
        this.drawMagnifyCircle = drawMagnifyCircle;
    }

    public boolean isDrawMagnifyBounds() {
        return drawMagnifyBounds;
    }

    public void setDrawMagnifyBounds(boolean drawMagnifyBounds) {
        this.drawMagnifyBounds = drawMagnifyBounds;
    }

    public void setCornorCircleType(int cornorCircleType) {
        this.cornorCircleType = cornorCircleType;
    }

    //未想好放大倍数和触摸点圆半径的设置边界在哪。暂时不允许调用
    private void setScaleMultiple(float scaleMultiple) {
        this.scaleMultiple = scaleMultiple;
    }

    private void setPointRadius(float pointRadius) {
        this.pointRadius = pointRadius;
    }

}
