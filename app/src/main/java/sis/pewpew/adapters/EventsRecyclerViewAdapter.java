package sis.pewpew.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sis.pewpew.R;

public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private Context context;

    public EventsRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    private String[] titles = {"Чистые игры",
            "Лекция про РСО",
            "Велопробег"
    };

    private String[] details = {"Мероприятие в рамках ВузЭкоФест 2017. " +
            "Проводит председатель студсовета факультета ЭиЕБ Богомолова А.",
            "Мероприятие в рамках ВузЭкоФест 2017. Лектор: Королева Валерия, организатор движения " +
                    "Раздельный Сбор в Москве. Вводная про экологический образ жизни и ресурсопотребление.",
            "Мероприятие в рамках ВузЭкоФест 2017. Участники проедутся по городу с целью " +
                    "показать важность развития велосипедной структуры."
    };

    private int[] images = {R.drawable.vuzecofest_moscow_icon,
            R.drawable.vuzecofest_moscow_icon,
            R.drawable.vuzecofest_moscow_icon,
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemDetail;
        Button shareButton;

        ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDetail = (TextView) itemView.findViewById(R.id.item_detail);
            shareButton = (Button) itemView.findViewById(R.id.event_share_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final android.app.AlertDialog.Builder eventCardDialog = new android.app.AlertDialog.Builder(context);
                    eventCardDialog.setTitle("Карточка события");
                    eventCardDialog.setMessage("Чтобы просмотреть более детальную информацию об этом событии, " +
                            "коснитесь его информационного окна на карте.");
                    eventCardDialog.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    eventCardDialog.show();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.events_card_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
        viewHolder.itemDetail.setText(details[i]);
        viewHolder.itemImage.setImageResource(images[i]);
        final String title = titles[i];
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Событие: " + title + "https://play.google.com/apps/testing/sis.pewpew";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться событием"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}