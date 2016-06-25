package decorations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.colinforzeal.blury.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration{
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;
    private int mDividerPaddingLeft;

    public DividerItemDecoration(Context context){
        final TypedArray attributes = context.obtainStyledAttributes(ATTRS);
        mDivider = attributes.getDrawable(0);
        attributes.recycle();

        mDividerPaddingLeft = context.getResources().getDimensionPixelSize(R.dimen.divider_padding_left);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int left = parent.getPaddingLeft() + mDividerPaddingLeft;
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();

        for (int i = 0;i < childCount;i++){
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            //Creates full-width divider for 0 item
            if (i == 0){
                mDivider.setBounds(left - mDividerPaddingLeft, top, right, bottom);
            }
            else{
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }
}
