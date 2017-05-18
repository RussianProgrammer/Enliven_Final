package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

import static com.google.android.gms.internal.zzt.TAG;

public class ProgressFragment extends Fragment {

    private DatabaseReference mDatabase;
    private AlertDialog.Builder progressFragmentWelcomeDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences("PROGRESS", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            progressFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            progressFragmentWelcomeDialog.setTitle(getString(R.string.progress_fragment_name));
            progressFragmentWelcomeDialog.setCancelable(false);
            progressFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_progress);
            progressFragmentWelcomeDialog.setMessage("В разделе \"Прогресс\" показаны результаты совместной работы всего сообщества. " +
                    "Также здесь показан и Ваш личный вклад в спасение планеты.");
            progressFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            progressFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_progress, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.progress_fragment_name));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        ValueEventListener pointsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long usersCountFromDatabase = dataSnapshot.child("users").getChildrenCount();
                long pointsFromDatabase = (long) dataSnapshot.child("progress").child("points").getValue();
                TextView points = (TextView) rootView.findViewById(R.id.progress_points);
                TextView usersCount = (TextView) rootView.findViewById(R.id.progress_users_count);
                usersCount.setText("" + usersCountFromDatabase);
                points.setText("" + (int) pointsFromDatabase);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(pointsListener);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
