package com.example.quinnm.socialmap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.DeleteMessage;
import com.example.quinnm.socialmap.api.service.MessageClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageListRecyclerViewAdapter extends RecyclerView.Adapter<MessageListRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MessageListRecyclerViewAdapter";

    private String _username;
    private List<Map<String, Object>> _messages;
    private Context _context;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        Button deleteMessage;
        RelativeLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageList_message);
            deleteMessage = itemView.findViewById(R.id.messageList_deleteMessageButton);
            parentLayout = itemView.findViewById(R.id.messageList_parent_layout);
        }
    }

    MessageListRecyclerViewAdapter(List<Map<String, Object>> messages, String username, Context context) {
        this._messages = messages;
        this._username = username;
        this._context = context;
    }


    private void onDeleteMessageSuccess(@NonNull ViewHolder holder) {
        ((ApplicationStore) _context.getApplicationContext()).decrementNumberOfMessages();
        _messages.remove(holder.getAdapterPosition());
        notifyItemRemoved(holder.getAdapterPosition());
    }

    private void onDeleteMessageFailure() {
        // do something
    }

    private void deleteMessage(@NonNull ViewHolder holder) {
        Toast.makeText(_context.getApplicationContext(), "Trying to delete!", Toast.LENGTH_SHORT).show();

        String targetMessage = _messages.get(holder.getAdapterPosition()).get("message_id").toString();

        DeleteMessage deleteMessage = new DeleteMessage(
                targetMessage
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(_context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        MessageClient client = retrofit.create(MessageClient.class);
        Call<DeleteMessage> call = client.deleteMessage(deleteMessage);

        call.enqueue(new Callback<DeleteMessage>() {
            @Override
            public void onResponse(@NonNull Call<DeleteMessage> call, @NonNull Response<DeleteMessage> response) {
                if (response.body() != null && response.body().getErrorMsg().equals("")) {
                    Toast.makeText(_context.getApplicationContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                    onDeleteMessageSuccess(holder);
                }
                else {
                    Toast.makeText(_context.getApplicationContext(), "ERROR: " + response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                    onDeleteMessageFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteMessage> call, @NonNull Throwable t) {
                Toast.makeText(_context.getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                onDeleteMessageFailure();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_message_list, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.message.setText(_messages.get(position).get("msg_body").toString());
        holder.deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _messages.size();
    }
}
