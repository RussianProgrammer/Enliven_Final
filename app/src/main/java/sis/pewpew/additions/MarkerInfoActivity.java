package sis.pewpew.additions;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sis.pewpew.R;

public class MarkerInfoActivity extends AppCompatActivity {

    private String title;
    private String snippet;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Locale locale = new Locale("ru");
    private String date = new SimpleDateFormat("dd-MM-yyyy", locale).format(new Date());
    private String detailsFromDatabase;
    private String addressFromDatabase;
    private String workTimeFromDatabase;
    private String workTimeBreakFromDatabase;
    private String contactsPhoneFromDatabase;
    private String contactsEmailFromDatabase;
    private String contactsUrlFromDatabase;
    private String prizeFundFromDatabase;
    private long timesUsedFromDatabase;
    private String rating;
    private String iconUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            snippet = extras.getString("SNIPPET");
        }

        setTitle(title);

        final ImageView markerInfoIcon = (ImageView) findViewById(R.id.marker_info_icon);

        mDatabase.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                findViewById(R.id.marker_info_rating_card).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final android.app.AlertDialog.Builder markerInfoRatingDialog = new android.app.AlertDialog.Builder(MarkerInfoActivity.this);
                        markerInfoRatingDialog.setTitle("Карточка рейтинга");
                        markerInfoRatingDialog.setIcon(R.drawable.marker_info_rating_icon);
                        markerInfoRatingDialog.setMessage("Здесь отображен средний рейтинг экопункта. " +
                                "Он необходим для того, чтобы пользователи смогли узнать, насколько хорошо " +
                                "его работники следят за ним. Если Вы уже использовали этот пункт " +
                                "и у Вас есть, что рассказать о нем, пожалуйста, оставьте оцентку, коснувшись кнопки \"Оценить\".");
                        markerInfoRatingDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        markerInfoRatingDialog.setPositiveButton("Оценить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (user.isAnonymous()) {
                                    Toast.makeText(MarkerInfoActivity.this, "В деморежиме нельзя оценивать экопункты", Toast.LENGTH_LONG).show();
                                } else if (dataSnapshot.child("users").child(user.getUid()).child("markers").child(snippet).getChildrenCount() > 0) {
                                    final android.app.AlertDialog.Builder setRatingDialog = new android.app.AlertDialog.Builder(MarkerInfoActivity.this);
                                    setRatingDialog.setTitle("Оценить");
                                    setRatingDialog.setMessage("Учтите, что в итоговом рейтинге будет учитываться " +
                                            "лишь одна положительная и одна отрицательная оценка от Вас. Кроме того, пожалуйста, " +
                                            "ставьте оценки максимально конструктивно, так как даже одна отрицательная " +
                                            "оценка может снизить общий рейтинг настолько, что никому не захочется его использовать.");
                                    setRatingDialog.setCancelable(false);
                                    setRatingDialog.setPositiveButton("Понравилось", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mDatabase.child("markers").child(snippet).child("rating")
                                                    .child("positive").child(user.getUid()).setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    showInfoDialog("Спасибо за оценку", "Сотрудники компаний по защите окружающей среды стараются делать все возможное для того, " +
                                                            "чтобы Вам было комфортно использовать их экологические пункты.");
                                                }
                                            });
                                        }
                                    });
                                    setRatingDialog.setNegativeButton("Не понравилось", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mDatabase.child("markers").child(snippet).child("rating")
                                                    .child("negative").child(user.getUid()).setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    showInfoDialog("Спасибо за оценку", "Сотрудники компаний по защите окружающей среды " +
                                                            "постараются сделать все возможное для улучшения услуг.");
                                                }
                                            });
                                        }
                                    });
                                    setRatingDialog.show();
                                } else {
                                    showErrorDialog();
                                }
                            }
                        });
                        markerInfoRatingDialog.show();
                    }
                });

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
                    if (dataSnapshot.child("markers").child(snippet).child("rating").getChildrenCount() > 0) {
                        rating = dataSnapshot.child("markers").child(snippet)
                                .child("rating").child("positive").getChildrenCount() * 100 / (dataSnapshot.child("markers").child(snippet)
                                .child("rating").child("positive").getChildrenCount() + dataSnapshot.child("markers").child(snippet)
                                .child("rating").child("negative").getChildrenCount()) + "%";
                    } else {
                        rating = "Нет рейтинга";
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
                ((TextView) findViewById(R.id.marker_info_rating)).setText(rating);

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
                            showInfoDialog("Спасибо",
                                    "Благодаря Вам наше приложение становится лучше. " +
                                            "Мы рассмотрим Вашу жалобу и примем необходимые меры.");
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

        final List<CardView> cards = new ArrayList<>();
        cards.add((CardView) findViewById(R.id.marker_info_details_card));
        cards.add((CardView) findViewById(R.id.marker_info_address_card));
        cards.add((CardView) findViewById(R.id.marker_info_work_time_card));
        cards.add((CardView) findViewById(R.id.marker_info_contacts_card));
        cards.add((CardView) findViewById(R.id.marker_info_prize_fund_card));
        cards.add((CardView) findViewById(R.id.marker_info_used_card));

        final List<String> titles = new ArrayList<>();
        titles.add("Карточка информции");
        titles.add("Карточка адреса");
        titles.add("Карточка времени работы и дат проведения");
        titles.add("Карточка контактов");
        titles.add("Карточка награды");
        titles.add("Карточка использования");

        final List<String> messages = new ArrayList<>();
        messages.add("Здесь мы собрали всю известную нам информацию об этом месте. " +
                "Если Вы считаете, что мы в чем-то ошиблись, пожалуйста, сообщите нам об этом, " +
                "используя кнопку жалобы в правом верхрем углу экрана информации.");
        messages.add("Чтобы Вам не пришлось искать это место вручную, " +
                "мы добавили особое меню в правом нижнем углу экрана с картой. " +
                "Просто коснитесь флажка экопункта. для отображения кнопок \"Проложить маршрут\" и " +
                "\"Показать на карте\". Коснувшись любой из них, " +
                "Вы будете перенаправлены в приложение \"Google Maps\" для дальнейшей навигации.");
        messages.add("Для экопунктов здесь показано время работы на протяжении недели, " +
                "а для экособытий – даты проведения. " +
                "Кроме того, здесь отображен график перерывов.");
        messages.add("Мы опросили работников экопунктов " +
                "и собрали для Вас всю имеющуюся у них контактную информацию. К сожалению, " +
                "далеко не все пункты имеют контактный телефон или собственный сайт, " +
                "однако даже у самых отдаленных пунктов нам удалось получить хотя бы один способ связи.");
        messages.add("У каждого экопункта есть свой призовой фонд. " +
                "Просто доберитесь до него, используйте и получите заслуженные очки в приложении. " +
                "Спасать планету еще никогда не было так просто!");
        messages.add("Здесь отображено суммартное количество раз использования этого экопункта.");

        final List<Integer> imageIds = new ArrayList<>();
        imageIds.add(R.drawable.marker_info_details_icon);
        imageIds.add(R.drawable.marker_info_address_icon);
        imageIds.add(R.drawable.marker_info_work_time_icon);
        imageIds.add(R.drawable.marker_info_contacts_icon);
        imageIds.add(R.drawable.marker_info_prize_fund_icon);
        imageIds.add(R.drawable.marker_info_used_icon);

        for (int i = 0; i < cards.size(); i++) {
            final String title = titles.get(i);
            final int imageId = imageIds.get(i);
            final String message = messages.get(i);
            cards.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final android.app.AlertDialog.Builder markerInfoDialog = new android.app.AlertDialog.Builder(MarkerInfoActivity.this);
                    markerInfoDialog.setTitle(title);
                    markerInfoDialog.setIcon(imageId);
                    markerInfoDialog.setMessage(message);
                    markerInfoDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    markerInfoDialog.show();
                }
            });
        }
    }

    private void showErrorDialog() {
        android.app.AlertDialog.Builder errorDialog = new android.app.AlertDialog.Builder(this);
        errorDialog.setTitle("Экопункт не был использован");
        errorDialog.setMessage("Вы можете оценивать только те экопункты, которые Вы посетили, не используя деморежим.");
        errorDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        errorDialog.show();
    }

    private void showInfoDialog(String title, String message) {
        android.app.AlertDialog.Builder infoDialog = new android.app.AlertDialog.Builder(this);
        infoDialog.setTitle(title);
        infoDialog.setMessage(message);
        infoDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        infoDialog.show();
    }
}
