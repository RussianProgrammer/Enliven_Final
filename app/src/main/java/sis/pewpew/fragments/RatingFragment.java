package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

import static com.google.android.gms.internal.zzt.TAG;
import static sis.pewpew.MainActivity.deleteCache;

public class RatingFragment extends Fragment {

    private ArrayList<String> mRating = new ArrayList<>();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String name;
    private long points;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

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

        ListView mListView = (ListView) rootView.findViewById(R.id.rating_list);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mRating);
        mListView.setAdapter(arrayAdapter);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.child("users").getChildren()) {
                    if (childSnapShot.child("name").getValue() != null) {
                        name = childSnapShot.child("name").getValue().toString();
                    } else {
                        name = "Анонимный пользователь";
                    }
                    if (childSnapShot.child("points").getValue() != null) {
                        points = (long) childSnapShot.child("points").getValue();
                    } else {
                        points = 0;
                    }

                    mRating.add(name + "     " + points);
                    arrayAdapter.notifyDataSetChanged();
                    //arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        /*ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot childSnapShot : dataSnapshot.child("users").getChildren()) {
                    String name = childSnapShot.child("name").getValue().toString();
                    long points = (long) childSnapShot.child("points").getValue();
                    mMeetings.add(name + " " + points);
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot childSnapShot : dataSnapshot.child("users").getChildren()) {
                    String name = childSnapShot.child("name").getValue().toString();
                    long points = (long) childSnapShot.child("points").getValue();
                    mMeetings.add(name + " " + points);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase.addChildEventListener(childEventListener);*/

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mRating.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rating, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.rating_action_update) {
            getActivity().recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        deleteCache(getActivity());
    }
}
