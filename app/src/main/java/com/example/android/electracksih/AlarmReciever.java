package com.example.android.electracksih;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by NITANT SOOD on 31-03-2018.
 */

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id,command;
        long[] vibrate_patter={0,500,300,800};
        String title="";
        Uri uri= Uri.parse("uri://");
        id=intent.getIntExtra("id",0);
        command=intent.getIntExtra("command",0);
        String mid="";

        if(command==0){
            mid="lightturnedon";
        }
        else{
            mid="lightturnedoff";
        }
        String urlString = "http://172.28.25.147:5000";
        switch(id){
            case 1: if(command==0){
                title="Lights 1 turned off!!";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=114&arg3=" + command;
            }
            else{
                title="Light 1 turned on";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=114&arg3=" + command;
            }
            break;
            case 2: if(command==0){
                title="Lights 2 turned off!!";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=113&arg3=" + command;
            }
            else{
                title="Light 2 turned on";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=113&arg3=" + command;
            }
                break;case 3: if(command==0){
                title="Fan turned off!!";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=115&arg3=" + command;
            }
            else{
                title="Fan turned on";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=115&arg3=" + command;
            }
            break;
            case 4: if(command==0){
                title="Exhaust turned off!!";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=112&arg3=" + command;
            }
            else{
                title="Exhaust turned on";
                urlString = urlString + "/" + mid + "?arg1=1&arg2=112&arg3=" + command;
            }
            break;
        }

        myTask myTask = new myTask(context, urlString, new com.example.android.electracksih.myTask.onSpecificStateChangeListener() {
            @Override
            public void onStateChanged(String string) {

            }
        });
        myTask.execute(urlString);
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setContentTitle("Electrack Alert !!")
                .setContentText(title)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, android.R.color.holo_orange_light))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setShowWhen(true)
                .setVibrate(vibrate_patter)
                .setSound(alarmsound)
                .setLights(Color.MAGENTA,300,1000);


        Intent resultIntent=new Intent(context,Device_Detail.class);
        resultIntent.putExtra("id",id);




        PendingIntent resultPendingIntent=PendingIntent.getActivity(context,id,resultIntent,PendingIntent.FLAG_ONE_SHOT);

        mBuilder.addAction(android.R.drawable.dialog_frame,"View",resultPendingIntent);


        NotificationManager mNotificationManager=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id,mBuilder.build());
    }
}
