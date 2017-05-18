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

public class AboutFragment extends Fragment {

    Button startTutorialButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.about_fragment_name));

        startTutorialButton = (Button) rootView.findViewById(R.id.start_tutorial);
        startTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TutorialActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
