package sis.pewpew.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.text.DecimalFormat;

import sis.pewpew.R;

public class ProgressRecyclerViewAdapter extends RecyclerView.Adapter<ProgressRecyclerViewAdapter.ViewHolder> {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DecimalFormat numberFormat = new DecimalFormat("0.0000");
    private long users;
    private long points;
    private long timesUsed;
    private double balance;
    private Context context;

    public ProgressRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    private String[] titles = new String[]{
            "Всего участников", "Очков собрано", "Экопунктов использовано", "Баланс восстановлен на",
            "Деревьев спасено", "Животных спасено", "Людей спасено"};

    private final int[] iconIds = new int[]{
            R.drawable.progress_users_icon, R.drawable.progress_points_icon,
            R.drawable.progress_used_icon,
            R.drawable.progress_balance_icon, R.drawable.progress_saved_trees_icon,
            R.drawable.progress_saved_animals_icon, R.drawable.progress_saved_people_icon};

    private final String[] colors = new String[]{
            "#262427", "#338782", "#339944", "#705544",
            "#1B5E20", "#E0A81D", "#440C1A"};

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView progressImage;
        TextView progressTitle;
        TextView progressNumber;
        CardView progressCard;

        ViewHolder(final View itemView) {
            super(itemView);

            progressImage = (ImageView) itemView.findViewById(R.id.progress_icon);
            progressTitle = (TextView) itemView.findViewById(R.id.progress_title);
            progressNumber = (TextView) itemView.findViewById(R.id.progress_number);
            progressCard = (CardView) itemView.findViewById(R.id.progress_card);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getAdapterPosition()) {
                        case 0:
                            showProgressDialog("Карточка участников", "Люди, окрыленные одной и той же мечтой," +
                                    " должны быть вместе. Именно поэтому мы собрали " +
                                    "всех участников нашего проекта здесь.");
                            break;
                        case 1:
                            showProgressDialog("Карточка общих очков", "Здесь показано, " +
                                    "сколько всего очков собрано усилиями нашего сообщества.");
                            break;
                        case 2:
                            showProgressDialog("Карточка использованных экопунктов", "Здесь " +
                                    "отображено суммарное количество экопунктов, использованных " +
                                    "всеми участниками сообщества. Планета улыбается каждой новой цифре. " +
                                    "Давайте заставим ее смеяться от счастья?");
                            break;
                        case 3:
                            showProgressDialog("Карточка баланса", "По ходу того, как все участники" +
                                    " проекта зарабывают очки, используя экопункты, " +
                                    "мы все ближе к экологичному будущему с чистым воздухом и землей, " +
                                    "к здоровым животным и растениям, к людям с долгой жизнью. " +
                                    "Когда-нибудь мы достигнем отметки в 100%, и тогда все изменится.");
                            break;
                        case 4:
                            showProgressDialog("Карточка спасенных деревьев", "Собирая очки в приложении," +
                                    " Вы спасаете настоящие деревья в будущем. Со временем мы спасем " +
                                    "целые леса.");
                            break;
                        case 5:
                            showProgressDialog("Карточка спасенных животных", "Собирая очки в приложении," +
                                    " Вы спасаете настоящих животных в будущем. " +
                                    "Они никак не могут повлиять на катострофу, от которой погибают. " +
                                    "Но мы — можем.");
                            break;
                        case 6:
                            showProgressDialog("Карточка спасенных людей", "Собирая очки в приложении," +
                                    " Вы спасаете настоящих людей в будущем. Все верно, мы настоящие " +
                                    "супергерои. Миллионы человек умирают ежегодно от плохой экологии. " +
                                    "Пора все менять.");
                            break;
                    }
                }
            });
        }
    }

    @Override
    public ProgressRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.progress_card_layout, viewGroup, false);
        return new ProgressRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ProgressRecyclerViewAdapter.ViewHolder viewHolder, int i) {

        final int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {

            mDatabase.keepSynced(true);

            ValueEventListener postListener1 = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String[] data = new String[7];

                    if (dataSnapshot.child("progress").child("points").getValue() != null
                            && dataSnapshot.child("users").getChildren() != null) {
                        users = dataSnapshot.child("users").getChildrenCount();
                        points = (long) dataSnapshot.child("progress").child("points").getValue();
                        timesUsed = (long) dataSnapshot.child("progress").child("timesUsed").getValue();
                        balance = (double) points * 100 / 300000;
                        data[0] = "" + users;
                        data[1] = "" + points;
                        data[2] = "" + timesUsed;
                        data[3] = numberFormat.format(balance / 1000) + "%";
                        data[4] = "" + points / 500;
                        data[5] = "" + points / 1000;
                        data[6] = "" + points / 1200;
                    } else {
                        data[0] = "0";
                        data[1] = "0";
                        data[2] = "0";
                        data[3] = "0%";
                        data[4] = "0";
                        data[5] = "0";
                        data[6] = "0";
                    }

                    viewHolder.progressTitle.setText(titles[position]);
                    viewHolder.progressNumber.setText(data[position]);
                    viewHolder.progressImage.setImageResource(iconIds[position]);
                    viewHolder.progressCard.setCardBackgroundColor(Color.parseColor(colors[position]));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                }
            };
            mDatabase.addValueEventListener(postListener1);
        }
    }

    private void showProgressDialog(String titleText, String messageText) {
        AlertDialog.Builder progressDialog = new AlertDialog.Builder(context);
        progressDialog.setTitle(titleText);
        progressDialog.setMessage(messageText);
        progressDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        progressDialog.show();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}