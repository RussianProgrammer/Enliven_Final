package sis.pewpew.fragments;

import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

public class GratitudeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //ОГРОМНОЕ СПАСИБО ВСЕМ ТЕМ, КТО ПОМОГАЛ МНЕ В СОЗДАНИИ ПРОЕКТА ЛЮБЫМ СПОСОБОМ.
        //ДЛЯ МЕНЯ ЭТО ПО-НАСТОЯЩЕМУ ОЧЕНЬ ВАЖНО.

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.gratitude_fragment_name));
        return inflater.inflate(R.layout.fragment_gratitude, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        List<Integer> imageIds = new ArrayList<>();
        imageIds.add(R.drawable.gratitude_icon_1);
        imageIds.add(R.drawable.gratitude_icon_2);
        imageIds.add(R.drawable.gratitude_icon_3);
        imageIds.add(R.drawable.gratitude_icon_4);
        imageIds.add(R.drawable.gratitude_icon_5);
        for (Integer id : imageIds) {
            BitmapFactory.decodeResource(getResources(), id);
        }
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
    }
}
