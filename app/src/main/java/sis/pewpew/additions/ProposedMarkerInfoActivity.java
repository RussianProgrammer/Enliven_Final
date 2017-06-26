package sis.pewpew.additions;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import sis.pewpew.R;
import sis.pewpew.classes.MarkerDataUpload;

public class ProposedMarkerInfoActivity extends AppCompatActivity {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private String id;
    private String details;
    private String title;
    private String contactsPhone;
    private String contactsEmail;
    private String address;
    private String contactsUrl;
    private String workTime;
    private String workTimeBreak;
    private String group;
    private String iconUrl;
    private String finalGroupEng;
    private String finalGroup;
    private double latitude;
    private double longitude;

    private ImageView markerImage;
    private TextView markerLatitude;
    private TextView markerLongitude;
    private TextView markerDetails;
    private TextView markerAddress;
    private TextView markerContactsPhone;
    private TextView markerContactsEmail;
    private TextView markerContactsUrl;
    private TextView markerWorkTime;
    private TextView markerGroup;
    private TextView markerWorkTimeBreak;
    private Button acceptMarkerButton;
    private CardView coordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposed_marker_info);

        markerImage = (ImageView) findViewById(R.id.proposed_marker_image_icon);
        markerLatitude = (TextView) findViewById(R.id.proposed_marker_latitude);
        markerLongitude = (TextView) findViewById(R.id.proposed_marker_longitude);
        markerDetails = (TextView) findViewById(R.id.proposed_marker_details);
        markerGroup = (TextView) findViewById(R.id.proposed_marker_group);
        markerAddress = (TextView) findViewById(R.id.proposed_marker_address);
        markerContactsPhone = (TextView) findViewById(R.id.proposed_marker_contacts_phone);
        markerContactsEmail = (TextView) findViewById(R.id.proposed_marker_contacts_email);
        markerContactsUrl = (TextView) findViewById(R.id.proposed_marker_contacts_url);
        markerWorkTime = (TextView) findViewById(R.id.proposed_marker_work_time);
        markerWorkTimeBreak = (TextView) findViewById(R.id.proposed_marker_work_time_break);
        acceptMarkerButton = (Button) findViewById(R.id.accept_marker_button);
        coordinates = (CardView) findViewById(R.id.proposed_marker_coordinates_card);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("ID");
        }

        setTitle("Данные слота " + id);

        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                address = dataSnapshot.child("requests").child("" + id).child("address").getValue().toString();
                details = dataSnapshot.child("requests").child("" + id).child("details").getValue().toString();
                title = dataSnapshot.child("requests").child("" + id).child("title").getValue().toString();
                iconUrl = dataSnapshot.child("requests").child("" + id).child("iconUrl").getValue().toString();
                contactsPhone = dataSnapshot.child("requests").child("" + id).child("contactsPhone").getValue().toString();
                contactsEmail = dataSnapshot.child("requests").child("" + id).child("contactsEmail").getValue().toString();
                contactsUrl = dataSnapshot.child("requests").child("" + id).child("contactsUrl").getValue().toString();
                if (dataSnapshot.child("requests").child("" + id).child("workTime").getValue() != null) {
                    workTime = dataSnapshot.child("requests").child("" + id).child("workTime").getValue().toString();
                } else {
                    workTime = "Круглосуточно";
                }
                if (dataSnapshot.child("requests").child("" + id).child("workTimeBreak").getValue() != null) {
                    workTimeBreak = dataSnapshot.child("requests").child("" + id).child("workTimeBreak").getValue().toString();
                } else {
                    workTimeBreak = "Без перерывов";
                }
                latitude = (double) dataSnapshot.child("requests").child("" + id).child("lat").getValue();
                longitude = (double) dataSnapshot.child("requests").child("" + id).child("lng").getValue();

                coordinates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + latitude
                                + ">,<" + longitude + " >?q=<" + latitude + ">,<" + longitude + ">"));
                        startActivity(intent);
                    }
                });

                group = dataSnapshot.child("requests").child("" + id).child("id").getValue().toString();

                switch (group) {
                    case "bat":
                        finalGroup = "Батареи";
                        finalGroupEng = "battery";
                        break;
                    case "ppr":
                        finalGroup = "Бумага";
                        finalGroupEng = "paper";
                        break;
                    case "gls":
                        finalGroup = "Стекло";
                        finalGroupEng = "glass";
                        break;
                    case "mtl":
                        finalGroup = "Металл";
                        finalGroupEng = "metal";
                        break;
                    case "blb":
                        finalGroup = "Лампы";
                        finalGroupEng = "bulb";
                        break;
                    case "dng":
                        finalGroup = "Опасные отходы";
                        finalGroupEng = "danger";
                        break;
                    case "oth":
                        finalGroup = "Другое";
                        finalGroupEng = "other";
                        break;
                    case "evt":
                        finalGroup = "События";
                        finalGroupEng = "event";
                        break;
                }

                markerLatitude.setText("" + latitude);
                markerLongitude.setText("" + longitude);
                markerDetails.setText(details);
                markerGroup.setText(finalGroup);
                markerAddress.setText(address);
                markerContactsPhone.setText(contactsPhone);
                markerContactsEmail.setText(contactsEmail);
                markerContactsUrl.setText(contactsUrl);
                markerWorkTime.setText(workTime);
                markerWorkTimeBreak.setText(workTimeBreak);

                try {
                    Glide.with(ProposedMarkerInfoActivity.this)
                            .load(iconUrl)
                            .into(markerImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                acceptMarkerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder acceptDialog = new AlertDialog.Builder(ProposedMarkerInfoActivity.this);
                        acceptDialog.setTitle("Подтверждение");
                        acceptDialog.setMessage("Нажав \"Одобрить\", Вы подтверждаете, что данный маркер полностью соответствует " +
                                "настоящему и пока не добавленному на Карту экопункту; а также то, что все предоставленные " +
                                "данные не содержат неприемлимых материалов. Сразу после Вашего одобрения маркер будет добавлен на карту.");
                        acceptDialog.setPositiveButton("Одобрить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final AlertDialog.Builder accept2Dialog = new AlertDialog.Builder(ProposedMarkerInfoActivity.this);
                                accept2Dialog.setTitle("Уникальный идентификатор");
                                accept2Dialog.setMessage("Введите уникальный идентификатор маркера");
                                InputFilter[] filterArray = new InputFilter[1];
                                filterArray[0] = new InputFilter.LengthFilter(5);
                                final EditText input = new EditText(ProposedMarkerInfoActivity.this);
                                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                input.setFilters(filterArray);
                                accept2Dialog.setView(input);
                                accept2Dialog.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (dataSnapshot.child("markers").child(group + input.getText().toString()).getChildrenCount() == 0) {
                                            final MarkerDataUpload markerDataUpload = new MarkerDataUpload(group + input.getText().toString(), finalGroupEng, title, address,
                                                    iconUrl, workTime, workTimeBreak, details, contactsPhone, contactsEmail, contactsUrl, latitude, longitude);
                                            mDatabase.child("markers").child(group + input.getText().toString()).setValue(markerDataUpload)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            showAdditionalDialog("Экопункт опубликован", "Экопункт успешно добавлен на Карту.", 2);
                                                            DatabaseReference mTimesSentProgress = FirebaseDatabase.getInstance().getReference()
                                                                    .child("users").child(user.getUid()).child("timesAccepted");
                                                            onTimesAcceptedCount(mTimesSentProgress);
                                                            mDatabase.child("markers").child(group + input.getText().toString()).child("timesUsed").setValue(0);
                                                            mDatabase.child("requests").child(id).removeValue();
                                                        }
                                                    });
                                        } else {
                                            showAdditionalDialog("Ошибка", "Экопункт с таким идентификатором уже существует. Попробуйте выбрать другой.", 1);
                                        }
                                    }
                                });
                                accept2Dialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                accept2Dialog.show();
                            }
                        });
                        acceptDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        acceptDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);
    }

    private void showAdditionalDialog(String title, String message, int i) {
        String keyWord = "Закрыть";
        switch (i) {
            case 1:
                keyWord = "Изменить";
                break;
            case 2:
                keyWord = "Готово";
        }
        AlertDialog.Builder additionalDialog = new AlertDialog.Builder(ProposedMarkerInfoActivity.this);
        additionalDialog.setTitle(title);
        additionalDialog.setMessage(message);

        additionalDialog.setNegativeButton(keyWord, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 1:
                        dialogInterface.cancel();
                        break;
                    case 2:
                        dialogInterface.cancel();
                        finish();
                }
            }
        });
        additionalDialog.show();
    }

    private void onTimesAcceptedCount(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long timesUsed = 0;
                if (mutableData != null) {
                    timesUsed = (long) mutableData.getValue();
                }
                timesUsed = timesUsed + 1;
                assert mutableData != null;
                mutableData.setValue(timesUsed);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("TAG", "postTransaction:onComplete:" + databaseError);
            }
        });
    }
}
