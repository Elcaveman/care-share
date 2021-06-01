package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalappv1.Analyses;
import com.example.medicalappv1.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Analyses> analyses;
    private LineChart mChart;

    public RecyclerViewAdapter(ArrayList<String> mNames, ArrayList<Analyses> analyses, Context mContext) {
        this.mNames = mNames;
        this.analyses = analyses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(mNames.get(position));

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView medName = (TextView) v.getRootView().findViewById(R.id.val_examen_text_2);
                medName.setText(mNames.get(position));

                mChart = (LineChart) v.getRootView().findViewById(R.id.linechart);
                mChart.invalidate();
                mChart.clear();
                ArrayList<Entry> yValues = new ArrayList<>();
                yValues.add(analyses.get(position).getCoord().get(0));
                yValues.add(analyses.get(position).getCoord().get(1));
                yValues.add(analyses.get(position).getCoord().get(2));
                LineDataSet set1 = new LineDataSet(yValues, "DataSet1");
                set1.setFillAlpha(110);
                LineData data = new LineData(set1);
                mChart.setData(data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
        }
    }
}
