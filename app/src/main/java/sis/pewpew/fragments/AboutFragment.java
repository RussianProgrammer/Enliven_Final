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
import sis.pewpew.tutorial.TutorialActivity;

import static sis.pewpew.MainActivity.deleteCache;

public class AboutFragment extends Fragment {

    Button startTutorialButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.about_fragment_name));

        startTutorialButton = (Button) rootView.findViewById(R.id.start_tutorial);
        startTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TutorialActivity.class);
                intent.putExtra("ABOUT", 5);
                startActivity(intent);
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
