package sis.pewpew.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sis.pewpew.R;
import sis.pewpew.classes.LeaderData;

public class RatingRecyclerViewAdapter extends RecyclerView.Adapter<RatingRecyclerViewAdapter.MyHolder>{

    private List<LeaderData> list;

    public RatingRecyclerViewAdapter(List<LeaderData> list) {
        this.list = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_card_layout, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        LeaderData myList = list.get(position);
        holder.name.setText(myList.getName());
        holder.points.setText("" + myList.getPoints());
    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try {
            if (list.size() == 0) {
                arr = 0;
            } else {
                arr = list.size();
            }
        } catch (Exception ignored) {}
        return arr;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView name, points;
        MyHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.rating_name);
            points = (TextView) itemView.findViewById(R.id.rating_points);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
