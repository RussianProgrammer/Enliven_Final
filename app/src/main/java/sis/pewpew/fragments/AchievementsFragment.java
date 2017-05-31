package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.utils.AchievesRecyclerViewAdapter;

import static com.google.android.gms.internal.zzt.TAG;

public class AchievementsFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Locale locale = new Locale("ru");
    private String date = new SimpleDateFormat("dd.MM.yyyy", locale).format(new Date());
    private long points;
    private boolean closed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVEMENTS", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder achievementsFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            achievementsFragmentWelcomeDialog.setTitle(getString(R.string.achievements_fragment_name));
            achievementsFragmentWelcomeDialog.setCancelable(false);
            achievementsFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_achievements);
            achievementsFragmentWelcomeDialog.setMessage("В разделе \"Достижения\" Вы сможете исследовать полученные награды. " +
                    "Каждый раз, открывая новое достижение, дата его получения регистрируется в Вашем аккаунте. " +
                    "Кроме того, коснувшись открытого достижения Вы также можете просмотреть, " +
                    "кто еще из участников открыл его, а также прочесть о нем краткую информацию.");
            achievementsFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            achievementsFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        final View rootView = inflater.inflate(R.layout.fragment_achievements, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.achievements_fragment_name));

        mDatabase.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {
                    points = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();
                } else {
                    points = 0;
                }

                if (!closed) {
                    if (!user.isAnonymous()) {
                        if (points >= 100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE1", 0);
                            boolean achieve1Gotten = settings.getBoolean("achieve1Gotten", true);
                            if (achieve1Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("1").setValue(date);
                                mDatabase.child("achieves").child("1").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Кислое, сладкое, снаружи черное, внутри белое. " +
                                            "Что это можеть быть?");
                                }
                            }

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve1Gotten", false);
                            editor.apply();
                        }
                        if (points >= 600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE2", 0);
                            boolean achieve2Gotten = settings.getBoolean("achieve2Gotten", true);
                            if (achieve2Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("2").setValue(date);
                                mDatabase.child("achieves").child("2").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Осторожно! Вам понадобится противогаз.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve2Gotten", false);
                            editor.apply();
                        }
                        if (points >= 1100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE3", 0);
                            boolean achieve3Gotten = settings.getBoolean("achieve3Gotten", true);
                            if (achieve3Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("3").setValue(date);
                                mDatabase.child("achieves").child("3").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Когда-нибудь пробовали кокосы? Так вот, это не кокос.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve3Gotten", false);
                            editor.apply();
                        }
                        if (points >= 1600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE4", 0);
                            boolean achieve4Gotten = settings.getBoolean("achieve4Gotten", true);
                            if (achieve4Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("4").setValue(date);
                                mDatabase.child("achieves").child("4").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Отвратительный с виду, приятный на вкус.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve4Gotten", false);
                            editor.apply();
                        }
                        if (points >= 2100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE5", 0);
                            boolean achieve5Gotten = settings.getBoolean("achieve5Gotten", true);
                            if (achieve5Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("5").setValue(date);
                                mDatabase.child("achieves").child("5").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Выглядит эффектно!");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve5Gotten", false);
                            editor.apply();
                        }
                        if (points >= 2600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE6", 0);
                            boolean achieve6Gotten = settings.getBoolean("achieve6Gotten", true);
                            if (achieve6Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("6").setValue(date);
                                mDatabase.child("achieves").child("6").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Это не гриб… Наверное…");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve6Gotten", false);
                            editor.apply();
                        }
                        if (points >= 3100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE7", 0);
                            boolean achieve7Gotten = settings.getBoolean("achieve7Gotten", true);
                            if (achieve7Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("7").setValue(date);
                                mDatabase.child("achieves").child("7").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Ого! Вот это цвета!");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve7Gotten", false);
                            editor.apply();
                        }
                        if (points >= 3600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE8", 0);
                            boolean achieve8Gotten = settings.getBoolean("achieve8Gotten", true);
                            if (achieve8Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("8").setValue(date);
                                mDatabase.child("achieves").child("8").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Глаз дракона. Звучит неплохо, правда?");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve8Gotten", false);
                            editor.apply();
                        }
                        if (points >= 4100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE9", 0);
                            boolean achieve9Gotten = settings.getBoolean("achieve9Gotten", true);
                            if (achieve9Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("9").setValue(date);
                                mDatabase.child("achieves").child("9").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Ваш звездный час! В прямом смысле.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve9Gotten", false);
                            editor.apply();
                        }
                        if (points >= 4600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE10", 0);
                            boolean achieve10Gotten = settings.getBoolean("achieve10Gotten", true);
                            if (achieve10Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("10").setValue(date);
                                mDatabase.child("achieves").child("10").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Ух! Снова драконий, только уже не глаз.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve10Gotten", false);
                            editor.apply();
                        }
                        if (points >= 5100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE11", 0);
                            boolean achieve11Gotten = settings.getBoolean("achieve11Gotten", true);
                            if (achieve11Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("11").setValue(date);
                                mDatabase.child("achieves").child("11").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Полезно для здоровья. Даже очень.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve11Gotten", false);
                            editor.apply();
                        }
                        if (points >= 5600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE12", 0);
                            boolean achieve12Gotten = settings.getBoolean("achieve12Gotten", true);
                            if (achieve12Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("12").setValue(date);
                                mDatabase.child("achieves").child("12").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Забавное название, знакомый вкус.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve12Gotten", false);
                            editor.apply();
                        }
                        if (points >= 6100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE13", 0);
                            boolean achieve13Gotten = settings.getBoolean("achieve13Gotten", true);
                            if (achieve13Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("13").setValue(date);
                                mDatabase.child("achieves").child("13").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Вы любите змей?");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve13Gotten", false);
                            editor.apply();
                        }
                        if (points >= 6600) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE14", 0);
                            boolean achieve14Gotten = settings.getBoolean("achieve14Gotten", true);
                            if (achieve14Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("14").setValue(date);
                                mDatabase.child("achieves").child("14").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Яблоко в орехе. Посмотрим.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve14Gotten", false);
                            editor.apply();
                        }
                        if (points >= 7100) {
                            SharedPreferences settings = getActivity().getSharedPreferences("ACHIEVE15", 0);
                            boolean achieve15Gotten = settings.getBoolean("achieve15Gotten", true);
                            if (achieve15Gotten) {
                                mDatabase.child("users").child(user.getUid()).child("achievements").child("15").setValue(date);
                                mDatabase.child("achieves").child("15").child(user.getUid()).setValue(date);
                                if (user.getDisplayName() != null) {
                                    showNewAchieveDialog("Вы открыли все достижения! Поздравляем! Последнее, что Вас ждет - это " +
                                            "рогатая смесь всех вкусов.");
                                }
                            }
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("achieve15Gotten", false);
                            editor.apply();
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

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.achieves_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new AchievesRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private void showNewAchieveDialog(String uniqueText) {
        AlertDialog.Builder newAchieveDialogDialog = new AlertDialog.Builder(getActivity());
        newAchieveDialogDialog.setTitle("Новое достижение");
        newAchieveDialogDialog.setIcon(R.drawable.unknown_achieve_icon);
        newAchieveDialogDialog.setMessage("Похоже, Вы разблокировали новое достижение. " + uniqueText);
        newAchieveDialogDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        newAchieveDialogDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        super.onDestroyView();
        closed = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        closed = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}