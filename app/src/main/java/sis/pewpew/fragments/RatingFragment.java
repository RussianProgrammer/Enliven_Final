package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.adapters.RatingRecyclerViewAdapter;
import sis.pewpew.classes.LeaderData;

import static sis.pewpew.MainActivity.deleteCache;

public class RatingFragment extends Fragment {

    FirebaseDatabase mDatabase;
    DatabaseReference myRef;
    List<LeaderData> list;
    RecyclerView recycle;
    RatingRecyclerViewAdapter recyclerAdapter;
    Button show;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences("RATING", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        if (!dialogShown) {
            AlertDialog.Builder ratingFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            ratingFragmentWelcomeDialog.setTitle(getString(R.string.rating_fragment_name));
            ratingFragmentWelcomeDialog.setCancelable(false);
            ratingFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_rating);
            ratingFragmentWelcomeDialog.setMessage("В разделе \"Рейтинг\" показана таблица лидеров по очкам, заработанным в приложении," +
                    " среди членов сообщества.");
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

        show = (Button) rootView.findViewById(R.id.show_rating);

        show.performClick();

        recycle = (RecyclerView) rootView.findViewById(R.id.rating_list);
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference().child("users");

        Query data = myRef.orderByChild("points");

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    LeaderData value = dataSnapshot1.getValue(LeaderData.class);
                    LeaderData fire = new LeaderData();
                    String name = value.getName();
                    long points = value.getPoints();
                    fire.setName(name);
                    fire.setPoints(points);
                    list.add(fire);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Firebase Error.", error.toException());
            }
        });

        /*RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.achieves_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new AchievesRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(adapter);*/

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerAdapter = new RatingRecyclerViewAdapter(list);
                LinearLayoutManager recyce = new LinearLayoutManager(getActivity());
                recyce.setReverseLayout(true);
                recyce.setStackFromEnd(true);
                recycle.setLayoutManager(recyce);
                recycle.setItemAnimator(new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);
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