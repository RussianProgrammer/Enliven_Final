package sis.pewpew.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sis.pewpew.R;
import sis.pewpew.support.TrainingActivity;

public class TrainingRecyclerViewAdapter extends RecyclerView.Adapter<TrainingRecyclerViewAdapter.ViewHolder> {

    private Context context;

    public TrainingRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    private String[] titles = new String[]{
            "Курс 1", "Курс 2"};

    private String[] descriptions = new String[]{
            "Как посадить дерево", "Как уменьшить свой экослед"};

    private final int[] iconIds = new int[]{
            R.drawable.training_1_icon, R.drawable.training_2_icon};

    private final String[] colors = new String[]{
            "#1B5E20", "#5D4037"};

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView trainingImage;
        TextView trainingTitle;
        TextView trainingDescription;
        CardView trainingCard;

        ViewHolder(final View itemView) {
            super(itemView);

            trainingImage = (ImageView) itemView.findViewById(R.id.training_icon);
            trainingTitle = (TextView) itemView.findViewById(R.id.training_title);
            trainingDescription = (TextView) itemView.findViewById(R.id.training_desc);
            trainingCard = (CardView) itemView.findViewById(R.id.training_card);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TrainingActivity.class);
                    intent.putExtra("TITLE", descriptions[getAdapterPosition()]);
                    intent.putExtra("ID", (getAdapterPosition() + 1));
                    intent.putExtra("COLOR", colors[getAdapterPosition()]);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public TrainingRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.training_card_layout, viewGroup, false);
        return new TrainingRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final TrainingRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        final int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            viewHolder.trainingTitle.setText(titles[position]);
            viewHolder.trainingDescription.setText(descriptions[position]);
            viewHolder.trainingImage.setImageResource(iconIds[position]);
            viewHolder.trainingCard.setCardBackgroundColor(Color.parseColor(colors[position]));
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}