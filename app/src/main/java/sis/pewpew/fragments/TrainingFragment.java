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

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.utils.TrainingRecyclerViewAdapter;

public class TrainingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences("TRAINING", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder trainingFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            trainingFragmentWelcomeDialog.setTitle(getString(R.string.training_fragment_name));
            trainingFragmentWelcomeDialog.setCancelable(false);
            trainingFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_training);
            trainingFragmentWelcomeDialog.setMessage("В разделе \"Обучение\" Вы узнаете, какие существуют способы защиты окружающей среды. " +
                    "Кроме того, Вы научитесь многим простым вещам, которые сделают Вашу помощь планете еще эффективней.");
            trainingFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            trainingFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_training, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.training_fragment_name));

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.training_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new TrainingRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
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