package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.adapters.ProgressRecyclerViewAdapter;

import static sis.pewpew.MainActivity.deleteCache;

public class ProgressFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences("PROGRESS", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder progressFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
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

        final View rootView = inflater.inflate(R.layout.fragment_progress, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.progress_fragment_name));

        mDatabase.keepSynced(true);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.progress_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new ProgressRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);

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
