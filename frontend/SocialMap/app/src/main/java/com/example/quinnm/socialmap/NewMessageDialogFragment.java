package com.example.quinnm.socialmap;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

// ...


/**
 * Prompts user to create a new message.
 * Contains a dialogue title, a message body, a create button, a cancel button.
 * Communicates to MainActivity
 *
 * @author Keir Armstrong
 * @since May 27, 2018
 *
 * REFERENCES:
 *  DialogFragment Tutorial By CodePath Guides
 *      https://guides.codepath.com/android/using-dialogfragment
 */

public class NewMessageDialogFragment extends DialogFragment {
    private EditText _messageText;
    private Button _createButton, _cancelButton;

    public NewMessageDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewMessageDialogFragment newInstance(String title) {
        NewMessageDialogFragment frag = new NewMessageDialogFragment();
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
        return inflater.inflate(R.layout.fragment_add_message, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dark);
        // Get field from view

        _messageText = view.findViewById(R.id.input_message);
        _createButton = view.findViewById(R.id.btn_create);
        _cancelButton = view.findViewById(R.id.btn_cancel);

        _createButton.setOnClickListener(
                (View v) -> {
                    //do stuff
                }
        );

        _cancelButton.setOnClickListener(
                (View v) -> dismiss()
        );

       // Show soft keyboard automatically and request focus to field
        _messageText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
