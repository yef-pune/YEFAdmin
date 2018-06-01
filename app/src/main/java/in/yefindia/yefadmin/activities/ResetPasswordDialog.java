package in.yefindia.yefadmin.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import in.yefindia.yefadmin.R;

public class ResetPasswordDialog extends DialogFragment {

    private final String TAG = "ResetPasswordDialog";
    Context context;
    EditText editEmail;
    Button buttonCancel, buttonSend;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reset_password, container, false);
        context = getActivity();
        editEmail = view.findViewById(R.id.edit_dialog_email1);
        buttonCancel = view.findViewById(R.id.button_dialog_cancel);
        buttonSend = view.findViewById(R.id.button_dialog_send);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmpty(editEmail.getText().toString())) {
                    if (Utils.checkDomain(editEmail.getText().toString())) {
                        sendPasswordResetLink(editEmail.getText().toString());
                    } else {
                        showToast(context, "Invalid email");
                    }
                } else {
                    showToast(context, "Enter email address");
                }
            }
        });
        return view;
    }

    private void sendPasswordResetLink(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset link sent");
                            showToast(context, "Password reset link sent");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        getDialog().dismiss();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
