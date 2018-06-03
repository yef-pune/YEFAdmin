package in.yefindia.yefadmin.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.yefindia.yefadmin.R;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout mFullName, mEmail, mContactNumber, mState, mCity, mPassword, mConfirmPassword;
    private Button signupButton;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViews();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidationAndAttempRegisteration(mFullName.getEditText().getText().toString(),
                        mContactNumber.getEditText().getText().toString(),
                        mState.getEditText().getText().toString(),
                        mCity.getEditText().getText().toString(),
                        mEmail.getEditText().getText().toString(),
                        mPassword.getEditText().getText().toString(),
                        mConfirmPassword.getEditText().getText().toString()
                );
            }
        });
    }

    private void initializeViews() {
        mFullName = findViewById(R.id.editText_fullName);
        mEmail = findViewById(R.id.editText_emailSignUp);
        mContactNumber = findViewById(R.id.editText_phoneNumber);
        mState = findViewById(R.id.editText_state);
        mCity = findViewById(R.id.editText_city);
        mPassword = findViewById(R.id.editText_passwordSignUp);
        mConfirmPassword = findViewById(R.id.editText_confirmPassword);
        signupButton = findViewById(R.id.button_signUp);
    }

    //Method to check weather all validations are correct
    private void checkValidationAndAttempRegisteration(final String fullName, final String contactNumber, final String state, final String city, final String email, final String password, final String conPass) {
        if (!Utils.isEmpty(fullName) && !Utils.isEmpty(contactNumber) && !Utils.isEmpty(state) && !Utils.isEmpty(city) && !Utils.isEmpty(email) && !Utils.isEmpty(email) && !Utils.isEmpty(conPass)) {
            if (Utils.doPasswordsMatch(password, conPass)) {
                if (Utils.checkDomain(email)) {
                    if (Utils.checkPasswordLength(password)) {
                        if (Utils.checkContctNumberLength(contactNumber)) {
                            register(fullName, contactNumber, state, city, email, password);
                        } else {
                            mContactNumber.getEditText().setError(getString(R.string.invalidContactNumber));
                        }
                    } else {
                        mPassword.getEditText().setError(getString(R.string.shortPassword));
                    }
                } else {
                    mEmail.getEditText().setError(getString(R.string.invalidEmail));
                }
            } else {
                mConfirmPassword.getEditText().setError(getString(R.string.passwordNotMatched));
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.emptyForm), Toast.LENGTH_SHORT).show();
        }
    }


    //Method to add user
    private void register(final String fullName, final String contactNumber, final String state, final String city, final String email, final String password) {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerificationMail();
                    addUserNameToDb(fullName, contactNumber, state, city, email);
                    FirebaseAuth.getInstance().signOut();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.cancel();
            }

        });
    }

    //Method to send verification mail
    private void sendVerificationMail() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (firebaseUser != null) {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setCancelable(false)
                                .setTitle(Utils.REGISTERATION_SUCESSFUL)
//                                .setIcon(R.drawable.ic_done_black_32dp)
                                .setMessage(Utils.REGISTERATION_SUCESSFUL_MESSAGE_1 + " " + mEmail.getEditText().getText().toString() +
                                        System.getProperty("line.separator") + Utils.REGISTERATION_SUCESSFUL_MESSAGE_2)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        builder.show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.somethingWrong, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    //Method to add user details to Firebase Database
    public void addUserNameToDb(final String fullName, final String contactNumber, final String state, final String city, final String email) {

        AdminDetailsModel mUser = new AdminDetailsModel(fullName, contactNumber, state, city, email);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Utils.FIREBASE_ADMINS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(mUser);
        FirebaseAuth.getInstance().signOut();
    }

}
