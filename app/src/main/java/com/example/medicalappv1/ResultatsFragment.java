package com.example.medicalappv1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import Adapters.RecyclerViewAdapter;
import firebase.FirebaseUtils;

public class ResultatsFragment extends Fragment {



    private ArrayList<String> mNames = new ArrayList<>();
    private LineChart mChart;
    ArrayList<Analyses> analyses = new ArrayList<>();
    FirebaseUtils firebaseUtils;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resultats, container, false);
        firebaseUtils = new FirebaseUtils();
        userID = fAuth.getCurrentUser().getUid();
        //Setting the recyclerView
        mNames = firebaseUtils.getAnalyseNames(userID);


        /*ArrayList<Entry> entry_acide_urique = new ArrayList<>();
        entry_acide_urique.add(new Entry(0,20));
        entry_acide_urique.add(new Entry(1,10));
        entry_acide_urique.add(new Entry(3,50));
        Analyses acide_urique = new Analyses("Acide Urique", entry_acide_urique);
        analyses.add(acide_urique);

        ArrayList<Entry> entry_fibrinogene = new ArrayList<>();
        entry_fibrinogene.add(new Entry(0,5));
        entry_fibrinogene.add(new Entry(1,5.2f));
        entry_fibrinogene.add(new Entry(3,4.2f));
        Analyses fibrinogene = new Analyses("fibronigene", entry_fibrinogene);
        analyses.add(fibrinogene);


        mNames.add(acide_urique.getName());
        mNames.add(fibrinogene.getName());
        mNames.add("Albumine");
        mNames.add("Hémoglobine Glyquée");
        mNames.add("Mycoplasmes génitaux");*/

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        initRecyclerView(recyclerView);

        getActivity().setTitle("Mon suivi biologique");

        //Setting the graph

        mChart = (LineChart) view.findViewById(R.id.linechart);
        /*XAxis xAxis = mChart.getXAxis();
        final String[] quarters = new String[] { "25/11/19", "26/11/19", "27/11/19", "28/11/19" };
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return quarters[(int) value];
            }
        });*/

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        /*ArrayList<Entry> yValues = new ArrayList<>();

        yValues.add(new Entry(0,60f));
        yValues.add(new Entry(1,40f));
        yValues.add(new Entry(2,50f));

        LineDataSet set1 = new LineDataSet(yValues, "DataSet1");
        set1.setFillAlpha(110);
        //ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //dataSets.add(set1);

        LineData data = new LineData(set1);
        mChart.setData(data);*/



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initRecyclerView (RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, analyses, getActivity());
        recyclerView.setAdapter(adapter);
    }

}