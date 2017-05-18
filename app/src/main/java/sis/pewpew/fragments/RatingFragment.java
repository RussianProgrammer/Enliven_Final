package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

public class RatingFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SharedPreferences settings = getActivity().getSharedPreferences("RATING", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder ratingFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            ratingFragmentWelcomeDialog.setTitle(getString(R.string.rating_fragment_name));
            ratingFragmentWelcomeDialog.setCancelable(false);
            ratingFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_rating);
            ratingFragmentWelcomeDialog.setMessage("В разделе \"Рейтинг\" показана таблица лидеров по очкам, заработанным в приложении," +
                    " среди членов сообщества. Вы также можете найти свою позицию в рейтинге, используя фильтр.");
            ratingFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            ratingFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.rating_fragment_name));

        /*mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rating_list);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new EventsRecyclerViewAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);*/

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
