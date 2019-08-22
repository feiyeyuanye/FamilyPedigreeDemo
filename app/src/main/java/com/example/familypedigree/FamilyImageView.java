package com.example.familypedigree;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */
public class FamilyImageView extends View {
    //矩形画笔
    private Paint tipPaint;
    //圆形画笔
    private Paint cirPaint;

    //直线画笔
    private Paint linePaint;
    //文字画笔
    private Paint textPaint;
    //文字大小
    private int textPaintSize;

    //绘制矩形框
    private Rect textRect = new Rect();
    private RectF rectF = new RectF();
    //矩形框内文字

    //矩形框画笔宽度
    private int tipWidthPaint;
    //矩形框高度
    private int tipHeight;
    //矩形框宽度
    private int tipWidth;

    //画直线的Path
    private Path linePath = new Path();
    //直线的长
    private int lineLength;

    //  框内背景颜色 男
    private int bgColor = 0xFF00A1FF;
    //  背景颜色  女
    private int bgColor2 = 0xFFFF0000;
    //  毕匡与直线颜色
    private int progressColor = 0xFF000000;

    //连接线长度
    private int connectLength;

    //  X轴移动的距离
    private float moveDis;


    public FamilyImageView(Context context) {
        super(context);
    }

    public FamilyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    String[][] str;

    public void startCanver(String[][] str) {
        this.str = str;
        init();
        initPaint();
    }


    private int topMarginCir;

    private int topMarginRect;

    //初始化画笔宽度及View大小
    private void init() {
        tipWidthPaint = dp2px(1);
        tipHeight = dp2px(50);
        tipWidth = dp2px(55);
        lineLength = dp2px(30);
        connectLength = dp2px(100);

        textPaintSize = sp2px(15);

    }

    //初始化画笔
    private void initPaint() {
        tipPaint = getPaint(tipWidthPaint, bgColor, Paint.Style.FILL);
        linePaint = getPaint(tipWidthPaint, progressColor, Paint.Style.FILL);
        cirPaint = getPaint(tipWidthPaint, bgColor2, Paint.Style.FILL);
        initTextPaint();

    }

    //  统一处理paint
    private Paint getPaint(int strokeWidth, int color, Paint.Style style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(style);
        return paint;
    }

    //初始化文字画笔
    private void initTextPaint() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textPaintSize);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

//        cirPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        cirPaint.setTextSize(textPaintSize);
//        cirPaint.setColor(Color.WHITE);
//        cirPaint.setTextAlign(Paint.Align.CENTER);
//        cirPaint.setAntiAlias(true);


    }

    private int mWidth;
    private int mHeight;
    private int mViewHeight;
    Bitmap bitmap;
    Canvas bimCanvas;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

//        LogUtils.e(getWidth()+"--------------getWidth");
//        LogUtils.e(getHeight()+"-------------getHeight");
//        LogUtils.e(width+"-------------width");
//        LogUtils.e(height+"-------------height");
//        LogUtils.e(widthMode+"-------------widthMode");
//        LogUtils.e(heightMode+"-------------heightMode");

        setMeasuredDimension(measureWidth(widthMode, width), measureHeight(heightMode, height));
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bimCanvas = new Canvas(bitmap);
        bimCanvas.drawColor(Color.WHITE);
    }

    //     * 测量宽度
    private int measureWidth(int mode, int width) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                mWidth = width;
                break;
        }
        return mWidth;
    }

    //     * 测量高度
    private int measureHeight(int mode, int height) {
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                mHeight = mViewHeight;
                break;
            case MeasureSpec.EXACTLY:
                mHeight = height;
                break;
        }
        return mHeight;
    }


    //保存图片
    public void saveBitmap() {
        //sd卡位置
        //权限
        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DrawBorad/").mkdirs();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DrawBorad/" + System.currentTimeMillis() + ".jpg";
        OutputStream stream;
        try {
            stream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImageView(bimCanvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }


    private void drawImageView(Canvas canvas) {
        //为什么会绘制了两遍呢？
        drawTipView(canvas, str);
    }

    int xj = 0;
    //判断子女是否都画完，并且在没有女婿的情况下。。。改变想xy轴值
    int zin = 0;

    private void drawTipView(Canvas canvas, String[][] str) {
        //父母人数
        //4
        int isSi = 0;
        //兄弟姐妹人数。算上户主
        //户主：1    兄弟姐妹：3
        int isSan = 0;
        //配偶
        //2
        int isQiZi = 0;
        //子女
        //子：5    女：6
        int isZiNv = 0;
        //孙子孙女
        //孙子：7   孙女：8
        int isSunZiNv = 0;
        //女婿
        //9
        int isNxXu = 0;
        //儿媳
        //10
        int isErXi = 0;
        //爷爷奶奶
        //11
        int isYeNai = 0;


        //祖父母
        //外祖父母
        //叔、伯
        //姑姑
        //外孙子，外孙女
        //
        //堂兄弟姐妹
        //侄子，侄女
        //外甥，外甥女

        //判断出户主的位置
        List<String> huZhu = new ArrayList<>();
        //判断出父母和兄弟姐妹各有多少人
        for (int y = 0; y < str.length; y++) {
            if (str[y][3].equals("4")) {
                isSi++;
            } else if (str[y][3].equals("3") || str[y][3].equals("1")) {
                huZhu.add(str[y][3]);
                isSan++;
            } else if (str[y][3].equals("2")) {
                isQiZi++;
            } else if (str[y][3].equals("5") || str[y][3].equals("6")) {
                isZiNv++;
            } else if (str[y][3].equals("9")) {
                isNxXu++;
            } else if (str[y][3].equals("8") || str[y][3].equals("7")) {
                isSunZiNv++;
            } else if (str[y][3].equals("10")) {
                isErXi++;
            } else if (str[y][3].equals("11")) {
                isYeNai++;
            }
        }
        //判断出户主的位置
        int hu = huZhu.indexOf("1");
//        Log.e("TAG", "父母人数：" + isSi);
//        Log.e("TAG", "兄弟姐妹人数：" + isSan);
//        Log.e("TAG", "配偶：" + isQiZi);
//        Log.e("TAG", "子女：" + isZiNv);
//        Log.e("TAG", "子：" + isSon);
//        Log.e("TAG", "女：" + isNv);
//        Log.e("TAG", "女婿：" + isNxXu);
//        Log.e("TAG", "孙子孙女：" + isSunZiNv);
//        Log.e("TAG", "外孙子：" + isWaiZi);
//        Log.e("TAG", "外孙女：" + isWaiNv);
//        Log.e("TAG", "儿媳：" + isErXi);

        // 千行代码
        for (int i = 0; i < str.length; i++) {
            //当成员只有一名的时候，只需要画一个矩形或者圆形
            //当成员有两名的时候，看是否是夫妻，或者是父子、母子、父女、母女
            if (str.length == 1) {
                if (str[0][1].equals("男")) {
                    addLengthOne();
                    drawRoundRect(canvas, str[0][0]);
                } else {
                    addLengthOne();
                    drawCirTwo(canvas, str[0][0]);
                }
            } else {
                //如果数组内长度不为一。。不止一位家庭成员
                //如果有爷爷奶奶信息
                if (isYeNai > 0) {
                    if (isYeNai == 1 && str[i][3].equals("11")) {
                        addLengthOne();
                        if (str[i][1].equals("男"))
                            drawRoundRect(canvas, str[i][0]);
                        else if (str[i][1].equals("女"))
                            drawCirTwo(canvas, str[i][0]);
                        if (isSan==0&&isSi==0&&isZiNv==0&&isSunZiNv==0){

                        }else {
                            drawTriangleSix(canvas);

                        }
                        topMarginRect += tipHeight + lineLength;
                        topMarginCir += tipHeight + lineLength;
                    } else if (isYeNai == 2 && str[i][3].equals("11")) {
                        addLengthOne();
                        if (str[i][1].equals("男"))
                            drawRoundRect(canvas, str[i][0]);
                        else if (str[i][1].equals("女"))
                            drawCir(canvas, str[i][0]);
                        drawTriangleTwo(canvas);
                        topMarginRect += lineLength;
                        topMarginCir += lineLength;
                        if (isSan==0&&isSi==0&&isZiNv==0&&isSunZiNv==0){

                        }else {
                            drawTriangleSix(canvas);

                        }
                        topMarginRect += tipHeight + lineLength;
                        topMarginCir += tipHeight + lineLength;
                    }
                    if (isSi == 0 && isSan == 0 && isZiNv == 0 && isSunZiNv != 0) {
                        if (str[i][3].equals("11")) {
                            if (isSunZiNv == 1) {
                                topMarginRect -= lineLength + tipHeight + lineLength;
                                topMarginCir -= lineLength + tipHeight + lineLength;
                                moveDis -= tipHeight ;
                            } else if (isSunZiNv > 1) {
                                topMarginRect -= tipHeight + lineLength + lineLength;
                                topMarginCir -= tipHeight + lineLength + lineLength;
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int s = 1; s < isSunZiNv; s++) {
                                    drawTriangleFif(canvas);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            }
                        }
                    }else if (isSi==0&&isSan==0&&isZiNv!=0){
                        if (str[i][3].equals("11")) {
                            if (isZiNv == 1) {
                                topMarginRect -= lineLength + tipHeight ;
                                topMarginCir -= lineLength + tipHeight ;
//                                moveDis -= tipHeight / 2;
                            } else if (isZiNv > 1) {
                                topMarginRect -= tipHeight + lineLength + lineLength;
                                topMarginCir -= tipHeight + lineLength + lineLength;
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int s = 1; s < isZiNv; s++) {
                                    drawTriangleFif(canvas);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            }
                        }
                    }


                }
                //如果有父母信息
                if (isSi > 0) {
                    if (isSi == 1) {
                        //如果有一个父母信息
                        if (str[i][3].equals("4")) {
                            if (isYeNai == 0)
                                addLengthOne();

                            if (str[i][1].equals("男"))
                                drawRoundRect(canvas, str[i][0]);
                            else if (str[i][1].equals("女"))
                                drawCirTwo(canvas, str[i][0]);
//                            if (isSan == 0){
////                                drawTriangleTwo(canvas);
//                            }
//                            else
//                                drawTriangleSix(canvas);

                            if (isSan != 0) {
                                drawTriangleSix(canvas);
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int a = 1; a < isSan; a++) {
                                    canvas.drawLine(tipWidth / 2 + moveDis, tipHeight / 2 - 10 + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis, tipHeight / 2 - 10 + lineLength + lineLength + lineLength + topMarginRect, linePaint);
                                    canvas.drawLine(tipWidth / 2 + moveDis, tipHeight / 2 - 10 + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight / 2 - 10 + lineLength + lineLength + topMarginRect, linePaint);
                                    canvas.drawLine(tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight / 2 - 10 + lineLength + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight / 2 - 10 + lineLength + lineLength + topMarginRect, linePaint);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            } else if (isSan == 0) {
                                if (isZiNv > 0) {
                                    if (isZiNv == 1) {
                                        drawTriangleSix(canvas);
                                    } else if (isZiNv > 1) {
                                        drawTriangleSix(canvas);
                                        topMarginRect -= lineLength;
                                        topMarginCir -= lineLength;
                                        int index = 0;
                                        int con = connectLength;
                                        //判断子嗣人数
                                        //因为两个人只需要画一次线条
                                        for (int s = 1; s < isZiNv; s++) {
                                            drawTriangleFif(canvas);
                                            moveDis = moveDis + con * 2;
                                            index++;
                                        }
                                        moveDis = moveDis - con * 2 * index;
                                    }
                                } else if (isZiNv == 0) {
                                    if (isSunZiNv == 0) {
                                        //这里只有一个人，不需要划线
                                    } else if (isSunZiNv == 1) {
                                        drawTriangleSix(canvas);
                                        topMarginRect -= lineLength;
                                        topMarginCir -= lineLength;
                                        moveDis -= connectLength / 2;
                                    } else if (isSunZiNv > 1) {
                                        drawTriangleSix(canvas);
                                        topMarginRect -= lineLength;
                                        topMarginCir -= lineLength;
                                        int index = 0;
                                        int con = connectLength;
                                        //判断子嗣人数
                                        //因为两个人只需要画一次线条
                                        for (int s = 1; s < isSunZiNv; s++) {
                                            drawTriangleFif(canvas);
                                            moveDis = moveDis + con * 2;
                                            index++;
                                        }
                                        moveDis = moveDis - con * 2 * index;
                                    }
                                }
                            }
                        }
                    } else if (isSi == 2) {
                        //如果由两个父母信息
                        if (str[i][3].equals("4")) {
                            if (isYeNai == 0)
                                addLengthOne();
                            if (str[i][1].equals("男")) {
                                drawRoundRect(canvas, str[i][0]);
                            } else if (str[i][1].equals("女")) {
                                drawCir(canvas, str[i][0]);
                                //当有配偶后
                                //判断是否有子嗣，再决定接下来的连接线怎么画
//                                if (isSan == 0)
//                                    drawTriangleTwo(canvas);
//                                else
//                                    drawTriangle(canvas);
                            }

                            if (isSan != 0) {
                                if (isSan == 1) {
                                    drawTriangleTwo(canvas);
                                } else if (isSan > 1) {
                                    drawTriangle(canvas);
                                }
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int s = 1; s < isSan; s++) {
                                    drawTriangleFif(canvas);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            } else if (isSan == 0) {
                                if (isZiNv > 0) {
                                    if (isZiNv == 1) {
                                        drawTriangle(canvas);
                                        topMarginRect += lineLength;
                                        topMarginCir += lineLength;
                                        moveDis += connectLength / 2;
                                    } else if (isZiNv > 1) {
                                        drawTriangle(canvas);
                                        int index = 0;
                                        int con = connectLength;
                                        //判断子嗣人数
                                        //因为两个人只需要画一次线条
                                        for (int s = 1; s < isZiNv; s++) {
                                            drawTriangleFif(canvas);
                                            moveDis = moveDis + con * 2;
                                            index++;
                                        }
                                        moveDis = moveDis - con * 2 * index;
                                    }
                                } else if (isZiNv == 0) {
                                    if (isSunZiNv == 0)
                                        drawTriangleTwo(canvas);
                                    else if (isSunZiNv == 1) {
                                        drawTriangle(canvas);
                                    } else if (isSunZiNv > 1) {
                                        drawTriangle(canvas);
                                        int index = 0;
                                        int con = connectLength;
                                        //判断子嗣人数
                                        //因为两个人只需要画一次线条
                                        for (int s = 1; s < isSunZiNv; s++) {
                                            drawTriangleFif(canvas);
                                            moveDis = moveDis + con * 2;
                                            index++;
                                        }
                                        moveDis = moveDis - con * 2 * index;
                                    }
                                }
                            }
                        }
                    }
                }
                if (isSan == 1) {
                    //只有户主信息，，没有兄弟姐妹信息
                    if (isSi == 1) {
                        //只有一个父母信息
                        if (str[i][3].equals("1")) {
                            drawTriangleSix(canvas);
                            topMarginRect = topMarginRect + lineLength + tipHeight;
                            topMarginCir = topMarginCir + lineLength + tipHeight;
                            if (str[i][1].equals("男")) {
                                drawRoundRect(canvas, str[i][0]);
                            } else if (str[i][1].equals("女")) {
                                drawCirTwo(canvas, str[i][0]);
                            }
                            if (isZiNv > 1 && isQiZi == 0) {
                                drawTriangleSix(canvas);
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int a = 1; a < isZiNv; a++) {
                                    canvas.drawLine(tipWidth / 2 + moveDis, tipHeight / 2 + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis, tipHeight / 2 + lineLength + lineLength + lineLength + topMarginRect, linePaint);
                                    canvas.drawLine(tipWidth / 2 + moveDis, tipHeight / 2 + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight / 2 + lineLength + lineLength + topMarginRect, linePaint);
                                    canvas.drawLine(tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight / 2 + lineLength + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight / 2 + lineLength + lineLength + topMarginRect, linePaint);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            }

                            if (isZiNv == 0 && isSunZiNv != 0) {
                                if (isSunZiNv == 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    moveDis -= tipHeight;
                                } else if (isSunZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isSunZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con * 2 * index;
                                }
                            }


                        }
                    } else if (isSi == 2) {
                        //有两个父母信息
                        if (str[i][3].equals("1")) {
                            drawTriangleFou(canvas);
                            addLengthTwo();
                            if (str[i][1].equals("男")) {
                                drawRoundRect(canvas, str[i][0]);
                            } else if (str[i][1].equals("女")) {
                                drawCirTwo(canvas, str[i][0]);
                            }
                            if (isZiNv != 0 && isQiZi == 0) {
                                if (isZiNv == 1)
                                    drawTriangleSix(canvas);
                                else if (isZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con * 2 * index;


                                }
                            }
                            if (isZiNv == 0 && isSunZiNv != 0) {
                                if (isSunZiNv == 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    moveDis -= tipHeight;
                                } else if (isSunZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isSunZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con * 2 * index;
                                }
                            }
                        }

                    } else if (isSi == 0) {
                        //只有户主信息
                        //没有父母信息，，一切都是从头开始
                        if (str[i][3].equals("1")) {
                            if (isYeNai == 0)
                                addLengthOne();
                            if (str[i][1].equals("男")) {
                                drawRoundRect(canvas, str[i][0]);
                            } else if (str[i][1].equals("女")) {
                                drawCirTwo(canvas, str[i][0]);
                            }
                            if (isZiNv > 1 && isSunZiNv == 0) {
                                drawTriangleSix(canvas);
                                if (isQiZi==0){
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                }
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int s = 1; s < isZiNv; s++) {
                                    drawTriangleFif(canvas);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            } else if (isZiNv > 1 && isSunZiNv != 0) {
                                drawTriangleSix(canvas);
                                topMarginRect -= lineLength;
                                topMarginCir -= lineLength;
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int s = 1; s < isZiNv; s++) {
                                    drawTriangleFif(canvas);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;


                            }
                            if (isZiNv == 0) {

                                if (isSunZiNv == 0) {

                                } else if (isSunZiNv == 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    moveDis -= tipHeight;
                                } else if (isSunZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isSunZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con * 2 * index;
                                }
                            }
                        }

                    }
                } else if (isSan > 1) {
                    //有兄弟姐妹信息
                    if (isSi == 1 && (str[i][3].equals("3") || str[i][3].equals("1"))) {
                        //只有一个父母信息
                        topMarginRect = topMarginRect + lineLength + lineLength + tipHeight;
                        topMarginCir = topMarginCir + lineLength + lineLength + tipHeight;
                        if (str[i][1].equals("男")) {
                            drawRoundRect(canvas, str[i][0]);
                        } else if (str[i][1].equals("女")) {
                            drawCirTwo(canvas, str[i][0]);
                        }
                        topMarginRect = topMarginRect - lineLength - lineLength - tipHeight;
                        topMarginCir = topMarginCir - lineLength - lineLength - tipHeight;
                        moveDis = moveDis + connectLength + connectLength;
                        xj++;
                        if (xj == isSan * 2) {
                            if (isQiZi == 0) {
                                moveDis = 50;
                                moveDis += connectLength * 2 * hu;
                                topMarginRect += lineLength + lineLength + tipHeight;
                                topMarginCir += lineLength + lineLength + tipHeight;
                                if (isZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con1 = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int a = 1; a < isZiNv; a++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con1 * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con1 * 2 * index;
                                }

                            }
                            if (isZiNv == 0) {
                                if (isSunZiNv == 0) {

                                } else if (isSunZiNv == 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    moveDis -= tipHeight;
                                } else if (isSunZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con2 = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isSunZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con2 * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con2 * 2 * index;
                                }
                            }
                        }

                    } else if (isSi > 1 && (str[i][3].equals("3") || str[i][3].equals("1"))) {
                        //有两个父母信息
                        topMarginRect = topMarginRect + lineLength + lineLength + lineLength + tipHeight;
                        topMarginCir = topMarginCir + lineLength + lineLength + lineLength + tipHeight;
                        if (str[i][1].equals("男")) {
                            drawRoundRect(canvas, str[i][0]);
                        } else if (str[i][1].equals("女")) {
                            drawCirTwo(canvas, str[i][0]);
                        }
                        topMarginRect = topMarginRect - lineLength - lineLength - lineLength - tipHeight;
                        topMarginCir = topMarginCir - lineLength - lineLength - lineLength - tipHeight;
                        moveDis = moveDis + connectLength + connectLength;

                        xj++;
                        if (xj == isSan * 2) {
                            if (isQiZi == 0) {
                                moveDis = 50;
                                moveDis += connectLength * 2 * hu;
                                topMarginRect += lineLength + lineLength + lineLength + tipHeight;
                                topMarginCir += lineLength + lineLength + lineLength + tipHeight;
                                if (isZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con1 = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int a = 1; a < isZiNv; a++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con1 * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con1 * 2 * index;
                                } else if (isZiNv == 1) {


                                }
                            }
                            if (isZiNv == 0) {
                                if (isSunZiNv == 0) {

                                } else if (isSunZiNv == 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    moveDis -= tipHeight;
                                } else if (isSunZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con2 = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isSunZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con2 * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con2 * 2 * index;
                                }
                            }
                        }
                    } else if (isSi == 0 && (str[i][3].equals("3") || str[i][3].equals("1"))) {

                        //没有父母信息。。有兄弟姐妹信息
                        if (moveDis == 0.0 || xj == isSan) {
                            if (isYeNai == 0)
                                addLengthOne();
                        }
//                        int index = 0;
                        int con = connectLength;
                        //判断子嗣人数
                        //因为两个人只需要画一次线条
                        int moveDis2 = 50;
                        for (int a = 1; a < isSan; a++) {
                            canvas.drawLine(tipWidth / 2 + moveDis2, topMarginRect, tipWidth / 2 + moveDis2, topMarginRect + lineLength, linePaint);
                            canvas.drawLine(tipWidth / 2 + moveDis2, topMarginRect, tipWidth / 2 + moveDis2 + connectLength + connectLength, topMarginRect, linePaint);
                            canvas.drawLine(tipWidth / 2 + moveDis2 + connectLength + connectLength, topMarginRect, tipWidth / 2 + moveDis2 + connectLength + connectLength, topMarginRect + lineLength, linePaint);
                            moveDis2 = moveDis2 + con * 2;
//                            index++;
                        }
//                        moveDis2=moveDis2-con * 2*index;
                        topMarginRect += lineLength;
                        topMarginCir += lineLength;
                        if (str[i][1].equals("男")) {
                            drawRoundRect(canvas, str[i][0]);
                        } else if (str[i][1].equals("女")) {
                            drawCirTwo(canvas, str[i][0]);
                        }
                        topMarginRect -= lineLength;
                        topMarginCir -= lineLength;
                        moveDis = moveDis + connectLength + connectLength;
                        xj++;
                        if (xj == isSan * 2) {
                            if (isQiZi == 0) {

                                moveDis = 50;
                                moveDis += connectLength * 2 * hu;
                                topMarginRect += lineLength;
                                topMarginCir += lineLength;
                                if (isZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con1 = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int a = 1; a < isZiNv; a++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con1 * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con1 * 2 * index;
                                }

                            }
                            if (isZiNv == 0) {
                                if (isSunZiNv == 0) {

                                } else if (isSunZiNv == 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    moveDis -= tipHeight;
                                } else if (isSunZiNv > 1) {
                                    drawTriangleSix(canvas);
                                    topMarginRect -= lineLength;
                                    topMarginCir -= lineLength;
                                    int index = 0;
                                    int con2 = connectLength;
                                    //判断子嗣人数
                                    //因为两个人只需要画一次线条
                                    for (int s = 1; s < isSunZiNv; s++) {
                                        drawTriangleFif(canvas);
                                        moveDis = moveDis + con2 * 2;
                                        index++;
                                    }
                                    moveDis = moveDis - con2 * 2 * index;
                                }
                            }
                        }
                    }
                }
                //如果有妻子信息
                if (isQiZi == 1 && str[i][3].equals("2")) {
                    if (isSan > 1) {
                        moveDis = moveDis - connectLength * isSan - connectLength * isSan;
                        if (isSi == 1) {
                            topMarginCir = topMarginCir + lineLength * 3 + tipHeight;
                            topMarginRect += lineLength * 3 + tipHeight;
                        } else if (isSi == 2) {
                            topMarginCir = topMarginCir + lineLength * 3 + tipHeight;
                            topMarginRect += lineLength * 3 + tipHeight;
                        } else if (isSi == 0) {
                            topMarginCir += lineLength;
                            topMarginRect += lineLength;
                        }
                        //判断X轴移动的距离，，，，判断户主的位置
                        if (isSi == 1) {
                            topMarginCir -= tipHeight / 2;
                            topMarginRect -= tipHeight / 2;
                            moveDis += connectLength * 2 * hu;
                        } else if (isSi == 2) {
                            moveDis += connectLength * 2 * hu;
                        } else if (isSi == 0) {
                            moveDis += connectLength * 2 * hu;
                        }
                    }
                    drawCir(canvas, str[i][0]);
                    //是否有子嗣信息，，决定接下来的画图路线
                    if (isZiNv == 1) {
                        drawTriangle(canvas);
                    } else if (isZiNv == 0) {
                        drawTriangleTwo(canvas);
                    } else if (isZiNv > 1) {
                        drawTriangle(canvas);
                        int index = 0;
                        int con = connectLength;
                        //判断子嗣人数
                        //因为两个人只需要画一次线条
                        for (int a = 1; a < isZiNv; a++) {
                            drawTriangleFif(canvas);
                            moveDis = moveDis + con * 2;
                            index++;
                        }
                        moveDis = moveDis - con * 2 * index;

                    }


                }
                /**
                 * 绘制子嗣
                 */
                if (isZiNv == 1 && (str[i][3].equals("5") || str[i][3].equals("6"))) {
                    //只有一个子嗣
                    if (isQiZi == 0) {
                        drawTriangleSix(canvas);
                        if (isSunZiNv != 0) {
                            topMarginRect = topMarginRect + lineLength + lineLength + tipHeight / 2;
                            topMarginCir = topMarginCir + lineLength + lineLength + tipHeight / 2;
                        } else if (isSunZiNv == 0) {
                            topMarginRect = topMarginRect + lineLength + lineLength + tipHeight / 2;
                            topMarginCir = topMarginCir + lineLength + lineLength + tipHeight / 2;
                        }

                    } else if (isQiZi == 1) {
                        drawTriangleFou(canvas);
                        addLengthTwo();
                    }

                    if (str[i][1].equals("男")) {
                        drawRoundRect(canvas, str[i][0]);
                    } else if (str[i][1].equals("女")) {
                        drawCirTwo(canvas, str[i][0]);
                    }
                    //没有女婿信息
                    if (isNxXu == 0) {
                        if (isSunZiNv == 1)
                            drawTriangleSix(canvas);
                        topMarginRect = topMarginRect - lineLength;
                        topMarginCir = topMarginCir - lineLength;
                        moveDis -= tipHeight;
                        if (isSunZiNv > 1) {
                            drawTriangleFou(canvas);
                            int index = 0;
                            int con = connectLength;
                            //判断子嗣人数
                            //因为两个人只需要画一次线条
                            for (int b = 1; b < isSunZiNv; b++) {
                                drawTriangleFif(canvas);
                                moveDis = moveDis + con * 2;
                                index++;
                            }
                            moveDis = moveDis - con * 2 * index;
                        }
                    }
                } else if (isZiNv > 1 && (str[i][3].equals("5") || str[i][3].equals("6"))) {
                    zin++;
                    //有多个子嗣
                    if (isQiZi != 0) {
                        topMarginRect = topMarginRect + lineLength + lineLength + lineLength + tipHeight;
                        topMarginCir = topMarginCir + lineLength + lineLength + lineLength + tipHeight;
                        if (str[i][1].equals("男")) {
                            drawRoundRect(canvas, str[i][0]);
                        } else if (str[i][1].equals("女")) {
                            drawCirTwo(canvas, str[i][0]);
                        }
                        topMarginRect = topMarginRect - lineLength - lineLength - lineLength - tipHeight;
                        topMarginCir = topMarginCir - lineLength - lineLength - lineLength - tipHeight;
                        moveDis = moveDis + connectLength + connectLength;
                    } else if (isQiZi == 0) {
                        if (isSan == 1 && isSi == 1) {
                            topMarginRect = topMarginRect + lineLength + lineLength + lineLength + tipHeight / 2;
                            topMarginCir = topMarginCir + lineLength + lineLength + lineLength + tipHeight / 2;
                            if (str[i][1].equals("男")) {
                                drawRoundRect(canvas, str[i][0]);
                            } else if (str[i][1].equals("女")) {
                                drawCirTwo(canvas, str[i][0]);
                            }
                            topMarginRect = topMarginRect - lineLength - lineLength - lineLength - tipHeight / 2;
                            topMarginCir = topMarginCir - lineLength - lineLength - lineLength - tipHeight / 2;

                        }else {
                            topMarginRect = topMarginRect + lineLength + lineLength + lineLength + tipHeight;
                            topMarginCir = topMarginCir + lineLength + lineLength + lineLength + tipHeight;
                            if (str[i][1].equals("男")) {
                                drawRoundRect(canvas, str[i][0]);
                            } else if (str[i][1].equals("女")) {
                                drawCirTwo(canvas, str[i][0]);
                            }
                            topMarginRect = topMarginRect - lineLength - lineLength - lineLength - tipHeight;
                            topMarginCir = topMarginCir - lineLength - lineLength - lineLength - tipHeight;
                        }
                        moveDis = moveDis + connectLength + connectLength;
                    }

                    //没有女婿
                    if (isNxXu == 0) {
                        if (zin == isZiNv) {
                            topMarginRect = topMarginRect + lineLength + lineLength + tipHeight;
                            topMarginCir = topMarginCir + lineLength + lineLength + tipHeight;
                            moveDis = moveDis - connectLength - connectLength - connectLength - connectLength - tipHeight;
                            zin = 0;
                            if (isSunZiNv == 1) {
                                if (isQiZi == 0) {
                                    if (isSan == 1 && isSi == 2) {
//
                                    } else if (isSan==0&&isSi==0){

                                    }else {
                                        topMarginRect = topMarginRect - tipHeight / 2;
                                        topMarginCir = topMarginCir - tipHeight / 2;
                                    }

                                    drawTriangleFou(canvas);
                                } else {
                                    drawTriangleFou(canvas);
                                }
                            } else if (isSunZiNv > 1) {
                                if (isQiZi == 0) {
                                    if (isSan==1&&isSi==1){
                                        topMarginRect = topMarginRect - tipHeight / 2;
                                        topMarginCir = topMarginCir - tipHeight / 2;
                                    }
//                                    if (isSan >1 && isSi >0) {
//
//                                    } else if (isSan==0&&isSi==0){
//
//                                    }else if (isSi==0){
//
//                                    }else if (isSi>0&&isSan==1){
//
//                                    }else if (isSi>0&&isSan==0){
//
//                                    }else {
//                                        topMarginRect = topMarginRect - tipHeight / 2;
//                                        topMarginCir = topMarginCir - tipHeight / 2;
//                                    }

                                    drawTriangleFou(canvas);
                                } else {
                                    drawTriangleFou(canvas);
                                }
                                int index = 0;
                                int con = connectLength;
                                //判断子嗣人数
                                //因为两个人只需要画一次线条
                                for (int b = 1; b < isSunZiNv; b++) {
                                    drawTriangleFif(canvas);
                                    moveDis = moveDis + con * 2;
                                    index++;
                                }
                                moveDis = moveDis - con * 2 * index;
                            }
                        }
                    }
                    Log.e("TAG", zin + "--------------zin");
                }
                /**
                 * 绘制子嗣的配偶
                 */
                if (str[i][3].equals("9")) {
                    //如果子女的数量为 1
                    if (isZiNv == 1) {
                        if (isSunZiNv == 0) {
                            drawTriangleTwo(canvas);
                        } else if (isSunZiNv == 1) {
                            drawTriangle(canvas);
                        } else if (isSunZiNv > 1) {
                            drawTriangle(canvas);
                            int index = 0;
                            int con = connectLength;
                            //判断子嗣人数
                            //因为两个人只需要画一次线条
                            for (int a = 1; a < isSunZiNv; a++) {
                                drawTriangleFif(canvas);
                                moveDis = moveDis + con * 2;
                                index++;
                            }
                            moveDis = moveDis - con * 2 * index;
                        }
                        //这个矩形画的是女婿
                        drawRoundRectTwo(canvas, str[i][0]);

                    } else if (isZiNv > 1) {
                        if (isQiZi == 0) {
                            if (isSan == 1) {
//                                topMarginRect = topMarginRect - tipHeight;
//                                topMarginCir = topMarginCir - tipHeight;
                            } else {
//                                topMarginRect = topMarginRect - tipHeight;
//                                topMarginCir = topMarginCir - tipHeight;
                            }

                        }
                        moveDis = moveDis - connectLength * 2 * isZiNv + connectLength + connectLength;
                        topMarginRect = topMarginRect + lineLength + lineLength + tipHeight + lineLength;
                        topMarginCir = topMarginCir + lineLength + lineLength + tipHeight + lineLength;
                        //这个矩形画的是女婿
                        drawRoundRectTwo(canvas, str[i][0]);
                        if (isSunZiNv == 0) {
                            drawTriangleTwo(canvas);
                        } else if (isSunZiNv == 1) {
                            drawTriangle(canvas);
                        } else if (isSunZiNv > 1) {
                            drawTriangle(canvas);
                            int index = 0;
                            int con = connectLength;
                            //判断子嗣人数
                            //因为两个人只需要画一次线条
                            for (int a = 1; a < isSunZiNv; a++) {
                                drawTriangleFif(canvas);
                                moveDis = moveDis + con * 2;
                                index++;
                            }
                            moveDis = moveDis - con * 2 * index;
                        }
                    }
                }

                //绘制孙子孙女
                if (str[i][3].equals("7") || str[i][3].equals("8")) {
                    if (isSunZiNv == 1) {
                        if (isQiZi != 0)
                            addLengthTwo();
                        else if (isQiZi == 0) {
                            moveDis = moveDis + connectLength / 2;
                            topMarginRect = topMarginRect + lineLength + lineLength + tipHeight;
                            topMarginCir = topMarginCir + lineLength + lineLength + tipHeight;
                        }
                        if (str[i][1].equals("男")) {
                            drawRoundRect(canvas, str[i][0]);
                        } else if (str[i][1].equals("女")) {
                            drawCirTwo(canvas, str[i][0]);
                        }
                    } else {
                        topMarginRect = topMarginRect + lineLength + lineLength + lineLength + tipHeight;
                        topMarginCir = topMarginCir + lineLength + lineLength + lineLength + tipHeight;
                        if (str[i][1].equals("男")) {
                            drawRoundRect(canvas, str[i][0]);
                        } else if (str[i][1].equals("女")) {
                            drawCirTwo(canvas, str[i][0]);
                        }
                        topMarginRect = topMarginRect - lineLength - lineLength - lineLength - tipHeight;
                        topMarginCir = topMarginCir - lineLength - lineLength - lineLength - tipHeight;
                        moveDis = moveDis + connectLength + connectLength;
                    }
                }
            }
        }

    }

    //画第一项时

    private void addLengthOne() {
        moveDis = 50;
        topMarginCir = tipHeight / 2 + 5;
        topMarginRect = 5;
    }

    //画后续项时
    private void addLengthTwo() {
        moveDis = moveDis + connectLength / 2;
        topMarginRect = topMarginRect + lineLength + lineLength + tipHeight;
        topMarginCir = topMarginCir + lineLength + lineLength + tipHeight;
    }

    //     * 绘制圆形(当为第二项时)
    private void drawCir(Canvas canvas, String str) {
        //                         x                                     y          radius    paint
        canvas.drawCircle(moveDis + connectLength + tipWidth / 2, topMarginCir, tipWidth / 2, cirPaint);
        drawCirText(canvas, str);
    }

    //     * 绘制圆形(当为第一项时)
    private void drawCirTwo(Canvas canvas, String str) {
        //                  x                           y          radius    paint
        canvas.drawCircle(moveDis + tipWidth / 2, topMarginCir, tipWidth / 2, cirPaint);
        drawCirTextTwo(canvas, str);
    }


    //     * 绘制矩形  女婿
    private void drawRoundRectTwo(Canvas canvas, String str) {
        //           左             上         右              下
        rectF.set(moveDis + connectLength, topMarginRect, tipWidth + moveDis + connectLength, tipHeight + topMarginRect);
        canvas.drawRect(rectF, tipPaint);
        drawRecTextTwo(canvas, str);
    }

    //     * 绘制矩形
    private void drawRoundRect(Canvas canvas, String str) {
        //           左             上         右              下
        rectF.set(moveDis, topMarginRect, tipWidth + moveDis, tipHeight + topMarginRect);
        canvas.drawRect(rectF, tipPaint);
        drawRecText(canvas, str);
    }

    //     * 绘制直线
//     一对夫妻，有一子
    private void drawTriangle(Canvas canvas) {
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + topMarginRect, tipWidth / 2 + moveDis, tipHeight + lineLength + topMarginRect, linePaint);
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength, tipHeight + lineLength + topMarginRect, linePaint);
        canvas.drawLine(tipWidth / 2 + moveDis + connectLength, tipHeight + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength, tipHeight + topMarginRect, linePaint);

        canvas.drawLine(tipWidth / 2 + moveDis + connectLength / 2, tipHeight + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength / 2, tipHeight + lineLength + lineLength + topMarginRect, linePaint);
    }

    private void drawTriangleFou(Canvas canvas) {
        canvas.drawLine(tipWidth / 2 + moveDis + connectLength / 2, tipHeight + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength / 2, tipHeight + lineLength + lineLength + topMarginRect, linePaint);
    }

    private void drawTriangleSix(Canvas canvas) {
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + topMarginRect, tipWidth / 2 + moveDis, tipHeight + lineLength + topMarginRect, linePaint);
    }

    //     * 绘制直线
//     一对夫妻，但暂时无子女
    private void drawTriangleTwo(Canvas canvas) {
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + topMarginRect, tipWidth / 2 + moveDis, tipHeight + lineLength + topMarginRect, linePaint);
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength, tipHeight + lineLength + topMarginRect, linePaint);
        canvas.drawLine(tipWidth / 2 + moveDis + connectLength, tipHeight + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength, tipHeight + topMarginRect, linePaint);
    }


    private void drawTriangleFif(Canvas canvas) {
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis, tipHeight + lineLength + lineLength + lineLength + topMarginRect, linePaint);
        canvas.drawLine(tipWidth / 2 + moveDis, tipHeight + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight + lineLength + lineLength + topMarginRect, linePaint);
        canvas.drawLine(tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight + lineLength + lineLength + lineLength + topMarginRect, tipWidth / 2 + moveDis + connectLength + connectLength, tipHeight + lineLength + lineLength + topMarginRect, linePaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawRecText(Canvas canvas, String textString) {
        textRect.left = (int) moveDis;
        textRect.top = topMarginRect;
        textRect.right = (int) (tipWidth + moveDis);
        textRect.bottom = tipHeight + topMarginRect;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString, textRect.centerX(), baseline, textPaint);
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawCirText(Canvas canvas, String textString) {
        textRect.left = (int) moveDis + connectLength;
        textRect.top = topMarginRect;
        textRect.right = (int) (tipWidth + moveDis + connectLength);
        textRect.bottom = tipHeight + topMarginRect;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString, textRect.centerX(), baseline, textPaint);

    }

    private void drawRecTextTwo(Canvas canvas, String textString) {
        textRect.left = (int) moveDis + connectLength;
        textRect.top = topMarginRect;
        textRect.right = (int) (tipWidth + moveDis + connectLength);
        textRect.bottom = tipHeight + topMarginRect;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString, textRect.centerX(), baseline, textPaint);

    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawCirTextTwo(Canvas canvas, String textString) {
        textRect.left = (int) moveDis;
        textRect.top = topMarginRect;
        textRect.right = (int) (tipWidth + moveDis);
        textRect.bottom = tipHeight + topMarginRect;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(textString, textRect.centerX(), baseline, textPaint);

    }


    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }
}
