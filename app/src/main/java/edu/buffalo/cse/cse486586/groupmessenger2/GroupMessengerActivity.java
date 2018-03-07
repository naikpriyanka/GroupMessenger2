package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
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
import java.util.PriorityQueue;

import edu.buffalo.cse.cse486586.groupmessenger2.model.Message;
import edu.buffalo.cse.cse486586.groupmessenger2.model.MessageSequencer;
import edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType;

import static edu.buffalo.cse.cse486586.groupmessenger2.model.Message.DELIMITER;
import static edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType.MESSAGE;
import static edu.buffalo.cse.cse486586.groupmessenger2.model.MessageType.PROPOSED;
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

    static int failedAVD = 0;
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

    MessageSequencer messageSequencer = new MessageSequencer();

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
                        String[] msgPacket = msgReceived.split(DELIMITER);
                        MessageType msgType = getEnumBy(msgPacket[0]);
                        int msgID;
                        String sender;
                        switch (msgType) {
                            case MESSAGE:
                                System.out.println("In messages");
                                String msg = msgPacket[1];
                                msgID = Integer.parseInt(msgPacket[2]);
                                sender = msgPacket[3];
                                String receiver = msgPacket[4];
                                System.out.println("Message : " + msgReceived);
                                proposedSeq = Math.max(proposedSeq, agreedSeq) + 1;
                                System.out.println("Proposed Sequence number " + proposedSeq);
                                //Create an output data stream to send propose message
                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                /*
                                 * Create a propose message to be sent to the sender
                                 * Send the message with a new proposed sequence number
                                 */
                                Message proposedMsg = new Message(PROPOSED, msgID, proposedSeq, receiver);
                                //Write the message on the output stream
                                out.writeUTF(proposedMsg.toString());
                                break;
                        }
                    } else {
                        Log.e(TAG, "The server socket is null");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error accepting socket" + e);
                }
            }
        }

    }

    private class ClientTask extends AsyncTask<String, Void, Void> {

        PriorityQueue<Message> proposedQueue = new PriorityQueue<>(10, messageSequencer);

        @Override
        protected Void doInBackground(String... msgs) {
            String msgID = Integer.toString(++msgSeq) + msgs[1];
            //Iterate over the client ports to create socket for each client and send messages
            for (int i = 0; i < clientPorts.size(); i++) {
                try {
                    //Get the remote port for the client
                    String remotePort = clientPorts.get(i);
                    //Create client socket with that remote port number
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(remotePort));
                    //Get message
                    String msg = msgs[0];
                    //Get the sender of the message
                    String sender = msgs[1];
                    //Create message object with MESSAGE type
                    Message msgToSend = new Message(MESSAGE, msg, Integer.parseInt(msgID), sender, remotePort);
                    //Create an output data stream
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //Write message on the output stream
                    out.writeUTF(msgToSend.toString());
                    //Flush the output stream
                    out.flush();

                    //Get the propose message
                    try {
                        //Create an input data stream which will read from the same socket
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        //Read the response from the same socket
                        String responseMsg = in.readUTF();
                        System.out.println("Proposed Message " + responseMsg);
                        //Split the message using the delimiter
                        String[] proposedMsg = responseMsg.split(DELIMITER);
                        /*
                         * Create a message having proposed sequence number and the proposed sender
                         */
                        Message proposedMessage = new Message();
                        proposedMessage.setSeqNum(Integer.parseInt(proposedMsg[2]));
                        proposedMessage.setSender(proposedMsg[3]);
                        //Add the above message in the queue as a proposed message
                        proposedQueue.add(proposedMessage);
                        displayProposedMsg();
                    } catch (IOException e) {
                        /*
                         * If the reading from the socket fails the code will throw an exception
                         * If the client does not send any acknowledgement message it means that
                         * the client has stopped responding or working
                         */
                        Log.v("Failed AVD port no : ", remotePort);
                        //Update the failed AVD variable with the port number
                        failedAVD = Integer.parseInt(remotePort);
                    }
                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException");
                }
            }
            return null;
        }

        //Display Proposed messages
        private void displayProposedMsg() {
            for(Message m : proposedQueue) {
                System.out.println("Proposed Message in Queue" + m.toString());
            }
        }

    }
}