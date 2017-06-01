package sis.pewpew.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sis.pewpew.R;

public class MarkerInfoActivity extends AppCompatActivity {

    private String title;
    private String snippet;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String detailsFromDatabase;
    private String addressFromDatabase;
    private String workTimeFromDatabase;
    private String workTimeBreakFromDatabase;
    private String contactsPhoneFromDatabase;
    private String contactsEmailFromDatabase;
    private String contactsUrlFromDatabase;
    private String prizeFundFromDatabase;
    private long timesUsedFromDatabase;
    private String iconUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
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
            snippet = extras.getString("SNIPPET");
        }

        setTitle(title);

        final ImageView markerInfoIcon = (ImageView) findViewById(R.id.marker_info_icon);

        mDatabase.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.child("markers").child(snippet).child("iconUrl").getValue() != null) {
                        iconUrl = dataSnapshot.child("markers").child(snippet).child("iconUrl").getValue().toString();
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("details").getValue() != null) {
                        detailsFromDatabase = dataSnapshot.child("markers").child(snippet).child("details").getValue().toString();
                    } else {
                        detailsFromDatabase = "Без описания";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("address").getValue() != null) {
                        addressFromDatabase = dataSnapshot.child("markers").child(snippet).child("address").getValue().toString();
                    } else {
                        addressFromDatabase = "Адрес не указан";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("workTime").getValue() != null) {
                        workTimeFromDatabase = dataSnapshot.child("markers").child(snippet).child("workTime").getValue().toString();
                    } else {
                        workTimeFromDatabase = "Круглосуточно";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("workTimeBreak").getValue() != null) {
                        workTimeBreakFromDatabase = dataSnapshot.child("markers").child(snippet).child("workTimeBreak").getValue().toString();
                    } else {
                        workTimeBreakFromDatabase = "Без перерывов";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("contactsPhone").getValue() != null) {
                        contactsPhoneFromDatabase = dataSnapshot.child("markers").child(snippet).child("contactsPhone").getValue().toString();
                    } else {
                        contactsPhoneFromDatabase = "Номер телефона не указан";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("contactsEmail").getValue() != null) {
                        contactsEmailFromDatabase = dataSnapshot.child("markers").child(snippet).child("contactsEmail").getValue().toString();
                    } else {
                        contactsEmailFromDatabase = "Электронная почта не указана";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("contactsUrl").getValue() != null) {
                        contactsUrlFromDatabase = dataSnapshot.child("markers").child(snippet).child("contactsUrl").getValue().toString();
                    } else {
                        contactsUrlFromDatabase = "Сайт не указан";
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("timesUsed").getValue() != null) {
                        timesUsedFromDatabase = (long) dataSnapshot.child("markers").child(snippet).child("timesUsed").getValue();
                    } else {
                        timesUsedFromDatabase = 0;
                    }
                    if (dataSnapshot.child("markers").child(snippet).child("prizeFund").getValue() != null) {
                        prizeFundFromDatabase = dataSnapshot.child("markers").child(snippet).child("contactsUrl").getValue().toString();
                    } else if (snippet.contains("ev")) {
                        prizeFundFromDatabase = "1000";
                    } else {
                        prizeFundFromDatabase = "200";
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                try {
                    Glide.with(MarkerInfoActivity.this)
                            .load(iconUrl)
                            .into(markerInfoIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((TextView) findViewById(R.id.marker_info_details)).setText(detailsFromDatabase);
                ((TextView) findViewById(R.id.marker_info_address)).setText(addressFromDatabase);
                ((TextView) findViewById(R.id.marker_info_work_time)).setText(workTimeFromDatabase);
                ((TextView) findViewById(R.id.marker_info_work_time_break)).setText(workTimeBreakFromDatabase);
                ((TextView) findViewById(R.id.marker_info_contacts_phone)).setText(contactsPhoneFromDatabase);
                ((TextView) findViewById(R.id.marker_info_contacts_email)).setText(contactsEmailFromDatabase);
                ((TextView) findViewById(R.id.marker_info_contacts_url)).setText(contactsUrlFromDatabase);
                ((TextView) findViewById(R.id.marker_info_prize_fund)).setText(prizeFundFromDatabase + " очков");
                ((TextView) findViewById(R.id.marker_info_used)).setText("" + timesUsedFromDatabase);

                if (snippet.contains("ev")) {
                    ((TextView) findViewById(R.id.marker_info_work_time_call)).setText("Дата");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Не удалось загрузить информацию", Toast.LENGTH_SHORT).show();
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MarkerInfoActivity.this);
                builder.setTitle("Жалоба: " + title + ", " + snippet);
                builder.setIcon(R.drawable.ic_report_icon);
                builder.setMessage("Вы можете сообщить нам, если описание не соответствует экопункту " +
                        "или если этот пункт закрыт. Не отправляйте жалобы, не касающиеся собранной здесь информации.");
                final EditText input = new EditText(MarkerInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText() != null) {
                            mDatabase.child("markers").child(snippet).child("reports").child(user.getUid())
                                    .setValue(input.getText().toString());
                            showGratitudeDialog();
                        }
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        final android.app.AlertDialog.Builder markerInfoDetailsCardDialog = new android.app.AlertDialog.Builder(this);
        markerInfoDetailsCardDialog.setTitle("Карточка информции");
        markerInfoDetailsCardDialog.setIcon(R.drawable.marker_info_details_icon);
        markerInfoDetailsCardDialog.setMessage("Здесь мы собрали всю известную нам информацию об этом месте. " +
                "Если Вы считаете, что мы в чем-то ошиблись, пожалуйста, сообщите нам об этом, " +
                "используя кнопку жалобы в правом верхрем углу экрана информации.");
        markerInfoDetailsCardDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final android.app.AlertDialog.Builder markerInfoAddressCardDialog = new android.app.AlertDialog.Builder(this);
        markerInfoAddressCardDialog.setTitle("Карточка адреса");
        markerInfoAddressCardDialog.setIcon(R.drawable.marker_info_address_icon);
        markerInfoAddressCardDialog.setMessage("Чтобы Вам не пришлось искать это место вручную, " +
                "мы добавили особое меню в правом нижнем углу экрана с картой. " +
                "Просто коснитесь флажка экопункта. для отображения кнопок \"Проложить маршрут\" и " +
                "\"Показать на карте\". Коснувшись любой из них, " +
                "Вы будете перенаправлены в приложение \"Google Maps\" для дальнейшей навигации.");
        markerInfoAddressCardDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final android.app.AlertDialog.Builder markerInfoWorkTimeCardDialog = new android.app.AlertDialog.Builder(this);
        markerInfoWorkTimeCardDialog.setTitle("Карточка времени работы и дат проведения");
        markerInfoWorkTimeCardDialog.setIcon(R.drawable.marker_info_work_time_icon);
        markerInfoWorkTimeCardDialog.setMessage("Для экопунктов здесь показано время работы на протяжении недели, " +
                "а для экособытий – даты проведения. " +
                "Кроме того, здесь отображен график перерывов.");
        markerInfoWorkTimeCardDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final android.app.AlertDialog.Builder markerInfoContactsCardDialog = new android.app.AlertDialog.Builder(this);
        markerInfoContactsCardDialog.setTitle("Карточка контактов");
        markerInfoContactsCardDialog.setIcon(R.drawable.marker_info_contacts_icon);
        markerInfoContactsCardDialog.setMessage("Мы опросили работников экопунктов " +
                "и собрали для Вас всю имеющуюся у них контактную информацию. К сожалению, " +
                "далеко не все пункты имеют контактный телефон или собственный сайт, " +
                "однако даже у самых отдаленных пунктов нам удалось получить хотя бы один способ связи.");
        markerInfoContactsCardDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final android.app.AlertDialog.Builder markerInfoPrizeFundCardDialog = new android.app.AlertDialog.Builder(this);
        markerInfoPrizeFundCardDialog.setTitle("Карточка награды");
        markerInfoPrizeFundCardDialog.setIcon(R.drawable.marker_info_prize_fund_icon);
        markerInfoPrizeFundCardDialog.setMessage("У каждого экопункта есть свой призовой фонд. " +
                "Просто доберитесь до него, используйте и получите заслуженные очки в приложении. " +
                "Спасать планету еще никогда не было так просто!");
        markerInfoPrizeFundCardDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        CardView markerInfoDetailsCard = (CardView) findViewById(R.id.marker_info_details_card);
        CardView markerInfoAddressCard = (CardView) findViewById(R.id.marker_info_address_card);
        CardView markerInfoWorkTimeCard = (CardView) findViewById(R.id.marker_info_work_time_card);
        CardView markerInfoContactsCard = (CardView) findViewById(R.id.marker_info_contacts_card);
        CardView markerInfoPrizeFundCard = (CardView) findViewById(R.id.marker_info_prize_fund_card);

        markerInfoDetailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoDetailsCardDialog.show();
            }
        });

        markerInfoAddressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoAddressCardDialog.show();
            }
        });

        markerInfoWorkTimeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoWorkTimeCardDialog.show();
            }
        });

        markerInfoContactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoContactsCardDialog.show();
            }
        });

        markerInfoPrizeFundCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoPrizeFundCardDialog.show();
            }
        });
    }

    private void showGratitudeDialog() {
        android.app.AlertDialog.Builder gratitudeDialog = new android.app.AlertDialog.Builder(this);
        gratitudeDialog.setTitle("Спасибо");
        gratitudeDialog.setMessage("Благодаря Вам наше приложение становится лучше. Мы рассмотрим Вашу жалобу и примем необходимые меры.");
        gratitudeDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        gratitudeDialog.show();
    }
}
