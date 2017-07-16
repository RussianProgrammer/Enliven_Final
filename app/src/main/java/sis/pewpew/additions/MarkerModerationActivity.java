package sis.pewpew.additions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import sis.pewpew.R;
import sis.pewpew.adapters.MarkerModerationRecyclerViewAdapter;

public class MarkerModerationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_moderation);

        setTitle("Консоль модерации");

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.marker_moderation_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter adapter = new MarkerModerationRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(adapter);
    }
}
