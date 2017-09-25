package net.twobeone.remotehelper.ui;

import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.ChatMessage;
import net.twobeone.remotehelper.util.ChatMessageAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Administrator on 2017-09-18.
 */

public class ChatActivity extends BaseActivity {
    private ChatMessageAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private String txtInput;
    private StringBuffer txtAnswer;
    private HttpURLConnection myConnection;
    private URL url;
    private boolean side = false;
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = UUID.randomUUID().toString().replaceAll("-", "") + "/HELPER";//사용자 ID 랜덤

        txtInput = "";
        setContentView(R.layout.activity_chat);

        buttonSend = (Button) findViewById(R.id.buttonSend);

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatMessageAdapter(getApplicationContext(), R.layout.activity_chat_adapter);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                txtInput = chatText.getText().toString();
                sendChatMessage();
                sendMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        sendMessage();
    }

    private boolean sendChatMessage(){
        side = false;
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

    public void sendMessage() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(txtInput == ""){
                            url = new URL(Constants.HTTP_URI_CHAT + uid);

                            myConnection
                                    = (HttpURLConnection) url.openConnection();

                            myConnection.setReadTimeout(10000);
                            myConnection.setConnectTimeout(15000);
                            myConnection.setRequestMethod("POST");
                            myConnection.setRequestProperty("Content-Type", "charset=UTF-8");
                            myConnection.setDoInput(true);
                            myConnection.setDoOutput(true);

                            myConnection.connect();
                    }else {
                            url = new URL(Constants.HTTP_URI_CHAT+uid+"?message="+txtInput);

                            myConnection
                                    = (HttpURLConnection) url.openConnection();

                            myConnection.setReadTimeout(10000);
                            myConnection.setConnectTimeout(15000);
                            myConnection.setRequestMethod("PUT");
                            myConnection.setRequestProperty("Content-Type", "charset=UTF-8");
                            myConnection.setDoInput(true);
                            myConnection.setDoOutput(true);

                            myConnection.connect();
                    }

                    if (myConnection.getResponseCode() == 200) {

                        BufferedReader in=new BufferedReader(new
                                InputStreamReader(
                                myConnection.getInputStream()));

                        txtAnswer = new StringBuffer("");
                        String line="";

                        while((line = in.readLine()) != null) {
                            txtAnswer.append(line);
                            break;
                        }

                        in.close();
                        myConnection.disconnect();
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    side = true;
                                    chatArrayAdapter.add(new ChatMessage(side, txtAnswer.toString()));
                                }
                            });
                        }
                    }).start();

                }catch (Exception ex){
                    Log.e("SSSSS", ex.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();
    }
}