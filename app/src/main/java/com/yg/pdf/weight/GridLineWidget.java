package com.yg.pdf.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GridLineWidget extends View {
    private Paint paint;

    public GridLineWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        paint = new Paint();
        paint.setColor(Color.parseColor("#999999"));

    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int i = width / 3;
        int i2 = height / 6;
        float f = i;
        float f2 = height;
        canvas.drawLine(f, 0.0f, f, f2, paint);
        float f3 = i * 2;
        canvas.drawLine(f3, 0.0f, f3, f2, paint);
        float f4 = i2;
        float f5 = width;
        canvas.drawLine(0.0f, f4, f5, f4, paint);
        float f6 = i2 * 2;
        canvas.drawLine(0.0f, f6, f5, f6, paint);
        float f7 = i2 * 3;
        canvas.drawLine(0.0f, f7, f5, f7, paint);
        float f8 = i2 * 4;
        canvas.drawLine(0.0f, f8, f5, f8, paint);
        float f9 = i2 * 5;
        canvas.drawLine(0.0f, f9, f5, f9, paint);
    }

    public GridLineWidget(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public GridLineWidget(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }
}