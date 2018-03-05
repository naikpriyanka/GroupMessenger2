package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.cse486586.groupmessenger2.model.Message;
import edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType;

import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.BASE_CONTENT_URI;
import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.KEY_FIELD;
import static edu.buffalo.cse.cse486586.groupmessenger2.data.GroupMessengerContract.GroupMessengerEntry.VALUE_FIELD;
import static edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType.MESSAGE;
import static edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType.getEnumBy;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;

    static int keyCount = 0;
    static int msgSeq = 0;
    static int agreedSeq = 0;
    static int proposedSeq = 0;

    static final List<String> clientPorts = new ArrayList() {{
        add(REMOTE_PORT0);
        add(REMOTE_PORT1);
        add(REMOTE_PORT2);
        add(REMOTE_PORT3);
        add(REMOTE_PORT4);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        //Calculate the port number that this AVD listens on
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             * ServerSocket is a socket which servers can use to listen and accept requests from clients
             * AsyncTask is a simplified thread construct that Android provides.
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * COMPLETED: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * COMPLETED: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        /*
         * Retrieve a pointer to the input box (EditText) defined in the layout
         * XML file
         */
        final EditText editTextView = (EditText) findViewById(R.id.editText1);

        /*
         * Register an OnClickListener for the input box. OnClickListener is an event handler that
         * processes each click event. The purpose of the following code is to detect a click
         * and create a client thread so that the client thread can send the string
         * in the input box over the network.
         */
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * If the send button is clicked then we display the string. Then we create
                 * an AsyncTask that sends the string to the remote AVD.
                 */
                String msg = editTextView.getText().toString() + "\n";
                editTextView.setText("");
                tv.append("\t" + msg);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
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
            /*
             * an iterative server that can service multiple clients, though, one at a time.
             */
            while (true) {
                try {
                    if (serverSocket != null) {
                        Socket socket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                        String msgReceived = in.readUTF();
                        String[] msgPacket = msgReceived.split(":");
                        MessageType msgType = getEnumBy(msgPacket[0]);
                        switch (msgType) {
                            case MESSAGE:
                                handleMessage(msgReceived);
                                break;

                            case PROPOSED:
                                handleProposal(msgReceived);
                                break;

                            case AGREED:
                                handleAgreement(msgReceived);
                                break;
                        }
                        /*
                         * Added message to the content values with the key as keyCount
                         * These values will be processed by the content resolver to be inserted in the database
                         * Values are bind in content values against column name of the table.
                         */
                        ContentValues values = new ContentValues();
                        values.put(KEY_FIELD, keyCount);
                        values.put(VALUE_FIELD, msgReceived);
                        //This method inserts a new row into the provider and returns a content URI for that row.
                        getContentResolver().insert(BASE_CONTENT_URI, values);
                        //Increment key count with each message
                        keyCount++;
                    } else {
                        Log.e(TAG, "The server socket is null");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error accepting socket" + e);
                }
            }
        }

        private void handleMessage(String msgReceived) {

        }

        private void handleProposal(String msgReceived) {

        }

        private void handleAgreement(String msgReceived) {

        }

    }

    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            //Iterate over the client ports to create socket for each client and send messages
            for (int i = 0; i < clientPorts.size(); i++) {
                try {
                    //Get the remote port for the client
                    String remotePort = clientPorts.get(i);
                    //Create client socket with that remote port number
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePort));
                    socket.setSoTimeout(500); //To detect failure of node after 500ms
                    //Get message
                    String msgToSend = msgs[0];
                    //Create an output data stream
                    Message message = new Message(MESSAGE, remotePort, msgToSend, ++msgSeq);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //Write message on the output stream
                    out.writeUTF(message.toString());
                    //Flush the output stream
                    out.flush();
                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException" + e);
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException" + e);
                }
            }
            return null;
        }

    }
}