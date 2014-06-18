package CustomViews;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.koruja.notecam.R;

/**
 * Created by andrody on 18/06/2014.
 */
public class CustomCheckBox extends CheckBox {

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setButtonDrawable(new StateListDrawable());
    }

    @Override
    public void setChecked(boolean t){
        /*if(t)
        {
            this.setBackgroundResource(R.drawable.checkbox_select);
        }
        else
        {
            this.setBackgroundResource(R.drawable.checkbox_deselect);
        }*/
        super.setChecked(t);
    }
}