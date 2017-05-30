package sis.pewpew.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

public class ConsoleFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private long points;
    private long status;

    View rootView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        /*ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                points = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();
                status = (long) dataSnapshot.child("users").child(user.getUid()).child("status").getValue();
                if (points >= 10000 && status >= 3) {
                    rootView = inflater.inflate(R.layout.fragment_share, container, false);
                } else {
                    rootView = inflater.inflate(R.layout.fragment_console, container, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);*/

        rootView = inflater.inflate(R.layout.fragment_console, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.console_fragment_name));

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
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
