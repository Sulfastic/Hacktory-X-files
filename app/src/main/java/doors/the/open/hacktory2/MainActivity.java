package doors.the.open.hacktory2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private String scanId;
    private EditText editText;
    OkHttpClient client;
    Request getRequest;
    Response response;
    private Button button;


//    // Should be invoked in #onStart.
//
//
//    // Should be invoked in #onStop.
//    beaconManager.stopNearableDiscovery(scanId);
//
//    // When no longer needed. Should be invoked in #onDestroy.
//    beaconManager.disconnect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button  = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    getOperationHttp();
                }catch (IOException e){}
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        editText = (EditText) findViewById(R.id.editText);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Context context = getApplicationContext();
        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(context, "epempeyo-gmail-com-s-your--abm", "3a82149c5827ab3ec99d94f6ef3f9d12");
// Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager = new BeaconManager(context);

        // Should be invoked in #onCreate.
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {
//                Log.d("costam", "Discovered nearables: " + nearables);
                for (int i = 0; i < nearables.size(); i++) {
                    if (nearables.get(i).identifier.equals("d14e34d01b2d866c")) {
                        Log.d("costam", "Rssi: " + nearables.get(i).rssi);
                        if (nearables.get(i).rssi > -90) {
                            try{
                                getOperationHttp();
                                break;
                            }catch (IOException e){}
                        }
                }
            }
        }
    });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback()

                              {
                                  @Override
                                  public void onServiceReady() {
                                      scanId = beaconManager.startNearableDiscovery();
                                  }
                              }
        );

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

    void getOperationHttp() throws IOException
    {
        client = new OkHttpClient();
        getRequest = new Request.Builder()
                .url("http://aa3d84fb.ngrok.io/63abf6d43b8f84414c7df1f330c998b22cf0d6c3c8dabe64cc8a4c575d96b8fd")
                .build();

        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());
            }
        });
//        return response.body().string();
    }
}
