package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

public class TrainingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SharedPreferences settings = getActivity().getSharedPreferences("TRAINING", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder trainingFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            trainingFragmentWelcomeDialog.setTitle(getString(R.string.training_fragment_name));
            trainingFragmentWelcomeDialog.setCancelable(false);
            trainingFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_training);
            trainingFragmentWelcomeDialog.setMessage("В разделе \"Обучение\" Вы узнаете, какие существуют способы защиты окружающей среды. " +
                    "Кроме того, Вы научитесь многим простым вещам, которые сделают Вашу помощь планете еще полезней.");
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
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.training_fragment_name));
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
