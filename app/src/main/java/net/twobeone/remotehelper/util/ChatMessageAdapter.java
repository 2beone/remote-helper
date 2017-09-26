package net.twobeone.remotehelper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.R;

import java.util.ArrayList;
import java.util.List;



public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private TextView ID;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private LinearLayout singleMessageContainer;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatMessageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_adapter, parent, false);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userName = prefs.getString(Constants.PREF_USER_NAME, "이름없음");

        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        ID = (TextView) row.findViewById(R.id.ID);
        chatText.setText(chatMessageObj.message);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bg_chat : R.drawable.bg_chat);
        chatText.setBackgroundColor(chatMessageObj.left ? Color.GREEN : Color.CYAN);
        if(chatMessageObj.left){
            singleMessageContainer.setPadding(10,0,300,0);
            chatText.setPadding(10,0,0,0);
        } else {
            singleMessageContainer.setPadding(300,0,10,0);
            chatText.setPadding(0,0,10,0);
        }
        ID.setText(chatMessageObj.left ? "안전도우미" : userName);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        ID.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }
}
