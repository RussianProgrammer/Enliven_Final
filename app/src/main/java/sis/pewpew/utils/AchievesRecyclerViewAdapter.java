package sis.pewpew.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sis.pewpew.R;

import static com.google.android.gms.internal.zzt.TAG;

public class AchievesRecyclerViewAdapter extends RecyclerView.Adapter<AchievesRecyclerViewAdapter.ViewHolder> {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private long points;
    private String dateFromDatabase;
    private Context context;

    public AchievesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    private String[] titles = new String[]{
            "Мангостин", "Дуриан", "Купуасу",
            "Тамаринд", "Цитрон", "Жаботикаба",
            "Рамбутан", "Лонган", "Карамбола",
            "Питайя", "Гуава", "Чомпу",
            "Салак", "Баиль", "Кивано"};

    private int[] limits = new int[]{
            100, 600, 1100, 1600, 2100,
            2600, 3100, 3600, 4100, 4600,
            5100, 5600, 6100, 6600, 7100};

    private int[] ids = new int[15];

    {
        for (int i = 0; i < 15; i++) {
            ids[i] = i;
        }

    }

    private final int[] iconIds = new int[]{
            R.drawable.achieve_1_icon, R.drawable.achieve_2_icon,
            R.drawable.achieve_3_icon, R.drawable.achieve_4_icon,
            R.drawable.achieve_5_icon, R.drawable.achieve_6_icon,
            R.drawable.achieve_7_icon, R.drawable.achieve_8_icon,
            R.drawable.achieve_9_icon, R.drawable.achieve_10_icon,
            R.drawable.achieve_11_icon, R.drawable.achieve_12_icon,
            R.drawable.profile_saved_trees_icon, R.drawable.profile_saved_trees_icon,
            R.drawable.profile_saved_trees_icon,
    };

    private final String[] colors1 = new String[]{
            "#262427", "#979329", "#705544",
            "#E3B06E", "#E0A81D", "#440C1A",
            "#D00624", "#DCB141", "#DDD705",
            "#DD0F3A", "#778F03", "#F95754",
            "#AD7568", "#C6A59A", "#EA8E04"
    };

    private final String[] colors2 = new String[]{
            "#8B5D5B", "#D7E5ED", "#B09F83",
            "#996A4C", "#36680A", "#6F3C40",
            "#306026", "#A59A8D", "#859F03",
            "#D7E5ED", "#F45F3D", "#32630F",
            "#30130B", "#6F986C", "#5D6E07"
    };

    private final String[] colors3 = new String[]{
            "#D7E5ED", "#F1C919", "#D7E5ED",
            "#311E18", "#6B3D12", "#6E5242",
            "#D7E5ED", "#510803", "#BE8500",
            "#111613", "#FFA366", "#A30002",
            "#FEA961", "#D7E5ED", "#E0CE0B"
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView achieveImage;
        TextView achieveTitle;
        TextView achieveProgress;
        CardView achieveCard;

        ViewHolder(final View itemView) {
            super(itemView);
            achieveImage = (ImageView) itemView.findViewById(R.id.achieve_icon);
            achieveTitle = (TextView) itemView.findViewById(R.id.achieve_title);
            achieveProgress = (TextView) itemView.findViewById(R.id.achieve_progress);
            achieveCard = (CardView) itemView.findViewById(R.id.achieve_card);
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {
                        points = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();
                    } else {
                        points = 0;
                    }
                    if (dataSnapshot.child("users").child(user.getUid())
                            .child("achievements").child("" + (getAdapterPosition() + 1)).getValue() != null) {
                        dateFromDatabase = dataSnapshot.child("users").child(user.getUid())
                                .child("achievements").child("" + (getAdapterPosition() + 1)).getValue().toString();
                    } else {
                        dateFromDatabase = "Когда-то";
                    }
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = getAdapterPosition();
                            if (points >= limits[position]) {
                                Intent intent = new Intent(context, AchievementInfoActivity.class);
                                intent.putExtra("TITLE", titles[position]);
                                intent.putExtra("ID", (position + 1));
                                intent.putExtra("COLOR1", colors1[position]);
                                intent.putExtra("COLOR2", colors2[position]);
                                intent.putExtra("COLOR3", colors3[position]);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Чтобы открыть это достижение, " +
                                        "необходимо заработать еще " + (limits[position] - points) + " очков.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabase.addValueEventListener(postListener);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.achieve_card_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final AchievesRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        final int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {

            ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("users").child(user.getUid()).child("points").getValue() != null) {
                        points = (long) dataSnapshot.child("users").child(user.getUid()).child("points").getValue();
                    } else {
                        points = 0;
                    }

                    if (points >= limits[position]) {
                        viewHolder.achieveCard.setCardBackgroundColor(Color.parseColor(colors1[position]));
                        viewHolder.achieveTitle.setText(titles[position]);
                        if (dataSnapshot.child("users").child(user.getUid())
                                .child("achievements").child("" + (position + 1)).getValue() != null) {
                            dateFromDatabase = dataSnapshot.child("users").child(user.getUid())
                                    .child("achievements").child("" + (position + 1)).getValue().toString();
                        } else {
                            dateFromDatabase = "когда-то";
                        }
                        viewHolder.achieveProgress.setText("Получено " + dateFromDatabase);
                        viewHolder.achieveImage.setImageResource(iconIds[position]);
                    } else {
                        viewHolder.achieveProgress.setText("Выполнено на " + points * 100 / limits[position] + "%");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabase.addValueEventListener(postListener1);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
