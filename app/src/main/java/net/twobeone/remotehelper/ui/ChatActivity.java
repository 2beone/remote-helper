package net.twobeone.remotehelper.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.model.ChatMessage;
import net.twobeone.remotehelper.model.ChatUser;
import net.twobeone.remotehelper.util.ChatMessageAdapter;
import net.twobeone.remotehelper.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends BaseActivity {

    private final List<ChatMessage> mItems = new ArrayList<>();

    private ChatMessageAdapter mMessageAdapter;
    private RecyclerView mMessageRecyclerView;
    private ChatUser mChatUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final EditText editTextChatBox = (EditText) findViewById(R.id.edittext_chatbox);
        editTextChatBox.requestFocus();
        mMessageAdapter = new ChatMessageAdapter(this, mChatUser = new ChatUser(UUID.randomUUID().toString().replaceAll("-", "")), mItems); // R.layout.activity_chat_adapter);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.setAdapter(mMessageAdapter);

        Button buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String txtInput = editTextChatBox.getText().toString().trim();
                editTextChatBox.setText("");
                if (!StringUtils.isNullOrEmpty(txtInput)) {
                    addItem(new ChatMessage(mChatUser, txtInput));
                    sendMessageToServer(txtInput);
                }
            }
        });
        sendMessageToServer(null);
    }

    public void sendMessageToServer(final String sendMessage) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String receivedMessage = null;

                try {

                    HttpURLConnection myConnection = null;
                    if (StringUtils.isNullOrEmpty(sendMessage)) {
                        myConnection = (HttpURLConnection) new URL(Constants.HTTP_URI_CHAT + mChatUser.getUserId() + "/HELPER").openConnection();
                        myConnection.setRequestMethod("POST");
                    } else {
                        myConnection = (HttpURLConnection) new URL(Constants.HTTP_URI_CHAT + mChatUser.getUserId() + "/HELPER?message=" + sendMessage).openConnection();
                        myConnection.setRequestMethod("PUT");
                    }
                    myConnection.setReadTimeout(10000);
                    myConnection.setConnectTimeout(15000);
                    myConnection.setRequestProperty("Content-Type", "charset=UTF-8");
                    myConnection.setDoInput(true);
                    myConnection.setDoOutput(true);
                    myConnection.connect();

                    if (myConnection.getResponseCode() == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(myConnection.getInputStream()));
                        StringBuilder txtAnswer = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            txtAnswer.append(line);
                            break;
                        }
                        in.close();
                        myConnection.disconnect();
                        receivedMessage = txtAnswer.toString();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return receivedMessage;
            }

            @Override
            protected void onPostExecute(String message) {
                addItem(new ChatMessage(new ChatUser("안전도우미"), message));
            }

        }.execute();
    }

    private void addItem(ChatMessage item) {
        mItems.add(item);
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecyclerView.scrollToPosition(mItems.size() - 1);
    }
}