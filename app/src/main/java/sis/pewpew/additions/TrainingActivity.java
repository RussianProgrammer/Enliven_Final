package sis.pewpew.additions;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import sis.pewpew.R;

public class TrainingActivity extends AppCompatActivity {

    private String title;
    private String color;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("TITLE");
            color = extras.getString("COLOR");
            id = extras.getInt("ID");
        }

        ImageView iconView = (ImageView) findViewById(R.id.training_main_icon);
        TextView titleView = (TextView) findViewById(R.id.training_main_title);
        TextView descriptionView = (TextView) findViewById(R.id.training_main_text);
        CardView cardView = (CardView) findViewById(R.id.training_main_card);

        switch (id) {
            case 1:
                iconView.setImageResource(R.drawable.training_1_icon);
                descriptionView.setText(R.string.training_1_text);
                break;
            case 2:
                iconView.setImageResource(R.drawable.training_2_icon);
                descriptionView.setText(R.string.training_2_text);
                break;
        }

        cardView.setCardBackgroundColor(Color.parseColor(color));

        titleView.setText(title);
    }
}
