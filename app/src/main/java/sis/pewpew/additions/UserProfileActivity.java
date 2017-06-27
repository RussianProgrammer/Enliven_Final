package sis.pewpew.additions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import sis.pewpew.R;

public class UserProfileActivity extends AppCompatActivity {

    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public long pointsFromDatabase;
    public long timesUsedFromDatabase;
    private String statusFromDatabase;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String uId;
    //private String uPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setTitle("Загрузка имени…");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uId = extras.getString("UID");
            //uPosition = extras.getString("POSITION");
        }

        mDatabase.keepSynced(true);

        ValueEventListener pointsListener = new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView userIcon = (ImageView) findViewById(R.id.rating_profile_user_icon);
                //TextView position = (TextView) findViewById(R.id.rating_profile_place);
                TextView points = (TextView) findViewById(R.id.rating_profile_points);
                TextView used = (TextView) findViewById(R.id.rating_profile_used);
                TextView status = (TextView) findViewById(R.id.rating_profile_status);
                TextView rank = (TextView) findViewById(R.id.rating_profile_rank);
                TextView achieves = (TextView) findViewById(R.id.rating_profile_achieves);
                TextView savedTrees = (TextView) findViewById(R.id.rating_profile_saved_trees);
                TextView savedAnimals = (TextView) findViewById(R.id.rating_profile_saved_animals);
                TextView savedPeople = (TextView) findViewById(R.id.rating_profile_saved_people);

                setTitle(dataSnapshot.child("users").child(uId).child("name").getValue().toString());

                //position.setText("#" + uPosition);

                if (user != null && dataSnapshot.child("users").child(uId).child("points").getValue() != null) {
                    pointsFromDatabase = (long) dataSnapshot.child("users").child(uId).child("points").getValue();

                    points.setText("" + pointsFromDatabase);
                    savedTrees.setText("" + (int) pointsFromDatabase / 500);
                    savedAnimals.setText("" + (int) pointsFromDatabase / 1000);
                    savedPeople.setText("" + (int) pointsFromDatabase / 1200);

                    if (pointsFromDatabase < 100) {
                        achieves.setText("0");
                    } else if (pointsFromDatabase >= 100 && pointsFromDatabase < 600) {
                        achieves.setText("1");
                    } else if (pointsFromDatabase >= 600 && pointsFromDatabase < 1100) {
                        achieves.setText("2");
                    } else if (pointsFromDatabase >= 1100 && pointsFromDatabase < 1600) {
                        achieves.setText("3");
                    } else if (pointsFromDatabase >= 1600 && pointsFromDatabase < 2100) {
                        achieves.setText("4");
                    } else if (pointsFromDatabase >= 2100 && pointsFromDatabase < 2600) {
                        achieves.setText("5");
                    } else if (pointsFromDatabase >= 2600 && pointsFromDatabase < 3100) {
                        achieves.setText("6");
                    } else if (pointsFromDatabase >= 3100 && pointsFromDatabase < 3600) {
                        achieves.setText("7");
                    } else if (pointsFromDatabase >= 3600 && pointsFromDatabase < 4100) {
                        achieves.setText("8");
                    } else if (pointsFromDatabase >= 4100 && pointsFromDatabase < 4600) {
                        achieves.setText("9");
                    } else if (pointsFromDatabase >= 4600 && pointsFromDatabase < 5100) {
                        achieves.setText("10");
                    } else if (pointsFromDatabase >= 5100 && pointsFromDatabase < 5600) {
                        achieves.setText("11");
                    } else if (pointsFromDatabase >= 5600 && pointsFromDatabase < 6100) {
                        achieves.setText("12");
                    } else if (pointsFromDatabase >= 6100 && pointsFromDatabase < 6600) {
                        achieves.setText("13");
                    } else if (pointsFromDatabase >= 6600 && pointsFromDatabase < 7100) {
                        achieves.setText("14");
                    } else {
                        achieves.setText("15");
                    }

                    if ((int) pointsFromDatabase < 500) {
                        rank.setText("Новичок");
                    } else if (pointsFromDatabase >= 500 && pointsFromDatabase < 1000) {
                        rank.setText("Начинающий");
                    } else if (pointsFromDatabase >= 1000 && pointsFromDatabase < 2000) {
                        rank.setText("Опытный");
                    } else if (pointsFromDatabase >= 2000 && pointsFromDatabase < 3500) {
                        rank.setText("Защитник флоры");
                    } else if (pointsFromDatabase >= 3500 && pointsFromDatabase < 5500) {
                        rank.setText("Защитник фауны");
                    } else if (pointsFromDatabase >= 5500 && pointsFromDatabase < 8000) {
                        rank.setText("Защитник людей");
                    } else if (pointsFromDatabase >= 8000 && pointsFromDatabase < 11000) {
                        rank.setText("Защитник Земли");
                    } else if (pointsFromDatabase >= 11000 && pointsFromDatabase < 14500) {
                        rank.setText("Герой");
                    } else {
                        rank.setText("Легенда");
                    }
                } else {

                    points.setText("" + 0);
                    savedTrees.setText("" + 0);
                    savedAnimals.setText("" + 0);
                    savedPeople.setText("" + 0);
                    achieves.setText("" + 0);
                    rank.setText("Нет ранга");
                }

                if (user != null && dataSnapshot.child("users").child(uId).child("status").getValue() != null) {
                    statusFromDatabase = dataSnapshot.child("users").child(uId).child("status").getValue().toString();

                    switch (statusFromDatabase) {
                        case "1":
                            status.setText("Сотрудник");
                            userIcon.setImageResource(R.drawable.employee_user_icon);
                            break;
                        case "2":
                            status.setText("Организатор");
                            userIcon.setImageResource(R.drawable.organizer_user_icon);
                            break;
                        case "3":
                            status.setText("Модератор");
                            userIcon.setImageResource(R.drawable.moderator_user_icon);
                            break;
                        case "4":
                            status.setText("Администратор");
                            userIcon.setImageResource(R.drawable.administrator_user_icon);
                            break;
                        case "5":
                            status.setText("Создатель");
                            userIcon.setImageResource(R.drawable.ceo_user_icon);
                            break;
                    }
                } else {
                    status.setText("Пользователь");
                    userIcon.setImageResource(R.drawable.dummy_user_icon);
                }
                if (dataSnapshot.child("users").child(uId).child("timesUsed").getValue() != null) {
                    timesUsedFromDatabase = (long) dataSnapshot.child("users").child(uId).child("timesUsed").getValue();
                    used.setText("" + timesUsedFromDatabase);
                } else {
                    used.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(pointsListener);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;

    }
}