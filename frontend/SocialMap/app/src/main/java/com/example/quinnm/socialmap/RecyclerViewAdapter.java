package com.example.quinnm.socialmap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.quinnm.socialmap.R;
import com.example.quinnm.socialmap.ViewFriendsActivity;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mFriendNames = new ArrayList<>(1);
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mFriendNames, Context mContext) {
        this.mFriendNames = mFriendNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listfriends,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.friendName.setText(mFriendNames.get(position));
        holder.deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + mFriendNames.get(position));
                if (mContext instanceof ViewFriendsActivity){
//                  todo implement delete friends method
//                    (ViewFriendsActivity)mContext.deleteFriends(mFriendNames.get(position));
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return mFriendNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView friendName;
        Button deleteFriend;

        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            deleteFriend = itemView.findViewById(R.id.deleteButton);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}