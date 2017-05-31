package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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

import sis.pewpew.MainActivity;
import sis.pewpew.R;

public class SettingsFragment extends PreferenceFragment {

    public FirebaseAuth mAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "LogInStatus";
    private DatabaseReference mDatabase;
    private AlertDialog.Builder signOutDialog;
    private AlertDialog.Builder redeemVoucherDialog;
    private AlertDialog.Builder deleteAccountDialog;
    private AlertDialog.Builder verifyAccountSupport;
    private AlertDialog.Builder resetDialog;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.settings_fragment_name));
        addPreferencesFromResource(R.xml.preferences);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        deleteAccountDialog = new AlertDialog.Builder(getActivity());
        deleteAccountDialog.setTitle("Удаление аккаунта");
        deleteAccountDialog.setMessage("Вы уверены, что хотите удалить текущий аккаунт?" +
                " Все данные, связанные с ним, будут безвозвратно удалены, включая очки и достижения.");
        deleteAccountDialog.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog("Удаление…");
                mDatabase.child("users").child(user.getUid()).removeValue().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        FirebaseAuth.getInstance().signOut();
                        user.delete();
                        logOut();
                        String[] sharedPrefsName = new String[]{"ACHIEVE1", "ACHIEVE2", "ACHIEVE3", "ACHIEVE4",
                                "ACHIEVE5", "ACHIEVE6", "ACHIEVE7", "ACHIEVE8", "ACHIEVE9", "ACHIEVE10", "ACHIEVE11",
                                "ACHIEVE12", "ACHIEVE13", "ACHIEVE14", "ACHIEVE15"};
                        for (String sharedPrefs : sharedPrefsName)
                            getActivity().getSharedPreferences(sharedPrefs, 0).edit().clear().apply();
                    }
                });

            }
        });
        deleteAccountDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        signOutDialog = new AlertDialog.Builder(getActivity());
        signOutDialog.setTitle("Выход");
        signOutDialog.setMessage("Вы уверены, что хотите выйти из текущего аккаунта?");
        signOutDialog.setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!user.isAnonymous()) {
                    FirebaseAuth.getInstance().signOut();
                    logOut();
                } else {
                    mDatabase.child("users").child(user.getUid()).removeValue().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();
                            user.delete();
                            logOut();
                        }
                    });
                }
            }
        });
        signOutDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        verifyAccountSupport = new AlertDialog.Builder(getActivity());
        verifyAccountSupport.setTitle("Аккаунт уже был подтвержден");
        verifyAccountSupport.setMessage("Дело в том, что при использовании учетной записи Google или демонстрационного режима, мы подтверждаем Ваш аккаунт автоматически. " +
                "Если же у Вас возникли подозрения, что кто-то мог получить доступ к Вашему аккаунту, пожалуйста, незамедлительно свяжитесь с нами.");
        verifyAccountSupport.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        resetDialog = new AlertDialog.Builder(getActivity());
        resetDialog.setTitle("Сброс");
        resetDialog.setMessage("Конфигурация приложения будет восстановлена.");
        resetDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        resetDialog.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reset();
            }
        });

        final Preference preference1 = findPreference("delete_account_button");
        preference1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteAccountDialog.show();
                return false;
            }
        });

        final Preference preference2 = findPreference("sign_out_button");
        preference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                signOutDialog.show();
                return false;
            }
        });

        final Preference preference5 = findPreference("reset_button");
        preference5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                resetDialog.show();
                return false;
            }
        });

        final Preference preference6 = findPreference("redeem_voucher_button");
        preference6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                redeemVoucherDialog = new AlertDialog.Builder(getActivity());
                redeemVoucherDialog.setTitle("Погашение промокода");
                redeemVoucherDialog.setMessage("Введите Ваш уникальный промокод ниже. " +
                        "Регистр не учитывается, однако дефисное разделение необходимо.");
                final EditText input = new EditText(getActivity());
                input.setHint("XXXX-XXXX-XXXX");
                input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                input.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(14)});
                redeemVoucherDialog.setView(input);
                redeemVoucherDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                redeemVoucherDialog.setPositiveButton("Погасить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (input.getText().length() < 14 || !input.getText().toString().contains("-")) {
                            showVoucherRedeemResultDialog("Неверный формат", "Пожалуйста, проверьте формат предоставленных данных. " +
                                    "Промокод должен содержать в общей сложности 12 символов и 2 дефиса-разделителя.");
                        } else {
                            showProgressDialog("Погашение…");
                            ValueEventListener postListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("vouchers").child(input.getText().toString().toUpperCase()).getValue() != null) {
                                        long pointsFromVoucher = (long) dataSnapshot.child("vouchers").child(input.getText().toString().toUpperCase()).getValue();
                                        DatabaseReference mProfilePoints = FirebaseDatabase.getInstance().getReference()
                                                .child("users").child(user.getUid()).child("points");
                                        onVoucherPointsAdded(mProfilePoints, pointsFromVoucher);
                                        mDatabase.child("vouchers").child(input.getText().toString().toUpperCase()).removeValue();
                                    } else {
                                        showVoucherRedeemResultDialog("Несуществующий промокод", "Пожалуйста, проверьте корректность введенного промокода. " +
                                                "Возможно, он уже был погашен ранее.");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                            mDatabase.addListenerForSingleValueEvent(postListener);
                        }
                    }
                });
                redeemVoucherDialog.show();
                return false;
            }
        });

        final Preference preference3 = findPreference("verify_account_button");
        preference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (user.isEmailVerified()) {
                    Toast.makeText(getActivity(), "Ваш аккаунт уже подтвержден", Toast.LENGTH_SHORT).show();
                } else {
                    sendEmailVerification();
                }
                return false;
            }
        });

        final Preference preference4 = findPreference("verify_account_support_button");
        preference4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                verifyAccountSupport.show();
                return false;
            }
        });

        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("account_verified_checkbox");
        if (user.isEmailVerified()) {
            checkBoxPreference.setChecked(true);
        }
        return rootView;
    }

    private void sendEmailVerification() {
        if (!user.isAnonymous()) {
            final FirebaseUser user = mAuth.getCurrentUser();
            assert user != null;
            user.sendEmailVerification()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Email подтверждения отправлен на " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(getActivity(),
                                        R.string.email_sending_error_message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "В деморежиме нельзя подтвердить аккаунт", Toast.LENGTH_LONG).show();
        }
    }

    private void logOut() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void reset() {
        String[] sharedPrefsName = new String[]{"MAP", "ACHIEVEMENTS", "EVENTS", "NEWS", "PROFILE", "PROGRESS", "TRAINING", "RATING"};
        for (String sharedPrefs : sharedPrefsName)
            getActivity().getSharedPreferences(sharedPrefs, 0).edit().clear().apply();
        Toast.makeText(getActivity(), "Настройки сброшены", Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog(String title) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage("Подождите…");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void onVoucherPointsAdded(DatabaseReference postRef, final long prizePoints) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                long pointsFromDatabase = 0;
                if (mutableData != null) {
                    pointsFromDatabase = (long) mutableData.getValue();
                }
                pointsFromDatabase = pointsFromDatabase + prizePoints;
                assert mutableData != null;
                mutableData.setValue(pointsFromDatabase);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                hideProgressDialog();
                showVoucherRedeemResultDialog("Промокод погашен", "Призовые очки успешно добавлены к Вашему профилю.");
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    private void showVoucherRedeemResultDialog(String resultTitle, String resultMessage) {
        hideProgressDialog();
        AlertDialog.Builder voucherRedeemResultDialog = new AlertDialog.Builder(getActivity());
        voucherRedeemResultDialog.setTitle(resultTitle);
        voucherRedeemResultDialog.setMessage(resultMessage);
        voucherRedeemResultDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        voucherRedeemResultDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}