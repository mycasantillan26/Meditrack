package com.example.meditrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

public class CustomAnalogClock extends View {
    private Paint paint, numeralPaint, centerCirclePaint;
    private Rect rect = new Rect();
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 1000; // 1 second update interval
    private int centerX, centerY, centerRadius = 10;

    public CustomAnalogClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(8);
        paint.setStyle(Paint.Style.STROKE);

        numeralPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numeralPaint.setColor(Color.BLACK);
        numeralPaint.setTextSize(40);

        centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCirclePaint.setColor(Color.BLACK);
        centerCirclePaint.setStyle(Paint.Style.FILL);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // Start the clock updates
        handler.postDelayed(runnable, UPDATE_INTERVAL);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 10;

        // Draw clock face
        paint.setShader(new RadialGradient(centerX, centerY, radius, Color.LTGRAY, Color.WHITE, Shader.TileMode.MIRROR));
        canvas.drawCircle(centerX, centerY, radius, paint);
        paint.setShader(null);

        // Draw numerals
        for (int num = 1; num <= 12; num++) {
            String numeral = String.valueOf(num);
            numeralPaint.getTextBounds(numeral, 0, numeral.length(), rect);
            double angle = Math.PI / 6 * (num - 3);
            int x = (int) (centerX + Math.cos(angle) * (radius - 40) - rect.width() / 2);
            int y = (int) (centerY + Math.sin(angle) * (radius - 40) + rect.height() / 2);
            canvas.drawText(numeral, x, y, numeralPaint);
        }

        // Draw hands
        drawHands(canvas, centerX, centerY, radius);

        // Draw the center dot
        canvas.drawCircle(centerX, centerY, centerRadius, centerCirclePaint);
    }

    private void drawHands(Canvas canvas, int x, int y, int radius) {
        Calendar now = Calendar.getInstance();
        int hours = now.get(Calendar.HOUR_OF_DAY);
        int minutes = now.get(Calendar.MINUTE);
        int seconds = now.get(Calendar.SECOND);

        // Hour hand
        drawHand(canvas, x, y, (hours % 12 + minutes / 60f) * 30, radius * 0.5f, 15);
        // Minute hand
        drawHand(canvas, x, y, minutes * 6, radius * 0.7f, 10);
        // Second hand
        drawHand(canvas, x, y, seconds * 6, radius * 0.9f, 6);
    }

    private void drawHand(Canvas canvas, int x, int y, double angle, float handRadius, float handWidth) {
        double rad = Math.toRadians(angle - 90);
        int endX = (int) (x + Math.cos(rad) * handRadius);
        int endY = (int) (y + Math.sin(rad) * handRadius);
        paint.setStrokeWidth(handWidth);
        canvas.drawLine(x, y, endX, endY, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float dx = event.getX() - centerX;
            float dy = event.getY() - centerY;
            if (Math.sqrt(dx * dx + dy * dy) <= centerRadius) {
                // User touched the center circle, start AddNewPlan activity
                getContext().startActivity(new Intent(getContext(), AddNewPlan.class));
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable); // Stop clock updates when the view is detached
    }
}
