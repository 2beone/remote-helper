package net.twobeone.remotehelper.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.model.ChatMessage;
import net.twobeone.remotehelper.model.ChatUser;
import net.twobeone.remotehelper.rest.ChatAPI;
import net.twobeone.remotehelper.util.ChatMessageAdapter;
import net.twobeone.remotehelper.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private static final String BOT_ID = "HELPER";
    private static final String BOT_NAME = "안전도우미";

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
        mMessageAdapter = new ChatMessageAdapter(this, mChatUser = new ChatUser(UUID.randomUUID().toString().replaceAll("-", "")), mItems);
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
                    send(mChatUser.getUserId(), BOT_ID, txtInput);
                }
            }
        });
        join(mChatUser.getUserId(), BOT_ID, null);
    }

    private void join(String uid, String botId, String message) {
        ChatAPI.retrofit.create(ChatAPI.class).join(uid, botId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                addItem(new ChatMessage(new ChatUser(BOT_NAME), response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void send(String uid, String botId, String message) {
        ChatAPI.retrofit.create(ChatAPI.class).send(uid, botId, message).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                addItem(new ChatMessage(new ChatUser(BOT_NAME), response.body()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void addItem(ChatMessage item) {
        mItems.add(item);
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecyclerView.scrollToPosition(mItems.size() - 1);
    }
}