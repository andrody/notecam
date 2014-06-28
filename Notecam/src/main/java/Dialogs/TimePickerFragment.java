package Dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.koruja.notecam.R;

import java.util.Calendar;

import model.Aula;
import view_fragment.AddMateriaFragment;


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
        hour = getArguments().getInt(Aula.HORA);
        minute = getArguments().getInt(Aula.MINUTO);
        position = getArguments().getInt(Aula.POSITION);
        startime = getArguments().getBoolean(Aula.STARTIME);


        // Create a new instance of TimePickerDialog and return it
        Dialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        if(startime)
            dialog.setTitle(getActivity().getString(R.string.horario_inicio));
        else
            dialog.setTitle(getActivity().getString(R.string.horario_fim));
        return dialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //if(startime)
        //    Toast.makeText(getActivity(), "Horário de início setado " + hourOfDay + ":" + String.format("%02d", minute) , Toast.LENGTH_SHORT).show();
        //else
        //    Toast.makeText(getActivity(), "End Time set to " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
        int id;

        id = R.id.mainLinearLayout;


        OnOkDialogListener fragment = ((AddMateriaFragment) getActivity().getFragmentManager().findFragmentById(id)).getAddAulasFragment();
        fragment.onOkDialog(hourOfDay,minute,position,startime);



    }
}