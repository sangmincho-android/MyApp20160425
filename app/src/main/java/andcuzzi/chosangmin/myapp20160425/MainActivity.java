package andcuzzi.chosangmin.myapp20160425;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private ProgressHandler handler;
    private ArrayList<Thread> listThread;
    private int increasing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btnStartThread = (Button) findViewById(R.id.btnStartThread);
        btnStartThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startThread(view);
            }
        });

        Button btnStopThread = (Button) findViewById(R.id.btnStopThread);
        btnStopThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopThread(view);
            }
        });

        Button btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear(view);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        handler = new ProgressHandler();
        listThread = new ArrayList<Thread>();
    }

    public void startThread(View view) {
        EditText editThreadTick  = (EditText)findViewById(R.id.numThreadTick);
        EditText editProgressInc = (EditText)findViewById(R.id.numProgressIncreasing);

        String strThreadTick  = editThreadTick.getText().toString();
        String strProgressInc = editProgressInc.getText().toString();

        if (strThreadTick.isEmpty()){
            this.makeSnackBar(view, "Thread Tick is empty");
            return;
        }

        if (strProgressInc.isEmpty()){
            this.makeSnackBar(view, "Progress Increasing is empty");
            return;
        }

        final int tick;

        try {
            tick = Integer.parseInt(strThreadTick);
            increasing  = Integer.parseInt(strProgressInc);
        }
        catch (Exception ex) {
            this.makeSnackBar(view, "Input Value is not number");
            return;
        }

        if (tick < 1 || tick > 100) {
            this.makeSnackBar(view, "Thread Tick is out of range(1~100)");
            return;
        }

        if (increasing < 1 || increasing > 10) {
            this.makeSnackBar(view, "Progress Increasing Length is out of range(1~10)");
            return;
        }

        for (int i = 1; i<=5; i++) {
            listThread.add(this.createThread(tick, i));
        }

        this.makeSnackBar(view, "START");
    }

    public void stopThread(View view) {
        if (listThread.isEmpty()) {
            this.makeSnackBar(view, "Thread is not running now");
            return;
        }

        for (int i = 0 ; i < listThread.size() ; i++) {
            listThread.get(i).interrupt();
        }

        listThread.clear();

        this.makeSnackBar(view, "STOP");
    }

    public void clear(View view) {
        if (!listThread.isEmpty()) {
            this.makeSnackBar(view, "Thread is running now");
            return;
        }

        for (int i = 1; i <= 5; i++) {
            int id = this.getResourceId("progressBar" + i, "id", getPackageName());
            ((ProgressBar)findViewById(id)).setProgress(0);
        }

        this.makeSnackBar(view, "CLEAR");
    }

    public Thread createThread(int tick, int num) {
        int id = this.getResourceId("progressBar" + num, "id", getPackageName());
        Object obj = findViewById(id);

        Thread th = new Thread(new OneShotTask(tick, obj));

        th.start();

        return th;
    }

    public class OneShotTask implements Runnable {
        private int tick;
        private Object obj;
        public boolean running = true;

        public OneShotTask(int tick, Object obj)
        {
            this.tick = tick;
            this.obj = obj;
        }

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(tick);

                    Message msg = handler.obtainMessage();

                    msg.obj = obj;

                    handler.sendMessage(msg);
                } catch (InterruptedException iex) {
                    break;
                } catch (Exception ex) {
                }
            }
        }
    }

    public class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ProgressBar bar = (ProgressBar)msg.obj;

            bar.incrementProgressBy(increasing);
        }
    }

    public void makeSnackBar(View view, String message) {
        if (view == null || message == null || message.isEmpty()){
            return;
        }

        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public int getResourceId(String pVariableName, String pResourcename, String pPackageName)
    {
        try {
            return getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://andcuzzi.chosangmin.myapp20160425/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://andcuzzi.chosangmin.myapp20160425/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
