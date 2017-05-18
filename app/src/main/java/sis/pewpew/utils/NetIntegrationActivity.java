package sis.pewpew.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sis.pewpew.R;
import sis.pewpew.connections.GoogleAuthActivity;

public class NetIntegrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Login";
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    String username = user.getDisplayName();
                    String email = user.getEmail();

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    if (user.getDisplayName() != null) {
                        TextView navUserName = (TextView) headerView.findViewById(R.id.user_display_name);
                        navUserName.setText(username);
                    } else {
                        TextView navUserName = (TextView) headerView.findViewById(R.id.user_display_name);
                        navUserName.setText("Имя пользователя");
                    }
                    if (user.getEmail() != null) {
                        TextView navUserEmail = (TextView) headerView.findViewById(R.id.user_email);
                        navUserEmail.setText(email);
                    } else {
                        TextView navUserEmail = (TextView) headerView.findViewById(R.id.user_email);
                        navUserEmail.setText("Адрес электронной почты");
                    }

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Intent intent = new Intent(NetIntegrationActivity.this, GoogleAuthActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
