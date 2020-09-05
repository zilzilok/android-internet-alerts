package ru.zilzilok.android_internet_alerts.utils.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import androidx.core.content.ContextCompat;

import ru.zilzilok.android_internet_alerts.R;

public class HorizontalDottedProgressBar extends View {
    private static final int DOT_RADIUS = 10;
    private static final int BOUNCE_DOT_RADIUS = DOT_RADIUS * 2;
    private static final int DOT_AMOUNT = 3;
    private static final int DOT_STEP = 2 * BOUNCE_DOT_RADIUS + 5;
    private static final int WIDTH = BOUNCE_DOT_RADIUS * 2 + DOT_STEP * (DOT_AMOUNT - 1);
    private static final int DURATION = 200;

    private int dotPosition;            //  which position dot has to bounce
    Paint paint = new Paint();

    public HorizontalDottedProgressBar(Context context) {
        super(context);
    }

    public HorizontalDottedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalDottedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //set the color for the dot that you want to draw
        paint.setColor(ContextCompat.getColor(getContext(), R.color.white));

        //function to create dot
        createDot(canvas, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //  Animation called when attaching to the window, i.e to your screen
        startAnimation();
    }

    private void createDot(Canvas canvas, Paint paint) {
        for (int i = 0; i < DOT_AMOUNT; i++) {
            if (i == dotPosition) {
                canvas.drawCircle(BOUNCE_DOT_RADIUS + (i * DOT_STEP), BOUNCE_DOT_RADIUS, BOUNCE_DOT_RADIUS, paint);
            } else {
                canvas.drawCircle(BOUNCE_DOT_RADIUS + (i * DOT_STEP), BOUNCE_DOT_RADIUS, DOT_RADIUS, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //MUST CALL THIS
        setMeasuredDimension(WIDTH, BOUNCE_DOT_RADIUS * 2);
    }

    private void startAnimation() {
        BounceAnimation bounceAnimation = new BounceAnimation();
        bounceAnimation.setDuration(DURATION);
        bounceAnimation.setRepeatCount(Animation.INFINITE);
        bounceAnimation.setInterpolator(new LinearInterpolator());
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                dotPosition++;
                /*
                    when dotPosition == DOT_AMOUNT,
                    then start again applying animation from 0th position,
                    i.e  mDotPosition = 0;
                 */
                if (dotPosition == DOT_AMOUNT) {
                    dotPosition = 0;
                }
            }
        });
        startAnimation(bounceAnimation);
    }


    private class BounceAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //call invalidate to redraw your view again.
            invalidate();
        }
    }
}