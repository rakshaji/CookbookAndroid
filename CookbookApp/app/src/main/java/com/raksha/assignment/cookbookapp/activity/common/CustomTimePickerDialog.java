package com.raksha.assignment.cookbookapp.activity.common;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

public class CustomTimePickerDialog extends TimePickerDialog {

    private CharSequence title;
    public static Calendar calendar = Calendar.getInstance();
    private static TextView timeTextView;

    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay,
                                  int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);
        setTitle(title);
    }

    public void setPermanentTitle(CharSequence title) {
        this.title = title;
        setTitle(title);
    }

    public void setTimeTextView(TextView timeTextView) {
        this.timeTextView = timeTextView;
    }

    public static CustomTimePickerDialog.OnTimeSetListener timeSetListener = new
            CustomTimePickerDialog
            .OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        }

        private void updateTimeLabel() {
            int hr = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            String timeStr = (hr < 10 ? "0"+hr : hr) + ":" + (min < 10 ? "0"+min : min);
            timeTextView.setText(timeStr);
        }
    };
}