package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.utils.EventsRecyclerViewAdapter;

public class EventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getSharedPreferences("EVENTS", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder eventsFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            eventsFragmentWelcomeDialog.setTitle(getString(R.string.events_fragment_name));
            eventsFragmentWelcomeDialog.setCancelable(false);
            eventsFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_events);
            eventsFragmentWelcomeDialog.setMessage("В разделе \"События\" Вы сможете наблюдать за появлением новых экологических фестивалей и других подобных мероприятний, проходящих в Вашем городе. " +
                    "Эти мероприятия будут также отмечены специальным флажком на карте, а за явку на такое событие Вы получите увеличенную награду."
            );
            eventsFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            eventsFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.events_fragment_name));

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.events_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new EventsRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        List<Integer> imageIds = new ArrayList<>();
        imageIds.add(R.drawable.fest_icon);
        imageIds.add(R.drawable.fest_icon_2);
        imageIds.add(R.drawable.fest_icon_3);
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
