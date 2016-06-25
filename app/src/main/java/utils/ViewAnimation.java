package utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;

public class ViewAnimation {

    /*
       If fab is visible, starts end animation
       if not, starts start animation
    */
    public static void startFabAnimation(View view){
        int centerX = view.getWidth() / 2;
        int centerY = view.getHeight() / 2;

        float radius = (float) Math.hypot(centerX, centerY);

        Animator animator;

        if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE){
            animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 0, radius);
            view.setVisibility(View.VISIBLE);
        }
        else{
            animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, radius, 0);
            animator.addListener(new AnimationAdapter(view));
        }

        animator.start();
    }

    //Because we can't declare View final
    public static class AnimationAdapter extends AnimatorListenerAdapter {
        private View view;

        public AnimationAdapter(View view){
            this.view = view;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);

            view.setVisibility(View.INVISIBLE);
        }
    }
}
