package com.example.android.electracksih;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by shruti on 27-03-2018.
 */

public class Device_Fragment extends android.support.v4.app.Fragment implements  DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    RecyclerView mRecyclerView;


    Timestamp timestamp;
    TextView alarmDate;

    int year,month,dom,hours,mins;
    RecyclerView mRecyclerView2;
    RecyclerView.Adapter mAdapter;
    RecyclerView.Adapter mAdapter2;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.LayoutManager mLayoutManager2;
    Switch onOff;

    ImageButton DateSelector;
int a;
    public Device_Fragment() {
    }

    ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_device__fragment, container, false);
        onOff = (Switch) view.findViewById(R.id.onOff);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.device_recycler_view_frag);
        // in content do not change the layout size of the RecyclerView
        //  mRecyclerView.setHasFixedSize(true);
Bundle bundle = getArguments();
 a = bundle.getInt("id",0);
        // use a linear layout manager


        DateSelector=view.findViewById(R.id.date_selector);
        // in content do not change the layout size of the RecyclerView
        //  mRecyclerView.setHasFixedSize(true);
        alarmDate=view.findViewById(R.id.dateText);
        mLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this.getActivity());
        mRecyclerView.setAdapter(mAdapter);

        DateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c=Calendar.getInstance();
                int year=c.get(Calendar.YEAR);
                int month=c.get(Calendar.MONTH);
                int dom=c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(),Device_Fragment.this,year,month,dom);
                datePickerDialog.show();
            }
        });

onOff.setOnClickListener(new View.OnClickListener() {





    @Override
    public void onClick(View v) {
        String mid;
        int stat;
        int command;
        boolean switch_checked = onOff.isChecked();
        if (switch_checked) {
            mid = "lightturnedon";
            stat = 1;
            command = 1;
        } else {
            mid = "lightturnedoff";
            stat = 0;
            command = 2;
        }

        if(alarmDate.getText().toString().equals("")) {
//                String urlString="http://192.168.43.189:5000/lightturnedon?arg1=&arg2=&ar3=";
        String urlString = "http://172.28.25.147:5000";
        switch (a) {
            case 1: {
                urlString = urlString + "/" + mid + "?arg1=1&arg2=114&arg3=" + stat;
            }
            case 2: {
                urlString = urlString + "/" + mid + "?arg1=1&arg2=113&arg3=" + stat;

            }
            case 3: {
                urlString = urlString + "/" + mid + "?arg1=1&arg2=112&arg3=" + stat;

            }
            case 4: {
                urlString = urlString + "/" + mid + "?arg1=1&arg2=115&arg3=" + stat;

            }

        }
        myTask myTask = new myTask(getContext(), urlString, new myTask.onSpecificStateChangeListener() {
            @Override
            public void onStateChanged(String string) {
                Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
            }
        });
        myTask.execute(urlString);
    }
         else{
            setAlarm(a,command);
        }
    }
});


        return view;

    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year=year;
        this.month=month;
        this.dom=dayOfMonth;


        Calendar c=Calendar.getInstance();
        int hours=c.get(Calendar.HOUR_OF_DAY);
        int mins=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(),Device_Fragment.this,hours,mins,true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        this.hours=hourOfDay;
        this.mins=minute;

        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,dom);
        cal.set(Calendar.HOUR_OF_DAY,hours);
        cal.set(Calendar.MINUTE,mins);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        timestamp=new Timestamp(cal.getTimeInMillis());
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        alarmDate.setText(simpleDateFormat.format(timestamp));

        Toast.makeText(getContext(), "Command Scheduled", Toast.LENGTH_SHORT).show();

    }

    private void setAlarm(int DeviceID,int command) {
        AlarmManager am=(AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i=new Intent(getContext(),AlarmReciever.class);
        i.putExtra("id",DeviceID);
        i.putExtra("command",command);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getContext(),a,i,PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(am.RTC_WAKEUP,timestamp.getTime(),pendingIntent);
    }


}
