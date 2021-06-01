package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv1.R;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.MyViewHolder> {

    String data1[], data2[], date[];
    Integer[] images;
    Context context;
    private OnNoteListener mOnNoteListener;

    public NotificationRecyclerAdapter(Context context, String[] c1, String[] c2, String[] date,  Integer[] img, OnNoteListener onNoteListener)
    {
        this.context = context;
        this.data1 = c1;
        this.data2 = c2;
        this.images = img;
        this.date = date;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_row, parent, false);

        return new MyViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.myText1.setText(data1[position]);
        holder.myText2.setText(data2[position]);
        holder.myDate.setText(date[position]);
        holder.image.setImageResource(images[position]);

    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myText1, myText2, myDate;
        ImageView image;
        OnNoteListener onNoteListener;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            myText1 = itemView.findViewById(R.id.notificationMain);
            myText2 = itemView.findViewById(R.id.notificationSecond);
            myDate = itemView.findViewById(R.id.notificationDate);
            image = itemView.findViewById(R.id.notificationImage);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
