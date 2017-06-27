package sis.pewpew.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sis.pewpew.MainActivity;
import sis.pewpew.R;
import sis.pewpew.adapters.RatingRecyclerViewAdapter;
import sis.pewpew.additions.UserProfileActivity;
import sis.pewpew.classes.LeaderData;

import static sis.pewpew.MainActivity.deleteCache;

public class RatingFragment extends Fragment {

    private List<LeaderData> result;
    private RatingRecyclerViewAdapter adapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Locale locale = new Locale("ru");
    private String date = new SimpleDateFormat("dd-MM-yyyy", locale).format(new Date());
    private ProgressBar progressBar;

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
                    " среди членов сообщества. Коснувшись стрелки в верхней части экрана, можно обновить список");
            ratingFragmentWelcomeDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    AlertDialog.Builder newTipDialog = new AlertDialog.Builder(getActivity());
                    newTipDialog.setCancelable(false);
                    newTipDialog.setTitle("Жесты");
                    newTipDialog.setMessage("При помощи долгого касания на карточке участника " +
                            "можно открыть раскрывающееся меню опций.");
                    newTipDialog.setIcon(R.drawable.ic_rating_tip);
                    newTipDialog.setNegativeButton("Понятно", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    newTipDialog.show();
                }
            });
            ratingFragmentWelcomeDialog.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.apply();
        }

        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.rating_fragment_name));

        progressBar = (ProgressBar) rootView.findViewById(R.id.rating_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        result = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rating_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RatingRecyclerViewAdapter(result);
        recyclerView.setAdapter(adapter);

        updateRating();

        return rootView;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                openUserInfo(item.getGroupId());
                break;
            case 1:
                if (!user.isAnonymous()) {
                    reportUser(item.getGroupId());
                } else {
                    Toast.makeText(getActivity(), "В деморежиме нельзя отправлять жалобы", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updateRating() {
        Query data = mDatabase.child("users").orderByChild("points");

        data.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                result.add(dataSnapshot.getValue(LeaderData.class));
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                LeaderData data = dataSnapshot.getValue(LeaderData.class);
                int index = getItemIndex(data);
                result.set(index, data);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                LeaderData data = dataSnapshot.getValue(LeaderData.class);
                int index = getItemIndex(data);
                result.remove(index);
                adapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //ACTION IGNORED
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ACTION IGNORED
            }
        });
    }

    private int getItemIndex(LeaderData leaderData) {

        int index = -1;

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).key.equals(leaderData.key)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rating_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.rating_action_refresh) {
            result.clear();
            updateRating();
        }
        return super.onOptionsItemSelected(item);
    }

    /*@SuppressWarnings("ConstantConditions")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<LeaderData> newList = new ArrayList<>();
                Collections.reverse(newList);
                for (LeaderData data : result) {
                    String name = data.name.toLowerCase();
                    if (name.contains(newText)) {
                        newList.add(data);
                    }
                }

                adapter.setFilter(newList);
                return true;
            }
        });
    }*/

    private void openUserInfo(int position) {
        final String userId = mDatabase.child("users").child(result.get(position).key).getKey();
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra("UID", userId);
        intent.putExtra("POSITION", position + "");
        startActivity(intent);
    }

    private void reportUser(int position) {
        final String userId = mDatabase.child("users").child(result.get(position).key).getKey();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final AlertDialog.Builder reportUserDialog = new AlertDialog.Builder(getActivity());
                reportUserDialog.setTitle("Отправка жалобы");
                reportUserDialog.setMessage("Если Вам кажется, что участник " + dataSnapshot.child("users")
                        .child(userId).child("name").getValue().toString() + " использует уязвимости в приложении для " +
                        "нечестного ведения игры, Вы можете сообщить нам об этом. Учтите, что отправка ложной жалобы может повлечь " +
                        "за собой применение мер в сторону Вашего аккаунта.");
                reportUserDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                reportUserDialog.setPositiveButton("Сообщить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        mDatabase.child("userReports").child(userId).child("report by " + user.getUid()).child(date).setValue("reported")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialogInterface.cancel();
                                        AlertDialog.Builder reportSentDialog = new AlertDialog.Builder(getActivity());
                                        reportSentDialog.setTitle("Жалоба отправлена");
                                        reportSentDialog.setMessage("Мы рассмотрим Вашу жалобу и примем необходимые меры. " +
                                                "Спасибо за сотрудничество.");
                                        reportSentDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                        reportSentDialog.show();
                                    }
                                });
                    }
                });
                reportUserDialog.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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