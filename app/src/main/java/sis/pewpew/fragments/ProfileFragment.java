package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import sis.pewpew.MainActivity;
import sis.pewpew.R;

import static com.google.android.gms.internal.zzt.TAG;

public class ProfileFragment extends Fragment {

    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public long pointsFromDatabase;
    public long timesUsedFromDatabase;
    private String statusFromDatabase;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        SharedPreferences settings = getActivity().getSharedPreferences("PROFILE", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder profileFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            profileFragmentWelcomeDialog.setTitle(getString(R.string.profile_fragment_name));
            profileFragmentWelcomeDialog.setCancelable(false);
            profileFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_profile);
            profileFragmentWelcomeDialog.setMessage("В разделе \"Профиль\" мы собрали всю самую интересную информацию о Вас. " +
                    "А именно, все Ваши очки, достижения и заслуги перед планетой. Не забудьте похвастаться ими, " +
                    "коснувшись кнопки \"Поделиться\".");
            profileFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            profileFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.profile_fragment_name));

        mDatabase.keepSynced(true);

        ValueEventListener pointsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView points = (TextView) rootView.findViewById(R.id.profile_points);
                TextView used = (TextView) rootView.findViewById(R.id.profile_used);
                TextView status = (TextView) rootView.findViewById(R.id.profile_status);
                TextView rank = (TextView) rootView.findViewById(R.id.profile_rank);
                TextView achieves = (TextView) rootView.findViewById(R.id.profile_achieves);
                TextView savedTrees = (TextView) rootView.findViewById(R.id.profile_saved_trees);
                TextView savedAnimals = (TextView) rootView.findViewById(R.id.profile_saved_animals);
                TextView savedPeople = (TextView) rootView.findViewById(R.id.profile_saved_people);

                if (user != null && dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {
                    pointsFromDatabase = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();

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

                if (user != null && dataSnapshot.child("users").child(user.getUid()).child("status").getValue() != null) {
                    statusFromDatabase = dataSnapshot.child("users").child(user.getUid()).child("status").getValue().toString();

                    switch (statusFromDatabase) {
                        case "1":
                            status.setText("Сотрудник");
                            break;
                        case "2":
                            status.setText("Организатор");
                            break;
                        case "3":
                            status.setText("Модератор");
                            break;
                        case "4":
                            status.setText("Администратор");
                            break;
                        case "5":
                            status.setText("Создатель");
                            break;
                    }
                } else {
                    status.setText("Пользователь");
                }
                if (dataSnapshot.child("users").child(user.getUid()).child("timesUsed").getValue() != null) {
                    timesUsedFromDatabase = (long) dataSnapshot.child("users").child(user.getUid()).child("timesUsed").getValue();
                    used.setText("" + timesUsedFromDatabase);
                } else {
                    used.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(pointsListener);

        if (user != null && user.getDisplayName() != null) {
            TextView username = (TextView) rootView.findViewById(R.id.profile_username);
            username.setText(user.getDisplayName());
        } else {
            TextView username = (TextView) rootView.findViewById(R.id.profile_username);
            username.setText("Имя пользователя");
        }

        List<CardView> cards = new ArrayList<>();
        cards.add((CardView) rootView.findViewById(R.id.profile_main_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_points_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_used_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_rank_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_achieves_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_saved_trees_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_saved_animals_card));
        cards.add((CardView) rootView.findViewById(R.id.profile_saved_people_card));

        final List<String> titles = new ArrayList<>();
        titles.add("Карточка профиля");
        titles.add("Карточка очков");
        titles.add("Карточка использованных экопунктов");
        titles.add("Карточка звания");
        titles.add("Карточка достижений");
        titles.add("Карточка спасенных деревьев");
        titles.add("Карточка спасеных животных");
        titles.add("Карточка спасеных людей");

        final List<String> messages = new ArrayList<>();
        messages.add("Это Ваша карточка профиля, на которой мы аккуратно " +
                "выгравировали Ваше имя и фамилию. У Вас мог возникнуть вопрос, " +
                "что означает пометка под именем. Отвечаем: она показывает, какую роль Вы выполняете в нашем сообществе. " +
                "Вы можете быть как обычным пользователем, так и модератором. " +
                "По всем вопросам касательно пометки Вы всегда можете обратиться к нам на нашем сайте.");
        messages.add("Здесь показано количество заработанных Вами очков. " +
                "Их можно получать, используя экологические пункты на карте. И помните: " +
                "с каждой новой цифрой на этой карточке мы все ближе к светлому будущему.");
        messages.add("Здесь отображено количество найденных и использованных Вами экопунктов. " +
                "По мере того, как эта цифра растет, жизнь на Земле становится лучше. Не останавливайтесь.");
        messages.add("Здесь паказано Ваше звание. Чем больше очков Вы зарабатываете, тем оно выше. " +
                "Не забывайте им хвастаться время от времени.");
        messages.add("Здесь мы посчитали все собранные Вами достижения. Как Вы уже наверняка знаете, " +
                "всего их 15, но со временем их количество будет расти. За каждые 200 очков Вы получаете одно новое. " +
                "Кроме того Вы всегда можете поделиться ими.");
        messages.add("Здесь отображено количество спасенных Вами деревьев. " +
                "Каждый раз зарабатывая 500 очков, Вы спасаете одно новое дерево, " +
                "которое могло погибнуть из-за токсинов и химикатов, находящихся в мусоре. " +
                "Кстати, собрав 2 млн очков, Вы спасете целый лес. Думаете, невозможно? " +
                "Просто сделайте использование приложения своей привычкой. Причем очень полезной.");
        messages.add("Здесь показано количество спасенных Вами животных. " +
                "Каждый раз собирая 1000 очков, Вы спасаете одного зверька, а может даже и целого зверя. " +
                "Не стесняйтесь делиться своими заслугами перед фауной нашей планеты с миром.");
        messages.add("Здесь мы посчитали, сколько реальных людей Вы спасли, используя приложение. " +
                "За каждые заработанные Вами 1200 очков один человек из будущего или даже настоящего говорит Вам \"Спасибо\"" +
                " за сохраненную жизнь.");

        final List<Integer> imageIds = new ArrayList<>();
        imageIds.add(R.drawable.profile_icon);
        imageIds.add(R.drawable.profile_points_icon_2);
        imageIds.add(R.drawable.profile_used_icon);
        imageIds.add(R.drawable.profile_rank_icon);
        imageIds.add(R.drawable.profile_achieves_icon);
        imageIds.add(R.drawable.profile_saved_trees_icon);
        imageIds.add(R.drawable.profile_saved_animals_icon);
        imageIds.add(R.drawable.profile_saved_people_icon);

        for (int i = 0; i < cards.size(); i++) {
            final String title = titles.get(i);
            final int imageId = imageIds.get(i);
            final String message = messages.get(i);
            cards.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder profileDialog = new AlertDialog.Builder(getActivity());
                    profileDialog.setTitle(title);
                    profileDialog.setIcon(imageId);
                    profileDialog.setMessage(message);
                    profileDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    profileDialog.setPositiveButton("Поделиться", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            shareProfile();
                        }
                    });
                    profileDialog.show();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.profile_action_share) {
            shareProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareProfile() {
        ValueEventListener postListenerForSharing = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (user != null && dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {
                    pointsFromDatabase = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();
                } else {
                    pointsFromDatabase = 0;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                if (pointsFromDatabase > 1200) {
                    String shareBody = "В приложении Enliven я заработал " + (int) pointsFromDatabase + " очков, спас " +
                            (int) (pointsFromDatabase / 500) + " деревьев, " + (int) (pointsFromDatabase / 1000) +
                            " животных и " + (int) (pointsFromDatabase / 1200) + " человек! Присоединяйтесь ко мне, " +
                            "пора все менять! #Enliven";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if (pointsFromDatabase > 1000) {
                    String shareBody = "В приложении Enliven я заработал " + (int) pointsFromDatabase + " очков, спас " +
                            (int) (pointsFromDatabase / 500) + " дерева и " + (int) (pointsFromDatabase / 1000) +
                            " животное! Присоединяйтесь ко мне, " +
                            "пора все менять! #Enliven";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if (pointsFromDatabase > 500) {
                    String shareBody = "В приложении Enliven я заработал " + (int) pointsFromDatabase + " очков и спас " +
                            (int) (pointsFromDatabase / 500) + " дерево! Присоединяйтесь ко мне, " +
                            "пора все менять! #Enliven";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else if (pointsFromDatabase > 0) {
                    String shareBody = "В приложении Enliven я получил " + (int) pointsFromDatabase + " очков." +
                            " Зарабатывая их, я улучшаю экологию на планете. Присоединяйтесь ко мне, " +
                            "пора все менять! #Enliven";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                } else {
                    String shareBody = "В приложении Enliven я спасаю наш мир. Присоединяйтесь ко мне, " +
                            "пора все менять! #Enliven";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                }
                startActivity(Intent.createChooser(shareIntent, "Поделиться профилем"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListenerForSharing);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}