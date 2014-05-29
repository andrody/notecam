package view_fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.koruja.notecam.R;

import java.util.Calendar;

import model.Aula;


/**
 * Dialog para escolher o Start Time ou End Time da Classe
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    boolean startime;
    int hour;
    int minute;
    int position;

    public interface OnOkDialogListener {
        public void onOkDialog(int hour, int minute, int position, boolean startime);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        hour = getArguments().getInt(Aula.HORA);
        minute = getArguments().getInt(Aula.MINUTO);
        position = getArguments().getInt(Aula.POSITION);
        startime = getArguments().getBoolean(Aula.STARTIME);


        // Create a new instance of TimePickerDialog and return it
        Dialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        if(startime)
            dialog.setTitle("Start Time");
        else
            dialog.setTitle("End Time");
        return dialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(startime)
            Toast.makeText(getActivity(), "Start Time set to " + hourOfDay + ":" + String.format("%02d", minute) , Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "End Time set to " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
        int id;

        //if (getActivity().findViewById(R.id.mainLinearLayout) != null)
            id = R.id.mainLinearLayout;
        //else
           // id = R.id.mainLinearLayout_land;
         //  id = 0;

        OnOkDialogListener fragment = ((AddMateriaFragment) getActivity().getSupportFragmentManager().findFragmentById(id)).getAddAulasFragment();
        fragment.onOkDialog(hourOfDay,minute,position,startime);



    }
}