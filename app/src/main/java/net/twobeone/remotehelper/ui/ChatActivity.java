package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.ChatMessage;
import net.twobeone.remotehelper.util.ChatMessageAdapter;

/**
 * Created by Administrator on 2017-09-18.
 */

public class ChatActivity extends BaseActivity {
    private ChatMessageAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;

    Intent intent;
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_chat);

        buttonSend = (Button) findViewById(R.id.buttonSend);

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatMessageAdapter(getApplicationContext(), R.layout.activity_chat_adapter);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
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
                sendChatMessage();
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
    }

    private boolean sendChatMessage(){
        side = false;
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        recvChatMessage();
        return true;
    }

    private boolean recvChatMessage(){
        side = true;
        chatArrayAdapter.add(new ChatMessage(side, "TEST중입니다."));
        return true;
    }
}
