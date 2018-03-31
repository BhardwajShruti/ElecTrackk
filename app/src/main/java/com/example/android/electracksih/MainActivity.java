package com.example.android.electracksih;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerView2;
    RecyclerView.Adapter mAdapter;
    RecyclerView.Adapter mAdapter2;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.LayoutManager mLayoutManager2;
    ChatBotFragment chatBotFragment;

    private int[] mDevice_ID = {1,2,3,4,5,6};
    private final int REQ_CODE_SPEECH_INPUT = 100;

    Firebase mRef,IOTRef;
    ArrayList<SensorData> sensorDataList=new ArrayList<>();
    ArrayList<OneDevice> deviceList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ImageView iv = (ImageView) findViewById(R.id.dotview);
//        Blurry.with(MainActivity.this)
//                .radius(10)
//                .sampling(8)
//                .color(Color.argb(66, 255, 255, 0))
//                .async()
//                .animate(500)
//                .onto(iv);

        mRecyclerView = (RecyclerView) findViewById(R.id.device_recycler_view);
        mRecyclerView2 = (RecyclerView) findViewById(R.id.temp_humid_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView2.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter2 = new MyAdapter2();
        mRecyclerView2.setAdapter(mAdapter2);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Chat ");

// Set up the input
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestToApiAI(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

    }


    void requestToApiAI(String query) {
        Log.d("TAG", "requestToApiAI: query:" + query);
        final AIConfiguration config = new AIConfiguration("1d4e9a40fc3841efab890f0bfc7cbe44", AIConfiguration.SupportedLanguages.English, AIConfiguration.RecognitionEngine.System);
        final AIDataService aiDataService = new AIDataService(config);
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    final ai.api.model.Status status = response.getStatus();

                    return response;
                } catch (AIServiceException e) {
                    Log.e("TAG", "doInBackground: " + e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    Log.d("TAG", "onPostExecute: success");
                    final Result result = aiResponse.getResult();

                    String intentName = result.getMetadata().getIntentName();
                    Log.d("TAG", intentName.toString());
                    String urlString = "http://172.28.25.147:5000";
                    String mid;
                    if(intentName.toString().equals("Bulb On")|| intentName.toString().equals("Fan On")) {
                        mid = "lightturnedon";
                    }else {
                        mid = "lightturnedoff";
                    }
                    int position = 0;
                    int stat = 0;
                    if (intentName.toString().equals("Bulb On")) {
                        position = 0;
                        stat = 1;
                    }
                    if (intentName.toString().equals("Fan On"))  {
                        position = 1;
                        stat = 1;
                    }
                    if (intentName.toString().equals("Bulb Off"))  {
                        position = 0;
                        stat = 0;
                    }

                    if (intentName.toString().equals("Fan Off"))  {
                        position = 1;
                        stat = 0;
                    }
                    switch (mDevice_ID[position]) {
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
                    myTask myTask = new myTask(MainActivity.this, urlString, new myTask.onSpecificStateChangeListener() {
                        @Override
                        public void onStateChanged(String string) {
                            Toast.makeText(MainActivity.this, result.getResolvedQuery(), Toast.LENGTH_SHORT).show();
                        }

                    });
                    myTask.execute(urlString);

                }
            }
        }.execute(aiRequest);

    }
    private void loadDataFromFirebase() {
        mRef = new Firebase("https://not-so-awesome-project-45a2e.firebaseio.com/sensors/");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot oneDataSnapshot:dataSnapshot.getChildren()){
                    DataSnapshot oneDataSnapshot_data=oneDataSnapshot.child("data");
                    String value=oneDataSnapshot_data.getValue(String.class);
                    String values1[]=value.split(",");
                    int sizeOfValues=values1.length;
                    String value2=values1[sizeOfValues-1];
                    String values2[]=value2.split("\"");

                    DataSnapshot oneDataSnapshot_time=oneDataSnapshot.child("time");
                    Long timeOfReading=oneDataSnapshot_time.getValue(Long.class);
                    Log.d("haha",sizeOfValues+"");
                    SensorData sensorData=new SensorData(values1[0],values1[1],values1[2],values1[3],values1[4],values2[0],"1",timeOfReading);
                    Log.d("data",oneDataSnapshot.toString());
                    Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
                    sensorDataList.add(sensorData);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        IOTRef=new Firebase("https://not-so-awesome-project-45a2e.firebaseio.com/IOTdata/");
        IOTRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot oneDeviceData:dataSnapshot.getChildren()){
                    DataSnapshot applianceIDSnapshot=oneDeviceData.child("applianceid");

//                    OneDevice oneDevice=oneDeviceData.getValue(OneDevice.class);
//                    deviceList.add(oneDevice);
                    Log.d("device",oneDeviceData.toString());
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, "failed2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Not recognized",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    requestToApiAI(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
