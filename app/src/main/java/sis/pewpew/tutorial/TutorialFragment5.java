package sis.pewpew.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sis.pewpew.R;

public class TutorialFragment5 extends Fragment {
    public static TutorialFragment5 newInstance() {
        return new TutorialFragment5();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tutorial_fragment_5, container, false);

        //IMAGEVIEW
        TextView TutorialText1 = (TextView) rootView.findViewById(R.id.tutorial_text_5);
        return rootView;

    }

}
