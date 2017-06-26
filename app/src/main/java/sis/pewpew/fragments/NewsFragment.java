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

import static sis.pewpew.MainActivity.deleteCache;

public class NewsFragment extends Fragment {

    private ArrayList<String> mNews = new ArrayList<>();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String summary;
    private String date;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences("NEWS", 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);

        setHasOptionsMenu(false);

        if (!dialogShown) {
            AlertDialog.Builder newsFragmentWelcomeDialog = new AlertDialog.Builder(getActivity());
            newsFragmentWelcomeDialog.setTitle(getString(R.string.news_fragment_name));
            newsFragmentWelcomeDialog.setCancelable(false);
            newsFragmentWelcomeDialog.setIcon(R.drawable.ic_menu_news);
            newsFragmentWelcomeDialog.setMessage("В разделе \"Новости\" Вы можете следить за последними обновлениями в приложении. " +
                    "Кроме того, если Вы разрешили приложению отправлять Вам уведомления, " +
                    "мы оповестим Вас о появлении новых публикаций.");
            newsFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            newsFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.news_fragment_name));

        final ListView mListView = (ListView) rootView.findViewById(R.id.news_list);

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mNews);
        mListView.setAdapter(arrayAdapter);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapShot : dataSnapshot.child("news").getChildren()) {
                    if (childSnapShot.child("date").getValue() != null) {
                        date = childSnapShot.child("date").getValue().toString();
                    } else {
                        date = "Неизвестная дата";
                    }
                    if (childSnapShot.child("title").getValue() != null) {
                        summary = childSnapShot.child("title").getValue().toString();
                    } else {
                        summary = "Новость";
                    }
                    mNews.add("\n" + date + "\n\n" + summary + "\n");
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mNews.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.news_action_update) {
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
