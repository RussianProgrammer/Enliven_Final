package sis.pewpew.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sis.pewpew.R;
import sis.pewpew.additions.ProposedMarkerInfoActivity;

public class MarkerModerationRecyclerViewAdapter extends RecyclerView.Adapter<MarkerModerationRecyclerViewAdapter.ViewHolder> {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Context context;
    private String id;

    public MarkerModerationRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView slotImage;
        TextView slotStatus;
        TextView slotDetails;
        CardView slotCard;

        ViewHolder(final View itemView) {
            super(itemView);

            slotImage = (ImageView) itemView.findViewById(R.id.marker_moderation_slot_icon);
            slotStatus = (TextView) itemView.findViewById(R.id.marker_moderation_slot_status);
            slotDetails = (TextView) itemView.findViewById(R.id.marker_moderation_slot_details);
            slotCard = (CardView) itemView.findViewById(R.id.marker_moderation_slot_card);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAvailability(getAdapterPosition() + 1);
                }
            });
        }
    }

    @Override
    public MarkerModerationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.marker_moderation_slot_card_layout, viewGroup, false);
        return new MarkerModerationRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MarkerModerationRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        final int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {

            mDatabase.keepSynced(true);

            ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String[] status = new String[10];
                    String[] details = new String[10];
                    String[] colors = new String[10];
                    int[] ids = new int[10];

                    if (dataSnapshot.child("requests").child("1").getChildrenCount() != 0) {
                        status[0] = "Запрос";
                        details[0] = "Слот " + 1 + " ожидает проверки";
                        colors[0] = "#FF5722";
                        ids[0] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[0] = "Пусто";
                        details[0] = "Слот " + 1 + " пуст";
                        colors[0] = "#616161";
                        ids[0] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("2").getChildrenCount() != 0) {
                        status[1] = "Запрос";
                        details[1] = "Слот " + 2 + " ожидает проверки";
                        colors[1] = "#FF5722";
                        ids[1] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[1] = "Пусто";
                        details[1] = "Слот " + 2 + " пуст";
                        colors[1] = "#616161";
                        ids[1] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("3").getChildrenCount() != 0) {
                        status[2] = "Запрос";
                        details[2] = "Слот " + 3 + " ожидает проверки";
                        colors[2] = "#FF5722";
                        ids[2] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[2] = "Пусто";
                        details[2] = "Слот " + 3 + " пуст";
                        colors[2] = "#616161";
                        ids[2] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("4").getChildrenCount() != 0) {
                        status[3] = "Запрос";
                        details[3] = "Слот " + 4 + " ожидает проверки";
                        colors[3] = "#FF5722";
                        ids[3] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[3] = "Пусто";
                        details[3] = "Слот " + 4 + " пуст";
                        colors[3] = "#616161";
                        ids[3] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("5").getChildrenCount() != 0) {
                        status[4] = "Запрос";
                        details[4] = "Слот " + 5 + " ожидает проверки";
                        colors[4] = "#FF5722";
                        ids[4] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[4] = "Пусто";
                        details[4] = "Слот " + 5 + " пуст";
                        colors[4] = "#616161";
                        ids[4] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("6").getChildrenCount() != 0) {
                        status[5] = "Запрос";
                        details[5] = "Слот " + 6 + " ожидает проверки";
                        colors[5] = "#FF5722";
                        ids[5] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[5] = "Пусто";
                        details[5] = "Слот " + 6 + " пуст";
                        colors[5] = "#616161";
                        ids[5] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("7").getChildrenCount() != 0) {
                        status[6] = "Запрос";
                        details[6] = "Слот " + 7 + " ожидает проверки";
                        colors[6] = "#FF5722";
                        ids[6] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[6] = "Пусто";
                        details[6] = "Слот " + 7 + " пуст";
                        colors[6] = "#616161";
                        ids[6] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("8").getChildrenCount() != 0) {
                        status[7] = "Запрос";
                        details[7] = "Слот " + 8 + " ожидает проверки";
                        colors[7] = "#FF5722";
                        ids[7] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[7] = "Пусто";
                        details[7] = "Слот " + 8 + " пуст";
                        colors[7] = "#616161";
                        ids[7] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("9").getChildrenCount() != 0) {
                        status[8] = "Запрос";
                        details[8] = "Слот " + 9 + " ожидает проверки";
                        colors[8] = "#FF5722";
                        ids[8] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[8] = "Пусто";
                        details[8] = "Слот " + 9 + " пуст";
                        colors[8] = "#616161";
                        ids[8] = R.drawable.marker_moderation_empty_slot_icon;
                    }
                    if (dataSnapshot.child("requests").child("10").getChildrenCount() != 0) {
                        status[9] = "Запрос";
                        details[9] = "Слот " + 10 + " ожидает проверки";
                        colors[9] = "#FF5722";
                        ids[9] = R.drawable.marker_moderation_full_slot_icon;
                    } else {
                        status[9] = "Пусто";
                        details[9] = "Слот " + 10 + " пуст";
                        colors[9] = "#616161";
                        ids[9] = R.drawable.marker_moderation_empty_slot_icon;
                    }

                    viewHolder.slotStatus.setText(status[position]);
                    viewHolder.slotDetails.setText(details[position]);
                    viewHolder.slotImage.setImageResource(ids[position]);
                    viewHolder.slotCard.setCardBackgroundColor(Color.parseColor(colors[position]));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabase.addValueEventListener(postListener1);
        }
    }

    private void checkAvailability(final int position) {
        ValueEventListener postListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("requests").child("" + position).getChildrenCount() != 0) {
                    AlertDialog.Builder moderationRequestDialog = new AlertDialog.Builder(context);
                    moderationRequestDialog.setTitle("Слот " + position);
                    moderationRequestDialog.setMessage("Выберите, что нужно сделать с этим слотом.");
                    moderationRequestDialog.setNeutralButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).setPositiveButton("Просмотреть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            id = dataSnapshot.child("requests").child("" + position).getKey();
                            Intent intent = new Intent(context, ProposedMarkerInfoActivity.class);
                            intent.putExtra("ID", id);
                            context.startActivity(intent);
                        }
                    }).setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDatabase.child("requests").child("" + position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Слот успешно очищен", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    moderationRequestDialog.show();
                } else {
                    Toast.makeText(context, "Слот пуст", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener1);
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}