package com.itzik.samplewear.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itzik.samplewear.R;


/**
 * Created by Michael on 01/01/2015.
 */
public class DataValuePresenterView extends LinearLayout
{
    private static final String LOG_TAG = DataValuePresenterView.class.getSimpleName();
    private static final int COLLAPSE_ANIMATION_DURATION_DEFAULT = 1000;
    private static final int TIME_DELAY_BEFORE_STARTING_ANIMATION = 400;

    private int mCollapseAnimationDuration = COLLAPSE_ANIMATION_DURATION_DEFAULT;
    private int mTimeDelayBeforeStartingAnimation = TIME_DELAY_BEFORE_STARTING_ANIMATION;

    private TextView mTitle;
    private LinearLayout mLayoutCenter;
    private LinearLayout mLayoutLeft;
    private LinearLayout mLayoutRight;

    private TextView mLayoutCenterMainText;
    private TextView mLayoutCenterAuxText;
    private TextView mLayoutCenterMeasurementUnit;

    private TextView mLayoutLeftMainText;
    private TextView mLayoutLeftAuxText;

    private TextView mLayoutRightMainText;
    private TextView mLayoutRightAuxText;

    private boolean mIsDuringAnimation;
    private OnClickListener mOnClickListener;
    private boolean mCanStartAnimation;

    public DataValuePresenterView(Context context)
    {
        super(context);
        init();
    }

    public DataValuePresenterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DataValuePresenterView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DataValuePresenterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setCollapseAnimationDuration(int collapseAnimationDuration)
    {
        mCollapseAnimationDuration = collapseAnimationDuration;
    }

    public void setTimeDelayBeforeStartingAnimation(int timeDelayBeforeStartingAnimation)
    {
        mTimeDelayBeforeStartingAnimation = timeDelayBeforeStartingAnimation;
    }

    private void init()
    {
//        setOrientation(LinearLayout.VERTICAL);
        mCanStartAnimation = false;
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                DataValuePresenterView.this.getViewTreeObserver().removeOnPreDrawListener(this);
//                mCanStartAnimation = true;
//                collapseViewAnimated();
                return false;
            }
        });
        LayoutInflater.from(getContext()).inflate(R.layout.view_data_type_value_presenter, this);
        assignViews();
    }

    private void assignViews()
    {
        mTitle = (TextView) findViewById(R.id.title);
        mLayoutCenter = (LinearLayout) findViewById(R.id.layoutCenter);
        mLayoutCenterMainText = (TextView) findViewById(R.id.layout_center_main_text);
        mLayoutCenterAuxText = (TextView) findViewById(R.id.layout_center_aux_text);
        mLayoutCenterMeasurementUnit = (TextView) findViewById(R.id.layout_center_measurement_unit);
        mLayoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
        mLayoutLeftMainText = (TextView) findViewById(R.id.layout_left_main_text);
        mLayoutLeftAuxText = (TextView) findViewById(R.id.layout_left_aux_text);
        mLayoutRight = (LinearLayout) findViewById(R.id.layoutRight);
        mLayoutRightMainText = (TextView) findViewById(R.id.layout_right_main_text);
        mLayoutRightAuxText = (TextView) findViewById(R.id.layout_right_aux_text);

        mLayoutCenter.setVisibility(View.INVISIBLE);
        mLayoutRight.setVisibility(View.INVISIBLE);
        mLayoutLeft.setVisibility(View.INVISIBLE);

        mLayoutCenter.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnClickListener.onClick(v);
            }
        });

    }

    @Override
    public void setOnClickListener(OnClickListener l)
    {
        mOnClickListener = l;
    }

    private void collapseViewAnimated()
    {
        if (mIsDuringAnimation)
        {
            Log.d(LOG_TAG, "collapseViewAnimated() During animation - do nothing");
            return;
        }
        if (!mCanStartAnimation)
        {
            Log.d(LOG_TAG, "collapseViewAnimated() can't start animation - do nothing");
            return;
        }
        Log.d(LOG_TAG, "collapseViewAnimated(), left width: " + mLayoutLeft.getWidth() + ", Right width: " + mLayoutRight.getWidth());

        float scaleSize = 1.5f;
        AnimatorSet set = new AnimatorSet().setDuration(mCollapseAnimationDuration);
        ObjectAnimator leftLayoutTranslateAnimation = ObjectAnimator.ofFloat(mLayoutLeft, View.TRANSLATION_X, mLayoutLeft.getWidth());
        ObjectAnimator leftLayoutAlphaAnimation = ObjectAnimator.ofFloat(mLayoutLeft, View.ALPHA, 1f, 0f);
        ObjectAnimator leftLayoutScaleXAnimation = ObjectAnimator.ofFloat(mLayoutLeft, View.SCALE_X, 1f, scaleSize);
        ObjectAnimator leftLayoutScaleYAnimation = ObjectAnimator.ofFloat(mLayoutLeft, View.SCALE_Y, 1f, scaleSize);

        ObjectAnimator rightLayoutTranslateAnimation = ObjectAnimator.ofFloat(mLayoutRight, View.TRANSLATION_X, -mLayoutRight.getWidth());
        ObjectAnimator rightLayoutFadeAnimation = ObjectAnimator.ofFloat(mLayoutRight, View.ALPHA, 1f, 0f);
        ObjectAnimator rightLayoutScaleXAnimation = ObjectAnimator.ofFloat(mLayoutRight, View.SCALE_X, 1f, scaleSize);
        ObjectAnimator rightLayoutScaleYAnimation = ObjectAnimator.ofFloat(mLayoutRight, View.SCALE_Y, 1f, scaleSize);

        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(leftLayoutTranslateAnimation, leftLayoutAlphaAnimation, rightLayoutTranslateAnimation, rightLayoutFadeAnimation, leftLayoutScaleXAnimation, leftLayoutScaleYAnimation, rightLayoutScaleXAnimation, rightLayoutScaleYAnimation);
        AnimatorSet delayedSet = new AnimatorSet();
        delayedSet.setStartDelay(mTimeDelayBeforeStartingAnimation);
        delayedSet.play(set);

        delayedSet.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mIsDuringAnimation = true;
                mLayoutRight.setVisibility(View.VISIBLE);
                mLayoutLeft.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mIsDuringAnimation = false;
                mLayoutRight.setScaleX(1f);
                mLayoutRight.setScaleY(1f);

                mLayoutLeft.setScaleX(1f);
                mLayoutLeft.setScaleY(1f);

                mLayoutRight.setAlpha(1.0f);
                mLayoutLeft.setAlpha(1.0f);

                mLayoutRight.setTranslationX(0);
                mLayoutLeft.setTranslationX(0);

                mLayoutRight.setVisibility(View.INVISIBLE);
                mLayoutLeft.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mIsDuringAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        delayedSet.start();
    }

    public void setTitle(String title)
    {
        mTitle.setText(title);
    }

    public void setTitleVisibility(int visibility)
    {
        mTitle.setVisibility(visibility);
    }

    public void applyDataValues(DataValuePresenterController.DataValue values)
    {
        mLayoutCenterMainText.setText(values.getConvertedValue());
        mLayoutCenterAuxText.setText(values.getAux());
        mLayoutCenterMeasurementUnit.setText(values.getUnits());
    }

    public void applyDataSetValues(DataValuePresenterController.DataValue[] dataValues)
    {
        showDataSet();
        mLayoutCenterMainText.setText(dataValues[1].getConvertedValue());
        mLayoutCenterAuxText.setText(dataValues[1].getAux());
        mLayoutCenterMeasurementUnit.setText(dataValues[1].getUnits());

        mLayoutLeftMainText.setText(dataValues[0].getConvertedValue());
        mLayoutLeftAuxText.setText(dataValues[0].getAux());

        mLayoutRightMainText.setText(dataValues[2].getConvertedValue());
        mLayoutRightAuxText.setText(dataValues[2].getAux());

//        collapseViewAnimated();
    }

    private void showDataSet()
    {
        mLayoutCenter.setVisibility(VISIBLE);
        mLayoutRight.setVisibility(VISIBLE);
        mLayoutLeft.setVisibility(VISIBLE);
    }
}
