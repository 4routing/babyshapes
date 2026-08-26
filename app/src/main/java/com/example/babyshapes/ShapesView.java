package com.example.babyshapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class ShapesView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    private final int[] colors = {
        Color.rgb(229,57,53),
        Color.rgb(30,136,229),
        Color.rgb(251,192,45),
        Color.rgb(67,160,71),
        Color.rgb(142,36,170),
        Color.rgb(251,140,0),
        Color.rgb(236,64,122)
    };

    private float cx, cy, size;
    private int shape = 0;
    private boolean initialized = false;
    private boolean flashing = false;

    public ShapesView(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        size = Math.min(w, h) * 0.28f;

        if (!initialized) {
            nextShape();
            initialized = true;
        }
    }

    private void nextShape() {
        if (getWidth() <= 0 || getHeight() <= 0) return;

        shape = random.nextInt(4);
        paint.setColor(colors[random.nextInt(colors.length)]);

        float margin = size * 0.65f + 20f;

        cx = margin + random.nextFloat()
                * Math.max(1f, getWidth() - 2f * margin);

        cy = margin + random.nextFloat()
                * Math.max(1f, getHeight() - 2f * margin);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (flashing) {
            canvas.drawColor(Color.WHITE);
            return;
        }

        canvas.drawColor(Color.BLACK);

        float r = size / 2f;

        switch (shape) {
            case 0:
                canvas.drawCircle(cx, cy, r, paint);
                break;

            case 1:
                canvas.drawRoundRect(
                        new RectF(cx - r, cy - r, cx + r, cy + r),
                        r * 0.18f,
                        r * 0.18f,
                        paint
                );
                break;

            case 2: {
                Path p = new Path();

                p.moveTo(cx, cy - r);
                p.lineTo(cx + r, cy + r);
                p.lineTo(cx - r, cy + r);
                p.close();

                canvas.drawPath(p, paint);
                break;
            }

            default: {
                Path p = new Path();

                for (int i = 0; i < 10; i++) {
                    double a = -Math.PI / 2 + i * Math.PI / 5;

                    float rr = (i % 2 == 0)
                            ? r
                            : r * 0.45f;

                    float x = cx + (float) Math.cos(a) * rr;
                    float y = cy + (float) Math.sin(a) * rr;

                    if (i == 0) {
                        p.moveTo(x, y);
                    } else {
                        p.lineTo(x, y);
                    }
                }

                p.close();
                canvas.drawPath(p, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && !flashing) {

            float dx = e.getX() - cx;
            float dy = e.getY() - cy;

            if (dx * dx + dy * dy <= size * size * 0.55f) {

                flashing = true;
                invalidate();

                postDelayed(() -> {
                    flashing = false;
                    nextShape();
                }, 1000);
            }

            return true;
        }

        return true;
    }
}
