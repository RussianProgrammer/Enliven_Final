package sis.pewpew.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import sis.pewpew.support.SendMarkerInfoActivity;

import static com.google.android.gms.internal.zzt.TAG;
import static sis.pewpew.MainActivity.deleteCache;

public class ConsoleFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private long points;
    private long timesSent;
    private long status;

    View rootView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_console, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.console_fragment_name));

        final Button addMarkerButton = (Button) rootView.findViewById(R.id.console_times_sent_button);
        final Button openModerationConsoleButton = (Button) rootView.findViewById(R.id.console_times_accepted_button);
        final TextView timesSentText = (TextView) rootView.findViewById(R.id.console_times_sent);
        TextView timesAcceptedText = (TextView) rootView.findViewById(R.id.console_times_accepted);

        ValueEventListener postListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(user.getUid()).child("timesSent").getValue() != null) {
                    timesSent = (long) dataSnapshot.child("users").child(user.getUid()).child("timesSent").getValue();
                } else {
                    timesSent = 0;
                }
                if (dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {
                    points = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();
                } else {
                    points = 0;
                }
                if (dataSnapshot.child("users").child(user.getUid()).child("status").getValue() != null) {
                    status = (long) dataSnapshot.child("users").child(user.getUid()).child("status").getValue();
                } else {
                    status = 0;
                }

                timesSentText.setText("" + timesSent);

                openModerationConsoleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (status >= 3 || user.isAnonymous()) {
                            Intent intent = new Intent(getActivity(), SendMarkerInfoActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Только пользователи с пометной уровня Модератор или выше " +
                                    "могут производить модерацию запросов. Если Вы являетесь сотрудником " +
                                    "организации по защите окружающей среды, Вы можете запросить пометку " +
                                    "повышенного уровня доступа в Настройках.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                addMarkerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (points >= 10000 || status >= 3  || user.isAnonymous()) {
                            Intent intent = new Intent(getActivity(), SendMarkerInfoActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Предлагать новые экопункты могут только пользователи, " +
                                    "набравшие свыше 10000 очков, либо владельцы пометки повышенного " +
                                    "уровня доступа.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);

        final List<String> titles = new ArrayList<>();
        titles.add("Карточка предложенных пунктов");
        titles.add("Карточка одобренных пунктов");

        final List<String> messages = new ArrayList<>();
        messages.add("Если Вы набрали более 10000 очков, Вы получаете возможность предлагать " +
                "новые экопункты. Модераторы из организаций по защите окружающей среды " +
                "смогут просмотреть Ваш запрос, и в случае, если вся предоставленная Вами информация " +
                "окажется корректной, Ваш экопункт будет добавлен на Карту. Здесь отображено количество " +
                "отправленных Вами запросов.");
        messages.add("Если Вы являетесь сотрудником одной из организаций по защите окружающей среды " +
                "и уже имеете пометку уровня Модератор или выше, то Вы можете осуществлять модерацию " +
                "запросов на добавление экопунктов. Здесь же показано количество одобренных Вами предложений.");

        final List<CardView> cards = new ArrayList<>();
        cards.add((CardView) rootView.findViewById(R.id.console_times_sent_card));
        cards.add((CardView) rootView.findViewById(R.id.console_times_accepted_card));

        final List<Integer> imageIds = new ArrayList<>();
        imageIds.add(R.drawable.add_marker_icon);
        imageIds.add(R.drawable.verified_markers_icon);

        for (int i = 0; i < cards.size(); i++) {
            final int finalI = i;
            cards.get(finalI).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder consoleCardInfoDialoog = new AlertDialog.Builder(getActivity());
                    consoleCardInfoDialoog.setIcon(imageIds.get(finalI));
                    consoleCardInfoDialoog.setTitle(titles.get(finalI));
                    consoleCardInfoDialoog.setMessage(messages.get(finalI));
                    consoleCardInfoDialoog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    consoleCardInfoDialoog.show();
                }
            });
        }
        return rootView;
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
        deleteCache(getActivity());
    }
}
