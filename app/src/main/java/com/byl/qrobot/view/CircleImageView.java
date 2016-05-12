package com.byl.qrobot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.byl.qrobot.R;

public class CircleImageView extends BaseImageView {

    public static class Shape {
        public static final int CIRCLE = 1;
        public static final int RECTANGLE = 2;
        public static final int SVG    = 3;
    }

    private int mShape = Shape.CIRCLE;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, int resourceId, int shape, int svgRawResourceId) {
        this(context);

        setImageResource(resourceId);
        mShape = shape;
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructor(context, attrs);
    }

    private void sharedConstructor(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mShape = a.getInt(R.styleable.CircleImageView_shape, Shape.CIRCLE);
        a.recycle();
    }
    
    public  Bitmap getBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawOval(new RectF(0.0f, 0.0f, width, height), paint);

        return bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        switch (mShape) {
            case Shape.CIRCLE:
                return getBitmap(getWidth(), getHeight());
        }
        return null;
    }

}
