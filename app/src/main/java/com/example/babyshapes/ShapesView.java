package com.example.babyshapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class ShapesView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    // Sunet scurt la atingerea formei
    private final ToneGenerator toneGenerator =
            new ToneGenerator(AudioManager.STREAM_MUSIC, 70);

    private final int[] colors = {
            Color.rgb(229, 57, 53),   // rosu
            Color.rgb(30, 136, 229),  // albastru
            Color.rgb(251, 192, 45),  // galben
            Color.rgb(67, 160, 71),   // verde
            Color.rgb(142, 36, 170),  // mov
            Color.rgb(251, 140, 0),   // portocaliu
            Color.rgb(236, 64, 122)   // roz
    };

    private float cx, cy, size;
    private int shape = 0;
    private boolean initialized = false;
    private boolean animating = false;

    public ShapesView(Context context) {
        super(context);

        // Fundal alb permanent
        setBackgroundColor(Color.WHITE);

        setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    protected void onSizeChanged(
            int w,
            int h,
            int oldw,
            int oldh
    ) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Marimea formelor
        size = Math.min(w, h) * 0.28f;

        if (!initialized) {
            nextShape();
            initialized = true;
        }
    }

    private void nextShape() {

        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        // Alege una dintre cele 4 forme
        shape = random.nextInt(4);

        // Alege o culoare aleatorie
        paint.setColor(
                colors[random.nextInt(colors.length)]
        );

        float margin = size * 0.65f + 20f;

        // Pozitie aleatorie pe ecran
        cx = margin
                + random.nextFloat()
                * Math.max(
                        1f,
                        getWidth() - 2f * margin
                );

        cy = margin
                + random.nextFloat()
                * Math.max(
                        1f,
                        getHeight() - 2f * margin
                );

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // IMPORTANT:
        // Ecranul este intotdeauna alb
        canvas.drawColor(Color.WHITE);

        float r = size / 2f;

        switch (shape) {

            // CERC
            case 0:

                canvas.drawCircle(
                        cx,
                        cy,
                        r,
                        paint
                );

                break;


            // PATRAT
            case 1:

                canvas.drawRoundRect(
                        new RectF(
                                cx - r,
                                cy - r,
                                cx + r,
                                cy + r
                        ),
                        r * 0.18f,
                        r * 0.18f,
                        paint
                );

                break;


            // TRIUNGHI
            case 2: {

                Path p = new Path();

                p.moveTo(
                        cx,
                        cy - r
                );

                p.lineTo(
                        cx + r,
                        cy + r
                );

                p.lineTo(
                        cx - r,
                        cy + r
                );

                p.close();

                canvas.drawPath(
                        p,
                        paint
                );

                break;
            }


            // STEA
            default: {

                Path p = new Path();

                for (int i = 0; i < 10; i++) {

                    double angle =
                            -Math.PI / 2
                            + i * Math.PI / 5;

                    float radius =
                            (i % 2 == 0)
                                    ? r
                                    : r * 0.45f;

                    float x =
                            cx
                            + (float) Math.cos(angle)
                            * radius;

                    float y =
                            cy
                            + (float) Math.sin(angle)
                            * radius;

                    if (i == 0) {
                        p.moveTo(x, y);
                    } else {
                        p.lineTo(x, y);
                    }
                }

                p.close();

                canvas.drawPath(
                        p,
                        paint
                );

                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            // Nu permite apasari multiple
            // in timpul animatiei
            if (animating) {
                return true;
            }

            float dx = e.getX() - cx;
            float dy = e.getY() - cy;

            // Verifica daca forma a fost atinsa
            if (
                    dx * dx + dy * dy
                    <= size * size * 0.55f
            ) {

                animating = true;

                // Sunet scurt
                toneGenerator.startTone(
                        ToneGenerator.TONE_PROP_BEEP2,
                        100
                );

                // Forma dispare rapid
                animate()
                        .scaleX(0.15f)
                        .scaleY(0.15f)
                        .alpha(0f)
                        .setDuration(120)
                        .withEndAction(() -> {

                            // Pregateste forma urmatoare
                            nextShape();

                            // Revine instant la dimensiunea normala
                            setScaleX(1f);
                            setScaleY(1f);
                            setAlpha(1f);

                            animating = false;

                        })
                        .start();
            }

            return true;
        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // Elibereaza resursele audio
        toneGenerator.release();
    }
}
