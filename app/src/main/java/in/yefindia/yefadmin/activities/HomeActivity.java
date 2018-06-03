package in.yefindia.yefadmin.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.yefindia.yefadmin.R;
import in.yefindia.yefadmin.activities.fragment.AboutAppFragment;
import in.yefindia.yefadmin.activities.fragment.BloodDonationFragment;
import in.yefindia.yefadmin.activities.fragment.CareerGuidanceFragment;
import in.yefindia.yefadmin.activities.fragment.CounsellingFragment;
import in.yefindia.yefadmin.activities.fragment.HelplineFragment;
import in.yefindia.yefadmin.activities.fragment.HomeFragment;
import in.yefindia.yefadmin.activities.fragment.JobUpdateFragment;
import in.yefindia.yefadmin.activities.fragment.ScholarshipFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    FirebaseDatabase database;
    DatabaseReference ref;
    private TextView userName;
    private TextView userEmail;
    private ImageView verIcon;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupupFirebseAuth();
        setupNavigationView();
        getAndSetUserData();

        //Default Selected Fragment
        replaceFragment(R.id.nav_Home);
        navigationView.setCheckedItem(R.id.nav_Home);
    }

    private void getAndSetUserData() {
        userName=navigationView.getHeaderView(0).findViewById(R.id.userNameNavigationHeader);
        userEmail=navigationView.getHeaderView(0).findViewById(R.id.userEmailNavigationHeader);
        verIcon=navigationView.getHeaderView(0).findViewById(R.id.imageVerifiedUser);
        final FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null){
            if(currentUser.isEmailVerified()){
                database=FirebaseDatabase.getInstance();
                ref=database.getReference(Utils.FIREBASE_USERS_CHILD_NODE);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userName.setText(dataSnapshot.child(currentUser.getUid()).child("fullName").getValue(String.class));
                        userEmail.setText(dataSnapshot.child(currentUser.getUid()).child("email").getValue(String.class));
                        verIcon.setImageResource(R.drawable.ic_check_circle_white_11dp);

                      //  RegisterBloodDonationDialog.name=dataSnapshot.child(currentUser.getUid()).child("fullName").getValue(String.class);
                       // RegisterBloodDonationDialog.contact=dataSnapshot.child(currentUser.getUid()).child("contactNumber").getValue(String.class);
                      //  RegisterBloodDonationDialog.email=dataSnapshot.child(currentUser.getUid()).child("email").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
                    }
                });
            }

        }
    }


    private void setupNavigationView() {
        drawerLayout=findViewById(R.id.drawerHomeNavigation);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=findViewById(R.id.nav_view);
        actionBarDrawerToggle=
                new ActionBarDrawerToggle(HomeActivity.this,drawerLayout,R.string.openString,R.string.closeString);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                item.setChecked(true);
                drawerLayout.closeDrawers();
                replaceFragment(item.getItemId());
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    public void checkAuthenticationState(){
        Log.d(TAG,"checkAuthenticationState: checking authenticating state");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
        {
            Log.d(TAG,"checkAuthenticationState: user is null, navigating back to login screen.");
            Intent intent = new Intent(HomeActivity.this,SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG,"checkAuthenticationState: user is Authenticated");
        }
    }

    private void setupupFirebseAuth(){
        Log.d(TAG,"setUpFirebaseAuth : started");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    Log.d(TAG,"checkAuthenticationState: user is null, navigating back to login screen.");
                    Intent intent = new Intent(HomeActivity.this,SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    //user is signed out
                    Log.d(TAG,"OnAuthStateChenged:signed_out:");
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    private void replaceFragment(int id){

        Fragment fragment=null;

        switch (id){
            case R.id.nav_Home:
                fragment=new HomeFragment();
                break;
            case R.id.nav_careerGuidance:
                fragment=new CareerGuidanceFragment();
                break;
            case R.id.nav_jobUpdates:
                fragment=new JobUpdateFragment();
                break;
            case R.id.nav_scolarship:
                fragment=new ScholarshipFragment();
                break;
            case R.id.nav_helplineNumber:
                fragment=new HelplineFragment();
                break;
            case R.id.nav_Counselling:
                fragment=new CounsellingFragment();
                break;
            case R.id.nav_bloodDonation:
                fragment=new BloodDonationFragment();
                break;
            case R.id.nav_aboutApp:
                fragment=new AboutAppFragment();
                break;
        }

        if(fragment!=null){
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.replaceFragment,fragment);
            fragmentTransaction.commit();
        }
    }
}
