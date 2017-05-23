package sis.pewpew.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sis.pewpew.R;

public class TutorialFragment1 extends Fragment {
    public static TutorialFragment1 newInstance() {
        return new TutorialFragment1();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_fragment_1, container, false);
    }
}
