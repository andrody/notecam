package CustomViews;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.koruja.notecam.R;

/**
 * Created by andrody on 18/06/2014.
 */
public class FlashCheckBox extends CheckBox {

    public FlashCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setButtonDrawable(new StateListDrawable());
    }

    @Override
    public void setChecked(boolean t){
        if(t)
        {
            this.setBackgroundResource(R.drawable.ic_action_flash_on);
        }
        else
        {
            this.setBackgroundResource(R.drawable.ic_action_flash_off);
        }
        super.setChecked(t);
    }
}