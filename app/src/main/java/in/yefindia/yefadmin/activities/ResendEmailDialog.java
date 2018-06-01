package in.yefindia.yefadmin.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import in.yefindia.yefadmin.R;

public class ResendEmailDialog extends DialogFragment {

    private final String TAG = "ResendEmailDialog";
    Context mContext;
    EditText etEmail, etPassword;
    TextView textCancel, textConfirm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_resend_email_dialog, container, false);
        mContext = getActivity();
        etEmail = view.findViewById(R.id.et_dialog_email);
        etPassword = view.findViewById(R.id.et_dialog_password);
        textCancel = view.findViewById(R.id.text_dialog_cancel);
        textConfirm = view.findViewById(R.id.text_dialog_confirm);
        textConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isEmpty(etEmail.getText().toString()) & !Utils.isEmpty(etPassword.getText().toString())) {
                    if (Utils.checkDomain(etEmail.getText().toString())) {
                        if (Utils.checkPasswordLength(etPassword.getText().toString())) {

                            resendEmailRequest(etEmail.getText().toString(), etPassword.getText().toString());

                        } else {
                            showToast(mContext, "Invalid password");
                        }
                    } else {
                        showToast(mContext, "Invalid email");
                    }

                } else {
                    showToast(mContext, "Please fill all the details");
                }
            }
        });
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    private void resendEmailRequest(String email, String password) {
        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null){
                                if(user.isEmailVerified()){
                                    Toast.makeText(getActivity(),"The email has been verified already.Please log in",Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    getDialog().dismiss();
                                }else {
                                    resendVerificationEmail();
                                    FirebaseAuth.getInstance().signOut();
                                    getDialog().dismiss();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure "+e.getMessage());
                        showToast(mContext, e.getMessage());
                        getDialog().dismiss();
                    }
                })
        ;
    }

    private void resendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast(mContext, "Verification mail has been sent.");
                            } else {
                                showToast(mContext, "Failed to send verification mail. Please try again");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to resend verification mail  " + e.getMessage());
                        }
                    });
        }
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
