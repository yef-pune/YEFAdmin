package in.yefindia.yefadmin.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.yefindia.yefadmin.R;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextInputLayout editTextEmail,editTextPassword;
    private TextView textResendMail,textforgotPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail=findViewById(R.id.editText_emailSignIn);
        editTextPassword=findViewById(R.id.editText_passwordSignIn);
        textResendMail=findViewById(R.id.textView_gotoResendverificationlink);
        textforgotPassword=findViewById(R.id.textView_forgotPassword);
        progressDialog=new ProgressDialog(SignInActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing in...");
        clearFields();
        setupupFirebseAuth();
        textforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordDialog dialog = new ResetPasswordDialog();
                dialog.show(getSupportFragmentManager(), "dialog_resend_email_verification");

            }
        });

    }


    public void gotoSignUp(View view) {
        startActivity(new Intent(SignInActivity.this,SignUpActivity.class));

    }

    public void sendVerificationMail(View view) {
        ResendEmailDialog dialog = new ResendEmailDialog();
        dialog.show(getSupportFragmentManager(), "dialog_resend_email_verification");
    }

    public void attemptLogin(View view) {
        //check if fields are filled out
        progressDialog.show();
        if(!Utils.isEmpty(editTextEmail.getEditText().getText().toString()) && !Utils.isEmpty(editTextPassword.getEditText().getText().toString())){
            if(Utils.checkDomain(editTextEmail.getEditText().getText().toString())){
                if(Utils.checkPasswordLength(editTextPassword.getEditText().getText().toString())){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmail.getEditText().getText().toString(),
                            editTextPassword.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.cancel();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignInActivity.this,"Invalid email or password", Toast.LENGTH_LONG).show();
                                    progressDialog.cancel();
                                }
                            });
                }else{
                    dismissProgressDialog();
                    editTextPassword.getEditText().setError(getString(R.string.shortPassword));
                }

            }else{
                dismissProgressDialog();
                editTextEmail.getEditText().setError(getString(R.string.invalidEmail));
            }
        }else {
            dismissProgressDialog();
            Toast.makeText(SignInActivity.this, R.string.emptyForm,Toast.LENGTH_SHORT).show();
        }
    }

    //Firebase Data------------------------------

    private void setupupFirebseAuth(){
        Log.d(TAG,"setUpFirebaseAuth : started");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    if(user.isEmailVerified()){
                        Log.d(TAG,"OnAuthStateChanged:signed_in:  "+user.getUid());
                        startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                        finish();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                        builder.setCancelable(true)
                                .setTitle(R.string.dialogResendEmail)
//                                .setIcon(R.drawable.ic_done_black_32dp)
                                .setMessage(R.string.dialogMsgResendEmail)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                        FirebaseAuth.getInstance().signOut();
                    }

                }else{
                    //user is signed out
                    Log.d(TAG,"OnAuthStateChanged:signed_out:");
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void clearFields(){
        if(editTextEmail.getEditText().getText().toString()!=null){
            editTextEmail.getEditText().setText("");
        }

        if(editTextPassword.getEditText().getText().toString()!=null){
            editTextPassword.getEditText().setText("");
        }
    }

    private void dismissProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

}
