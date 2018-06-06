package com.example.quinnm.socialmap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.ContentValues.TAG;


public class AddFriendFragment extends DialogFragment implements
        View.OnClickListener{
    private EditText _friendName;
    private Button _addButton, _cancelButton;
    private static final String TAG = "AddFriendFragment";


    public interface AddFriendDialogListener {
        void OnAddFriend(String friendName);
    }

    public AddFriendFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddFriendFragment newInstance(String title){
        AddFriendFragment frag = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.setCancelable(false);
        return frag;

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

//        get field from view

        _friendName = view.findViewById(R.id.id_friendName);
        _addButton = view.findViewById(R.id._addButton);
        _cancelButton = view.findViewById(R.id._cancelButton);

        _addButton.setOnClickListener(this);

        _cancelButton.setOnClickListener(
                (View v) -> dismiss()
        );

//        Show soft keyboard automatically and request focus to field.
        _friendName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onClick(View v) {
    String friendUsername = _friendName.getText().toString();

    AddFriendDialogListener listner = (AddFriendDialogListener) getActivity();
    listner.OnAddFriend(friendUsername);
//        Log.d(TAG, "onClick: added friend:  " + friendUsername);
        dismiss();
//    todo, check if username is in database
    }

}

