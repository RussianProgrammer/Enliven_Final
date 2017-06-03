package sis.pewpew.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sis.pewpew.MainActivity;
import sis.pewpew.R;

import static sis.pewpew.MainActivity.deleteCache;

public class FeedbackFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Locale locale = new Locale("ru");
    private String date = new SimpleDateFormat("dd-MM-yyyy", locale).format(new Date());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.feedback_fragment_name));

        final EditText feedbackField = (EditText) rootView.findViewById(R.id.feedback_field);
        final Button sendFeedbackButton = (Button) rootView.findViewById(R.id.send_feedback_button);
        final TextView feedbackResultText = (TextView) rootView.findViewById(R.id.feedback_result);
        final ImageView doneImage = (ImageView) rootView.findViewById(R.id.done);

        sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackField.getText().toString().isEmpty()) {
                    feedbackField.setError("Поле должно быть заполнено");
                } else if (feedbackField.getText().length() < 10) {
                    feedbackField.setError("Отзыв должен иметь полноценное содержание");
                } else {
                    mDatabase.child("feedback").child(user.getUid()).child(date)
                            .setValue(feedbackField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            feedbackResultText.setText("Отзыв принят");
                            doneImage.setImageResource(R.drawable.ic_done);
                            feedbackField.setText("");
                            feedbackField.setEnabled(false);
                            sendFeedbackButton.setEnabled(false);
                        }
                    });
                }
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