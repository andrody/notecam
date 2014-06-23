package android.support.v4.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class SlowViewPager extends ViewPager {

    // The speed of the scroll used by setCurrentItem()
    private static final int VELOCITY = 600;

    public SlowViewPager(Context context) {
        super(context);
    }

    public SlowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, VELOCITY);
    }

}