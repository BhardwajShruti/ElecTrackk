package com.example.android.electracksih;

import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Aadhaar extends AppCompatActivity {

    Button submit;
    TextToSpeech tta;
    EditText password;
    public String checkbox_detail;
    String Aadhaar;
    TextView tv1, tv2, tv3, tv4, tv5, tv6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aadhaar);
        tv1 = (TextView) findViewById(R.id.txt1);
        tv2 = (TextView) findViewById(R.id.txt2);
        tv3 = (TextView) findViewById(R.id.txt3);
        tv4 = (TextView) findViewById(R.id.textView3);
        tv5 = (TextView) findViewById(R.id.textView4);
        tv6 = (TextView) findViewById(R.id.textView5);
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);

        }
/*        tta = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status)
            {
                if(tta!=null)
                {
                    tta.setLanguage(Locale.ENGLISH);
                    tta.setSpeechRate(1);
                    tta.setPitch((float)1);
                }
                else
                {

                    Toast.makeText(getApplicationContext(),"No Device",Toast.LENGTH_SHORT).show();
                }
            }
        });*/


    }
    /*public void speakOut(View view){
        tta.speak(password.getHint().toString(),TextToSpeech.QUEUE_FLUSH,null);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                Aadhaar = data.getStringExtra("SCAN_RESULT");

                ArrayList<String> Key = new ArrayList<String>();
                ArrayList<String> Value = new ArrayList<String>();
                int keyIndex = 0, valueIndex = 0;
                String json = Aadhaar;
                for (int i = 0; i < json.length(); i++) {
                    String ch1 = String.valueOf(json.charAt(i));
                    if (ch1.equals(" ")) {
                        for (int j = i + 1; j < json.length(); j++) {
                            String ch2 = String.valueOf(json.charAt(j));
                            if (ch2.equals("=")) {
                                Key.add(keyIndex++, json.substring(i + 1, j));
                                break;
                            }
                            if (ch2.equals(" ")) {
                                break;
                            }
                        }
                    }
                }
                for (int i = 0; i < json.length(); i++) {
                    String ch1 = String.valueOf(json.charAt(i));
                    if (ch1.equals("=")) {
                        for (int j = i + 2; j < json.length() - 1; j++) {
                            String ch2 = String.valueOf(json.charAt(j)) + String.valueOf(json.charAt(j + 1));
                            if (ch2.equals("\" ") || ch2.equals("\"/") || ch2.equals("\"?")) {
                                Value.add(valueIndex++, json.substring(i + 2, j));
                                break;
                            }
                        }
                    }
                }
                String Address = "";
                int addressExistsCount = 0;
                for (int i = 0; i < Key.size(); i++) {
                    if (Key.get(i).equals("name")) {
                        tv2.setText(Value.get(i));
                    } else if (Key.get(i).equals("gender")) {
                        tv3.setText(Value.get(i));
                    } else if (Key.get(i).equals("uid")) {
                        tv1.setText(Value.get(i));
                    }
                }
            }

            submit = (Button) findViewById(R.id.button);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String worker_name = tv2.getText().toString().trim();
                    String worker_id = tv1.getText().toString().trim();
                    Intent abc=new Intent(getApplicationContext(), MainActivity.class);
                    abc.putExtra("name",tv2.getText().toString());
                    startActivity(abc);
                    //String worker_area = editText_area.getText().toString().trim();
                    //String from_date = editText_from_date.getText().toString().trim();
                    //String to_date = editText_to_date.getText().toString().trim();
                    //String m = mob.getText().toString().trim();
                        }});
        }
    }
}
