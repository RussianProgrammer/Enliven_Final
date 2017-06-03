package sis.pewpew.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

import static sis.pewpew.MainActivity.deleteCache;

public class ShareFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_share, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.share_fragment_name));

        Button mainShareButton = (Button) rootView.findViewById(R.id.main_share_button);
        mainShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Присоединяйтесь к проекту Enliven. Здесь Вы сможете по-настоящему помочь планете и получить за это награду! #Enliven";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(shareIntent, "Поделиться профилем"));
            }
        });
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
