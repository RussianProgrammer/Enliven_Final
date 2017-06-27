package sis.pewpew.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import sis.pewpew.R;
import sis.pewpew.classes.LeaderData;

public class RatingRecyclerViewAdapter extends RecyclerView.Adapter<RatingRecyclerViewAdapter.RatingViewHolder> {

    private List<LeaderData> list;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private final String[] colors = new String[]{
            "#FFD700", "#C0C0C0", "#CD7F32"};

    private final int[] iconIds = new int[]{
            R.drawable.rating_1_place_icon, R.drawable.rating_2_place_icon,
            R.drawable.rating_3_place_icon};

    public RatingRecyclerViewAdapter(List<LeaderData> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RatingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RatingViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rating_card_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RatingViewHolder holder, int position) {
        LeaderData leader = list.get(position);

        holder.name.setText(leader.name);
        holder.points.setText("" + leader.points);

        if (list.size() - holder.getAdapterPosition() < 4) {
            holder.card.setCardBackgroundColor(Color.parseColor(colors[list.size() - holder.getAdapterPosition() - 1]));
            holder.icon.setImageResource(iconIds[list.size() - holder.getAdapterPosition() - 1]);
        } else {
            holder.leaderPosition.setText("" + (list.size() - holder.getAdapterPosition()));
        }

        if (list.size() - holder.getAdapterPosition() > 9) {
            holder.leaderPosition.setTextSize(40);
        } else if (list.size() - holder.getAdapterPosition() > 99) {
            holder.leaderPosition.setTextSize(35);
        } else if (list.size() - holder.getAdapterPosition() > 999) {
            holder.leaderPosition.setTextSize(30);
        } else if (list.size() - holder.getAdapterPosition() > 9999) {
            holder.leaderPosition.setTextSize(25);
        } else if (list.size() - holder.getAdapterPosition() > 99999) {
            holder.leaderPosition.setTextSize(20);
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*if (holder.name.getText().toString().equals(dataSnapshot.child("users").child(user.getUid()).child("name").getValue().toString()) && holder.points.getText().toString().equals(
                        (dataSnapshot.child("users").child(user.getUid()).child("points").getValue() + ""))) {
                    holder.card.setCardBackgroundColor(Color.parseColor("#000000"));
                } else {
                    holder.card.setCardBackgroundColor(Color.parseColor("#000000"));
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ACTION IGNORED
            }
        };

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(holder.getAdapterPosition(), 0, 0, "Об участнике");
                contextMenu.add(holder.getAdapterPosition(), 1, 0, "Сообщить о нечестной игре");
            }
        });

        mDatabase.addValueEventListener(postListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView points;
        TextView leaderPosition;
        CardView card;
        ImageView icon;

        RatingViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.rating_name);
            points = (TextView) itemView.findViewById(R.id.rating_points);
            leaderPosition = (TextView) itemView.findViewById(R.id.leader_position);
            card = (CardView) itemView.findViewById(R.id.rating_card);
            icon = (ImageView) itemView.findViewById(R.id.rating_icon);
        }
    }

    /*public void setFilter(ArrayList<LeaderData> leaderDatas) {
        list = new ArrayList<>();
        list.addAll(leaderDatas);
        notifyDataSetChanged();
    }*/
}