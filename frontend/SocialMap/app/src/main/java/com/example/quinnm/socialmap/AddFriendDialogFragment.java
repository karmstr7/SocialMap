package com.example.quinnm.socialmap;


import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quinnm.socialmap.api.model.AddFriend;
import com.example.quinnm.socialmap.api.service.FriendsListClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFriendDialogFragment extends DialogFragment implements
        View.OnClickListener {
    private static final String TAG = "AddFriendDialogFragment";

    private String _username;
    private EditText _friendName;
    private Button _addButton, _cancelButton;
    private List<String> _friends;

    public interface AddFriendDialogListener{
        void OnAddFriend(String friendName);
    }

    public AddFriendDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddFriendDialogFragment newInstance(String title) {
        AddFriendDialogFragment frag = new AddFriendDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.setCancelable(false);  // prevent back and outside-area click from closing dialog
        return frag;
    }

    private void checkFriendExistence(String friend) {
        AddFriend newFriend = new AddFriend(
                _username,
                friend
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        FriendsListClient client = retrofit.create(FriendsListClient.class);
        Call<AddFriend> call = client.addFriend(newFriend);

        call.enqueue(new Callback<AddFriend>() {
            @Override
            public void onResponse(@NonNull Call<AddFriend> call, @NonNull Response<AddFriend> response) {
                if (response.body() != null && response.body().getErrorMsg().equals("")) {
                    Toast.makeText(getActivity().getBaseContext(),
                            "Friend Added",Toast.LENGTH_SHORT).show();
                    onAddFriendSuccess();
                }
                else {
                    Toast.makeText(getActivity().getBaseContext(),
                            "ERROR: " + response.body().getErrorMsg(),
                            Toast.LENGTH_SHORT).show();
                    onAddFriendFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddFriend> call, @NonNull Throwable t) {
                Toast.makeText(getActivity().getBaseContext(),
                        t.toString(), Toast.LENGTH_SHORT).show();
                onAddFriendFailure();
            }
        });
    }

    private void onAddFriendSuccess() {
        AddFriendDialogListener listener = (AddFriendDialogListener) getActivity();
        listener.OnAddFriend(_friendName.getText().toString());

        _friendName.setText("");
        _addButton.setEnabled(true);
        _cancelButton.setEnabled(true);
        dismiss();
    }

    private void onAddFriendFailure() {
        _addButton.setEnabled(true);
        _cancelButton.setEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _username = ((ApplicationStore) this.getActivity().getApplication()).getUsername();
        _friends = ((ApplicationStore) this.getActivity().getApplication()).getFriends();

        _friendName = view.findViewById(R.id.id_friendName);
        _addButton = view.findViewById(R.id._addButton);
        _cancelButton = view.findViewById(R.id._cancelButton);

        _addButton.setOnClickListener(this);
        _cancelButton.setOnClickListener(
                (View v) -> dismiss()
        );

//      Show soft keyboard automatically and request focus to field.
        _friendName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onClick(View v) {
        _addButton.setEnabled(false);
        _cancelButton.setEnabled(false);

        String friendName = _friendName.getText().toString();

        int listSize = _friends.size();
        boolean alreadyFriends = false;
        boolean addingSelf = false;

        for (int i = 0; i < listSize; i++) {
            if (_friends.get(i).equals(friendName)) {
                alreadyFriends = true;
                break;
            }
        }

        if (friendName.equals(_username)) {
            addingSelf = true;
        }

        if (friendName.length() < 3) {
            // invalid username length
            Toast.makeText(getActivity().getBaseContext(), "Invalid username length", Toast.LENGTH_SHORT).show();
            _addButton.setEnabled(true);
            _cancelButton.setEnabled(true);

            return;
        }

        if (addingSelf) {
            // prevent user from adding self as friend
            _addButton.setEnabled(true);
            _cancelButton.setEnabled(true);
            return;
        }

        if (alreadyFriends) {
            // prevent from adding existing friends
            _addButton.setEnabled(true);
            _cancelButton.setEnabled(true);
            return;
        }

        checkFriendExistence(friendName);
    }
}