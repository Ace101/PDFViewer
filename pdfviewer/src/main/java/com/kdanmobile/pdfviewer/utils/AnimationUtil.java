package com.kdanmobile.pdfviewer.utils;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

/**
 * @classname：AnimationUtil
 * @author：luozhipeng
 * @date：2/3/18 13:57
 * @description：动画工具类
 */
public class AnimationUtil {
    private static ObjectAnimator showTopToBottom;
    private static ObjectAnimator showBottomToTop;
    private static ObjectAnimator showRightToLeft;
    private static ObjectAnimator showLeftToRight;
    private static ObjectAnimator showRotation;

    private AnimationUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void ViewScaleAnimation(View view, float toScale) {
        if ((view != null) && (view.getAnimation() != null)) {
            view.getAnimation().start();
            return;
        }
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, toScale, 1.0f);
        anim1.setDuration(16000);
        anim1.setRepeatMode(Animation.RESTART);
        anim1.setRepeatCount(Animation.INFINITE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, toScale, 1.0f);
        anim2.setDuration(16000);
        anim2.setRepeatMode(Animation.RESTART);
        anim2.setRepeatCount(Animation.INFINITE);
//        ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "alpha",
//                1.0f, 0.9f, 1.0f);
//        anim3.setDuration(16000);
//        anim3.setRepeatMode(Animation.RESTART);
//        anim3.setRepeatCount(Animation.INFINITE);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new LinearInterpolator());
        animSet.playTogether(anim1, anim2);
        animSet.start();
    }

    private static void ImageViewAnimation(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0.4f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 1f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ);
        /****** 1->2->1的循环模式 ******/
        anim.setRepeatCount(Animation.RESTART);
        /****** 无限模式 ******/
        anim.setRepeatMode(Animation.INFINITE);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(16000).start();
    }

    /****** 隐藏buttons控件,从控件顶部往底部回收隐藏 ******/
    public static void hideViewFromTopToBottom(final View view) {
        if ((showBottomToTop != null) && showBottomToTop.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() == View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Y", view.getTop(), view.getTop() + view.getHeight());
            showBottomToTop = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhY);
            showBottomToTop.setInterpolator(new AccelerateInterpolator());
            showBottomToTop.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.INVISIBLE));
                    }
                }
            });
            showBottomToTop.setDuration(300).start();
        }
    }

    /****** 显示buttons控件,从控件底部往控件顶部扩展显示 ******/
    public static void showViewFromBottomToTop(final View view) {
        if ((showBottomToTop != null) && showBottomToTop.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 0f, 1.0f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Y", view.getTop() + view.getHeight(), view.getTop());
            showBottomToTop = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhY);
            showBottomToTop.setInterpolator(new AccelerateInterpolator());
            showBottomToTop.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.VISIBLE));
                    }
                }
            });
            showBottomToTop.setDuration(300).start();
        }
    }

    public static void showViewFromRightToLeft(final View view) {
        if ((showRightToLeft != null) && showRightToLeft.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 0f, 1.0f);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("X", view.getLeft() + view.getWidth(), view.getLeft());
            showRightToLeft = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhX);
            showRightToLeft.setInterpolator(new AccelerateInterpolator());
            showRightToLeft.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.VISIBLE));
                    }
                }
            });
            showRightToLeft.setDuration(300).start();
        }
    }

    public static void hideViewFromLeftToRight(final View view) {
        if ((showRightToLeft != null) && showRightToLeft.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() == View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("X", view.getLeft(), view.getLeft() + view.getWidth());
            showRightToLeft = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhX);
            showRightToLeft.setInterpolator(new AccelerateInterpolator());
            showRightToLeft.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.INVISIBLE));
                    }
                }
            });
            showRightToLeft.setDuration(300).start();
        }
    }

    /****** 隐藏buttons控件，从控件底部往控件顶部收缩隐藏 ******/
    public static void hideViewFromBottomToTop(final View view) {
        if ((showTopToBottom != null) && showTopToBottom.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() == View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Y", view.getTop(), view.getTop() - view.getHeight());
            showTopToBottom = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhY);
            showTopToBottom.setInterpolator(new AccelerateInterpolator());
            showTopToBottom.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.INVISIBLE));
                    }
                }
            });
            showTopToBottom.setDuration(300).start();
        }
    }

    /****** 显示buttons控件,从控件顶部往控件底部扩展显示 ******/
    public static void showViewFromTopToBottom(final View view) {
        if ((showTopToBottom != null) && showTopToBottom.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 0f, 1.0f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Y", view.getTop() - view.getHeight(), view.getTop());
            showTopToBottom = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhY);
            showTopToBottom.setInterpolator(new AccelerateInterpolator());
            showTopToBottom.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.VISIBLE));
                    }
                }
            });
            showTopToBottom.setDuration(300).start();
        }
    }

    public static void showViewFromLeftToRight(final View view) {
        if ((showLeftToRight != null) && showLeftToRight.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 0f, 1.0f);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("X", view.getLeft() - view.getWidth(), view.getLeft());
            showLeftToRight = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhX);
            showLeftToRight.setInterpolator(new AccelerateInterpolator());
            showLeftToRight.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.VISIBLE));
                    }
                }
            });
            showLeftToRight.setDuration(300).start();
        }
    }

    public static void hideViewFromRightToLeft(final View view) {
        if ((showLeftToRight != null) && showLeftToRight.isRunning()) {
            return;
        }

        if ((view != null) && (view.getVisibility() == View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("X", view.getLeft(), view.getLeft() - view.getWidth());
            showLeftToRight = ObjectAnimator.ofPropertyValuesHolder(view, pvhA, pvhX);
            showLeftToRight.setInterpolator(new AccelerateInterpolator());
            showLeftToRight.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.INVISIBLE));
                    }
                }
            });
            showLeftToRight.setDuration(300).start();
        }
    }

    public static void showViewAlph(final View view) {
        if ((view != null) && (view.getVisibility() != View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 0f, 1.0f);
            ObjectAnimator animAlph1 = ObjectAnimator.ofPropertyValuesHolder(view, pvhA);
            animAlph1.setInterpolator(new AccelerateInterpolator());
            animAlph1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.VISIBLE));
                    }
                }
            });
            animAlph1.setDuration(300).start();
        }
    }

    public static void hideViewAlph(final View view) {
        if ((view != null) && (view.getVisibility() == View.VISIBLE)) {
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0f);
            ObjectAnimator animAlph1 = ObjectAnimator.ofPropertyValuesHolder(view, pvhA);
            animAlph1.setInterpolator(new AccelerateInterpolator());
            animAlph1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (null != view) {
                        view.post(() -> view.setVisibility(View.GONE));
                    }
                }
            });
            animAlph1.setDuration(300).start();
        }
    }

    /****** 结束该动画 ******/
    public static void cancleAnim() {
        if (showTopToBottom != null) {
            showTopToBottom.cancel();
        }
        if (showBottomToTop != null) {
            showBottomToTop.cancel();
        }
        if (showLeftToRight != null) {
            showLeftToRight.cancel();
        }
        if (showRightToLeft != null) {
            showRightToLeft.cancel();
        }
        if(showRotation != null){
            showRotation.cancel();
        }
        showTopToBottom = null;
        showBottomToTop = null;
        showRightToLeft = null;
        showLeftToRight = null;
        showRotation = null;
    }

    public static void rotateFloatingButton(boolean isMenuOpen, View view){
        if ((showRotation != null) && showRotation.isRunning()) {
            return;
        }
        if(view != null) {
            showRotation = isMenuOpen ? ObjectAnimator.ofFloat(view
                    , "rotation", 45F, 0f) : ObjectAnimator.ofFloat(view, "rotation", 0f, 45f);
            showRotation.setDuration(150);
            showRotation.setInterpolator(new LinearInterpolator());
            showRotation.start();
        }
    }
}
