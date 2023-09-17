package ly.pp.justpiano3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.thread.ThreadPoolUtil;

public class DrawPrizeView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final SurfaceHolder mHolder;
    private Canvas mCanvas;
    private boolean isRunning;

    /**
     * 盘块的奖项
     */
    private final String[] mStrs = new String[]{"框框", "经验", "祝福", "考级", "挑战", "音符"};

    /**
     * 盘块的图片
     */
    private final int[] mImgs = new int[]{R.drawable.head, R.drawable.exp, R.drawable.favor, R.drawable.nailface, R.drawable.listen_play, R.drawable.gold};

    /**
     * 与图片对应的bitmap数组
     */
    private Bitmap[] mImgBitmap;

    /**
     * 盘块的颜色
     */
    private final int[] mColors = new int[]{0XFFFFC300, 0XFFF17E01, 0XFFFFC300, 0XFFF17E01, 0XFFFFC300, 0XFFF17E01};

    private final int mItemCount = mColors.length;

    /**
     * 绘制盘块的画笔
     */
    private Paint mArcPaint;

    /**
     * 绘制文本的画笔
     */
    private Paint mTextPaint;

    /**
     * 盘块的背景图
     * RectF与Rect区别:1、精度不一样，Rect是使用int类型作为数值，RectF是使用float类型作为数值
     * 2、两个类型提供的方法也不是完全一致
     */
    private RectF mRange = new RectF();

    /**
     * 整个盘块的直径
     */
    private int mRadius;

    /**
     * 文字大小
     */
    private final float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());

    /**
     * 滚动的速度
     */
    private double mSpeed = 0;

    /**
     * 可能有两个线程操作
     * 保证线程间变量的可见性
     */
    private volatile float mStartAngle = 0;

    /**
     * 转盘的中心位置
     */
    private int mCenter;

    /**
     * 这里我们的padding直接以paddingLeft为准
     */
    private int mPadding;

    public DrawPrizeView(Context context) {
        this(context, null);
    }

    public DrawPrizeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        // 获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mPadding = getPaddingLeft();
        // 直径
        mRadius = width - mPadding * 2;
        // 中心点
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // 初始化绘制盘块的画笔
        mArcPaint = new Paint();
        // 防止边缘的锯齿
        mArcPaint.setAntiAlias(true);
        // 设置递色
        mArcPaint.setDither(true);
        // 初始化绘制文本的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xff000000);
        mTextPaint.setTextSize(mTextSize);
        // 初始化盘块绘制的范围
        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);
        // 初始化图片
        mImgBitmap = new Bitmap[mItemCount];
        for (int i = 0; i < mImgBitmap.length; i++) {
            mImgBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
        }
        isRunning = true;
        ThreadPoolUtil.execute(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    @Override
    public void run() {
        // 不断进行绘制
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                mCanvas.drawColor(0xff000000);
                // 绘制盘块
                float tmpAngle = mStartAngle;
                float sweepAngle = 360f / mItemCount;
                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);
                    // 绘制盘块
                    // 绘制圆弧
                    // 第一个参数:绘制区域
                    // 第二个参数:起始角度
                    // 第三个参数:每个盘块角度
                    // 第三个参数(useCenter):要不要使用中间原点
                    // 第四个参数:画笔
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
                    // 绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    // 绘制Icon
                    drawIcon(tmpAngle, mImgBitmap[i]);
                    tmpAngle += sweepAngle;
                }
                mStartAngle += mSpeed;
                mSpeed -= 1;
                if (mSpeed <= 0) {
                    mSpeed = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null && mHolder.getSurface().isValid()) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 点击启动旋转
     *
     * @param index 停下来的位置
     */
    public void luckyStart(int index) {
        // 计算每一项的角度
        float angle = 360f / mItemCount;
        // 计算每一项中奖范围(当前index)
        // 1->150~210
        // 0->210~270
        float from = 270 - (index + 1) * angle;
        float end = from + angle;
        // 设置停下来需要旋转的距离
        float targetFrom = 4 * 360 + from;
        // 每次旋转的角度不同
        float targetEnd = 4 * 360 + end;
        /*
         *   v1 -> 0 每次 - 1
         *   等差数列 v1 + 1转成int
         *   (v1 + 0) * (v1 + 1) / 2 = targetFrom;
         *      v1 * v1 + v1 - 2 * targetFrom = 0;
         *      v1 = (-1 + Math.sqrt(1 + 8 * targetFrom)) / 2
         */
        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);
        mSpeed = v1 + Math.random() * (v2 - v1);
    }

    /**
     * 转盘是否在旋转
     *
     * @return
     */
    public boolean isStart() {
        return mSpeed != 0;
    }

    /**
     * 绘制Icon
     *
     * @param tmpAngle
     * @param bitmap
     */
    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        // 设置图片的宽度为直径1/8
        int imgWidth = mRadius / 8;
        // PI / 180 -> 一度
        // angle弧度值
        float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);
        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));
        // 确定图片位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth, y + imgWidth / 2);
        mCanvas.drawBitmap(bitmap, null, rect, null);
    }

    /**
     * 绘制每个盘块的文本（弧形）
     *
     * @param tmpAngle   当前绘制的倾斜角度
     * @param sweepAngle 每个扇区的角度
     * @param mStr       文字
     */
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(mStr);
        // 水平偏移量 mRadius是直径
        int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);
        // 垂直偏移量
        int vOffset = mRadius / 2 / 6;
        // 第三个参数:水平偏移量,第四个参数:垂直偏移量
        mCanvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }
}