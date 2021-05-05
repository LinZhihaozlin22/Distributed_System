package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();

    static final int SERVER_PORT = 10000;
    static int number;
    static Uri mUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        try {    //PA1
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        final EditText editText = (EditText) findViewById(R.id.editText1);
        final Button SendBtn =(Button) findViewById(R.id.button4);

        SendBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String msg = editText.getText().toString() + "\n";
                editText.setText(""); // This is one way to reset the input box.
                TextView localTextView = (TextView) findViewById(R.id.textView1);
                localTextView.append("\t" + msg); // This is one way to display a string.
                TextView remoteTextView = (TextView) findViewById(R.id.textView1);
                remoteTextView.append("\n");

                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];


            try{
                while(true) {
                    ContentValues contentValues = new ContentValues();
                    InputStream in = serverSocket.accept().getInputStream();
                    InputStreamReader inp = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(inp);
                    String x = br.readLine();

                    mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");
                    contentValues.put("key",Integer.toString(number));
                    contentValues.put("value",x);
                    getContentResolver().insert(mUri,contentValues);
                    number++;

                    publishProgress(x);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        private Uri buildUri(String scheme, String authority) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            uriBuilder.scheme(scheme);
            return uriBuilder.build();
        }

        protected void onProgressUpdate(String...strings) { //From PA1
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.textView1);
            remoteTextView.append(strReceived + "\t\n");
            TextView localTextView = (TextView) findViewById(R.id.textView1);
            localTextView.append("\n");
            return;
        }
    }

    //From PA1
    private class ClientTask extends AsyncTask<String, Void, Void> {
        String [] ports = {"11108", "11112", "11116", "11120", "11124" };
        @Override
        protected Void doInBackground(String... msgs) {
            try {
                for (String x : ports) {

                    String remotePort = x;

                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));

                    String msgToSend = msgs[0];

                    OutputStreamWriter wr = new OutputStreamWriter(socket.getOutputStream());
                    BufferedWriter bwr = new BufferedWriter(wr);
                    bwr.write(msgToSend);
                    bwr.flush();
                    socket.close();
                }
                } catch(UnknownHostException e){
                    Log.e(TAG, "ClientTask UnknownHostException");
                } catch(IOException e){
                    Log.e(TAG, "ClientTask socket IOException");
                }
                return null;
            }
        }
    }


