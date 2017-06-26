package sis.pewpew.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sis.pewpew.R;
import sis.pewpew.providers.AuthActivity;

public class NetIntegrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Login";
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean dialogShown;

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
                    Intent intent = new Intent(NetIntegrationActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (user != null && dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {

                    final long points = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();

                    if (points >= 1500) {
                        AlertDialog.Builder demoFinishDialog = new AlertDialog.Builder(NetIntegrationActivity.this);
                        demoFinishDialog.setTitle("Демоверсия подошла к концу");
                        demoFinishDialog.setMessage("Поздравляем, Вы заработали свои первые 1500 очков, используя деморежим, " +
                                "и познакомились с проектом Enliven. Однако чтобы продолжить зарабатывать очки и поднимать свое звание, " +
                                "необходимо войти с помощью аккаунта Google, после чего Вы сможете уже по-настоящему начать помогать нашей планете. Не волнуйтесь, " +
                                "все Ваши заработанные очки будут бережно перенесены в Ваш новый аккаунт. Имейте ввиду, что если " +
                                "Вы войдете в аккаунт, на котором уже есть очки, после переноса они будут заменены.");
                        demoFinishDialog.setIcon(R.drawable.ic_demo_finished);
                        demoFinishDialog.setCancelable(false);
                        demoFinishDialog.setPositiveButton("Начать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sp = getSharedPreferences("POINTS_FOR_UPGRADE", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putLong("pointsForUpgrade", points);
                                editor.apply();
                                logOut();
                            }
                        });
                        demoFinishDialog.setNegativeButton("Не переносить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sp2 = getSharedPreferences("POINTS_FOR_UPGRADE_TRANSFER", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp2.edit();
                                editor.putBoolean("noTransfer", true);
                                editor.apply();
                                logOut();
                            }
                        });
                        if (!dialogShown && user.isAnonymous()) {
                            demoFinishDialog.show();
                            dialogShown = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);
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

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(NetIntegrationActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
