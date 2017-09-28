package net.twobeone.remotehelper.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.model.ChatMessage;
import net.twobeone.remotehelper.model.ChatUser;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ChatUser mChatUser;
    private List<ChatMessage> mMessageList;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatMessageAdapter(Context context, ChatUser chatUser, List<ChatMessage> messageList) {
        mContext = context;
        mChatUser = chatUser;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList == null ? 0 : mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = mMessageList.get(position);
        if (mChatUser.getUserId().equals(message.getSender().getUserId())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return new SentMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false));
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false));
        }
        throw new IllegalArgumentException("viewType is not valid");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = mMessageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            timeText.setText(DateUtils.formatSameDayTime(message.getCreatedAt()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            timeText.setText(DateUtils.formatSameDayTime(message.getCreatedAt()));
            nameText.setText(message.getSender().getUserId());
        }
    }
}
