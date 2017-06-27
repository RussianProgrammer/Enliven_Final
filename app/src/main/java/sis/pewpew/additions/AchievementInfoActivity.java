package sis.pewpew.additions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sis.pewpew.R;

public class AchievementInfoActivity extends AppCompatActivity {

    private String title;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String color1;
    private String color2;
    private String color3;
    private long allUsers;
    private long gotAchieve;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("TITLE");
            color1 = extras.getString("COLOR1");
            color2 = extras.getString("COLOR2");
            color3 = extras.getString("COLOR3");
            id = extras.getInt("ID");
        }

        setTitle(title);

        List<String> detailsList = new ArrayList<>();
        detailsList.add(getString(R.string.achieve_1_details));
        detailsList.add(getString(R.string.achieve_2_details));
        detailsList.add(getString(R.string.achieve_3_details));
        detailsList.add(getString(R.string.achieve_4_details));
        detailsList.add(getString(R.string.achieve_5_details));
        detailsList.add(getString(R.string.achieve_6_details));
        detailsList.add(getString(R.string.achieve_7_details));
        detailsList.add(getString(R.string.achieve_8_details));
        detailsList.add(getString(R.string.achieve_9_details));
        detailsList.add(getString(R.string.achieve_10_details));
        detailsList.add(getString(R.string.achieve_11_details));
        detailsList.add(getString(R.string.achieve_12_details));
        detailsList.add(getString(R.string.achieve_13_details));
        detailsList.add(getString(R.string.achieve_14_details));
        detailsList.add(getString(R.string.achieve_15_details));


        ImageView icon = (ImageView) findViewById(R.id.achieve_info_icon);

        CardView dateCard = (CardView) findViewById(R.id.achieve_info_date_card);
        CardView gottenCard = (CardView) findViewById(R.id.achieve_info_gotten_card);
        CardView detailsCard = (CardView) findViewById(R.id.achieve_info_details_card);

        final TextView dateFromCard = (TextView) findViewById(R.id.achieve_info_date);
        final TextView dateCallFromCard = (TextView) findViewById(R.id.achieve_info_date_call);
        final TextView gottenFromCard = (TextView) findViewById(R.id.achieve_info_gotten);
        final TextView gottenCallFromCard = (TextView) findViewById(R.id.achieve_info_gotten_call);
        final TextView detailsFromCard = (TextView) findViewById(R.id.achieve_info_details);
        final TextView detailsCallFromCard = (TextView) findViewById(R.id.achieve_info_details_call);

        if (color1.equals("#D7E5ED")) {
            dateFromCard.setTextColor(Color.parseColor("#000000"));
            dateCallFromCard.setTextColor(Color.parseColor("#000000"));
        }
        if (color2.equals("#D7E5ED")) {
            gottenFromCard.setTextColor(Color.parseColor("#000000"));
            gottenCallFromCard.setTextColor(Color.parseColor("#000000"));
        }
        if (color3.equals("#D7E5ED")) {
            detailsFromCard.setTextColor(Color.parseColor("#000000"));
            detailsCallFromCard.setTextColor(Color.parseColor("#000000"));
        }

        dateCard.setCardBackgroundColor(Color.parseColor(color1));
        gottenCard.setCardBackgroundColor(Color.parseColor(color2));
        detailsCard.setCardBackgroundColor(Color.parseColor(color3));

        mDatabase.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allUsers = dataSnapshot.child("users").getChildrenCount();
                if (dataSnapshot.child("achieves").child("" + id) != null) {
                    gotAchieve = dataSnapshot.child("achieves").child("" + id).getChildrenCount();
                } else {
                    gotAchieve = 0;
                }
                gottenFromCard.setText(gotAchieve * 100 / allUsers + "%");

                if (!user.isAnonymous()) {
                    if (dataSnapshot.child("users").child(user.getUid())
                            .child("achievements").child("" + id).getValue() != null) {
                        dateFromCard.setText(dataSnapshot.child("users").child(user.getUid())
                                .child("achievements").child("" + id).getValue().toString());
                    } else {
                        dateFromCard.setText("Когда-то");
                    }
                } else {
                    if (dataSnapshot.child("demos").child(user.getUid())
                            .child("achievements").child("" + id).getValue() != null) {
                        dateFromCard.setText(dataSnapshot.child("demos").child(user.getUid())
                                .child("achievements").child("" + id).getValue().toString());
                    } else {
                        dateFromCard.setText("Когда-то");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);


        switch (id) {
            case (1):
                icon.setImageResource(R.drawable.achieve_1_image);
                detailsFromCard.setText(detailsList.get(0));
                break;
            case (2):
                icon.setImageResource(R.drawable.achieve_2_image);
                detailsFromCard.setText(detailsList.get(1));
                break;
            case (3):
                icon.setImageResource(R.drawable.achieve_3_image);
                detailsFromCard.setText(detailsList.get(2));
                break;
            case (4):
                icon.setImageResource(R.drawable.achieve_4_image);
                detailsFromCard.setText(detailsList.get(3));
                break;
            case (5):
                icon.setImageResource(R.drawable.achieve_5_image);
                detailsFromCard.setText(detailsList.get(4));
                break;
            case (6):
                icon.setImageResource(R.drawable.achieve_6_image);
                detailsFromCard.setText(detailsList.get(5));
                break;
            case (7):
                icon.setImageResource(R.drawable.achieve_7_image);
                detailsFromCard.setText(detailsList.get(6));
                break;
            case (8):
                icon.setImageResource(R.drawable.achieve_8_image);
                detailsFromCard.setText(detailsList.get(7));
                break;
            case (9):
                icon.setImageResource(R.drawable.achieve_9_image);
                detailsFromCard.setText(detailsList.get(8));
                break;
            case (10):
                icon.setImageResource(R.drawable.achieve_10_image);
                detailsFromCard.setText(detailsList.get(9));
                break;
            case (11):
                icon.setImageResource(R.drawable.achieve_11_image);
                detailsFromCard.setText(detailsList.get(10));
                break;
            case (12):
                icon.setImageResource(R.drawable.achieve_12_image);
                detailsFromCard.setText(detailsList.get(11));
                break;
            case (13):
                icon.setImageResource(R.drawable.achieve_13_image);
                detailsFromCard.setText(detailsList.get(12));
                break;
            case (14):
                icon.setImageResource(R.drawable.achieve_14_image);
                detailsFromCard.setText(detailsList.get(13));
                break;
            case (15):
                icon.setImageResource(R.drawable.achieve_15_image);
                detailsFromCard.setText(detailsList.get(14));
                break;
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.google.com/#q=" + title);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
