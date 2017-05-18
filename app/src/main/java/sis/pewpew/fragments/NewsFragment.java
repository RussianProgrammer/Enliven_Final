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

public class NewsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SharedPreferences settings = getActivity().getSharedPreferences("NEWS", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder newsFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            newsFragmentWelcomeDialog.setTitle(getString(R.string.news_fragment_name));
            newsFragmentWelcomeDialog.setCancelable(false);
            newsFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_news);
            newsFragmentWelcomeDialog.setMessage("В разделе \"Новости\" Вы можете следить за последними обновлениями в приложении. " +
                    "Кроме того, если Вы разрешили приложению отправлять Вам уведомления, " +
                    "мы оповестим Вас о появлении новых публикаций.");
            newsFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            newsFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.news_fragment_name));

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
