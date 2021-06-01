package com.example.medicalappv1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class AnalyseChart extends AppCompatActivity {

    LineChartView lineChartView;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String patientID, nomAnalyse, valeurUnite, unite, prelevementID, otherAnalyse;

    ArrayList<String> analyseValuesY = new ArrayList<>();
    ArrayList<String> dateValueStringX = new ArrayList<>();
    ArrayList<Float> analyseValuesFloat = new ArrayList<>();
    ArrayList<String> analyseData = new ArrayList<>();

    ArrayList<String> allAnalyses = new ArrayList<>();

    TextView nomAnalyseTV, valeurTV, datePrelevement;
    Float normeMax, normeMin;
    Float topValueGraph, bottomValueGraph;

    Spinner analyseSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyse_chart);

        nomAnalyseTV = findViewById(R.id.nomAnalyseChart);
        valeurTV = findViewById(R.id.resultatChart);
        datePrelevement = findViewById(R.id.datePrelevement);
        analyseSpinner = findViewById(R.id.analyseSpinner);

        Intent intent = getIntent();
        final String datePrelevmentIntent = intent.getStringExtra("DatePrelevement");
        final String datePrelevementStr = "Prélévement du : "+ intent.getStringExtra("DatePrelevement");
        datePrelevement.setText(datePrelevementStr);
        patientID = intent.getStringExtra("patientID");
        nomAnalyse = intent.getStringExtra("nomAnalyse");
        prelevementID = intent.getStringExtra("PrelevementID");

        fStore.collection("Patients")
                .document(patientID)
                .collection("Prelevement")
                .document(prelevementID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            Map<String, Object> allData = document.getData();

                            allData.remove("Date");
                            allData.remove("ID_Labo");
                            allData.remove("ID_Medecin");
                            allData.remove("Seen");
                            allData.remove("HasAudio");
                            allData.remove("HasRapport");
                            allData.remove("Interpretation");

                            allAnalyses.add(nomAnalyse);
                            for (String key : allData.keySet()) {
                                analyseData = (ArrayList<String>) allData.get(key);
                                otherAnalyse = analyseData.get(0);
                                if (!allAnalyses.contains(otherAnalyse))
                                    allAnalyses.add(otherAnalyse);
                            }
                        }
                        ArrayAdapter<String> arrayAdaptereUnit = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allAnalyses);
                        arrayAdaptereUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        analyseSpinner.setAdapter(arrayAdaptereUnit);
                    }
                });

        fStore.collection("Analyses")
                .document(nomAnalyse)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            normeMax = Float.parseFloat((String) document.get("NormeMax"));
                            normeMin = Float.parseFloat((String) document.get("NormeMin"));
                        }
                        fStore.collection("Patients")
                                .document(patientID)
                                .collection("Prelevement")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.contains(nomAnalyse)) {
                                                    ArrayList<String> temp;
                                                    temp = (ArrayList<String>) document.get(nomAnalyse);
                                                    analyseValuesY.add(temp.get(1));
                                                    unite = temp.get(2);
                                                    String temp2 = (String) document.get("Date");
                                                    dateValueStringX.add(temp2);
                                                }
                                            }

                                            //Setting the dates and sorting them
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                                            Date[] arrayOfDates = new Date[dateValueStringX.size()];
                                            for (int i = 0; i< dateValueStringX.size(); i++) {
                                                try {
                                                    Date date = formatter.parse(dateValueStringX.get(i));
                                                    arrayOfDates[i] = date;
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            Arrays.sort(arrayOfDates);
                                            for(int i = 0; i< dateValueStringX.size(); i++) {
                                                dateValueStringX.set(i, formatter.format(arrayOfDates[i]));
                                            }

                                            //Setting up Y axis and X axis
                                            String[] dateArrayX = new String[dateValueStringX.size()];
                                            for (int i=0; i<dateValueStringX.size(); i++) {
                                                dateArrayX[i] = dateValueStringX.get(i);
                                            }
                                            Float[] analyseArrayY = new Float[analyseValuesY.size()];
                                            for (int i=0; i<analyseValuesY.size(); i++) {
                                                analyseValuesFloat.add(Float.parseFloat(analyseValuesY.get(i)));
                                                analyseArrayY[i] = analyseValuesFloat.get(i).floatValue();
                                                //Log.i("value is", String.valueOf(analyseArrayY[i]));
                                                //Log.i("date is", dateArrayX[i]);
                                            }

                                            lineChartView = findViewById(R.id.chart);

                                            Log.i("norme min", String.valueOf(normeMin));
                                            Log.i("norme max", String.valueOf(normeMax));

                                            List normeMaxValue = new ArrayList();
                                            Line normeMaxLine = new Line(normeMaxValue).setColor(Color.parseColor("#FA5C55"));
                                            normeMaxValue.add(new PointValue(0, normeMax));
                                            normeMaxValue.add(new PointValue(analyseArrayY.length - 1, normeMax));

                                            List normeMinValue = new ArrayList();
                                            Line normeMinLine = new Line(normeMinValue).setColor(Color.parseColor("#FA5C55"));
                                            normeMinValue.add(new PointValue(0, normeMin));
                                            normeMinValue.add(new PointValue(analyseArrayY.length - 1, normeMin));

                                            List yAxisValues = new ArrayList();
                                            List axisValues = new ArrayList();

                                            Line line = new Line(yAxisValues).setColor(Color.parseColor("#3CA4FA"));

                                            for (int i = 0; i < dateArrayX.length; i++) {
                                                axisValues.add(i, new AxisValue(i).setLabel(dateArrayX[i]));
                                            }

                                            for (int i = 0; i < analyseArrayY.length; i++) {
                                                yAxisValues.add(new PointValue(i, analyseArrayY[i]));
                                            }

                                            List lines = new ArrayList();
                                            lines.add(line);
                                            lines.add(normeMaxLine);
                                            lines.add(normeMinLine);

                                            LineChartData data = new LineChartData();
                                            data.setLines(lines);

                                            Axis axis = new Axis();
                                            axis.setValues(axisValues);
                                            axis.setTextSize(10);
                                            axis.setTextColor(Color.parseColor("#03A9F4"));
                                            data.setAxisXBottom(axis);

                                            Axis yAxis = new Axis();
                                            yAxis.setName("  ");
                                            yAxis.setTextColor(Color.parseColor("#03A9F4"));
                                            yAxis.setTextSize(12);
                                            data.setAxisYLeft(yAxis);

                                            lineChartView.setLineChartData(data);
                                            Viewport viewport = new Viewport(lineChartView.getMaximumViewport());

                                            if (Collections.max(analyseValuesFloat) > normeMax)
                                                topValueGraph = Collections.max(analyseValuesFloat);
                                            else
                                                topValueGraph = normeMax;

                                            if (Collections.min(analyseValuesFloat) < normeMin)
                                                bottomValueGraph = Collections.min(analyseValuesFloat);
                                            else
                                                bottomValueGraph = normeMin;

                                            viewport.top = Math.round(topValueGraph) + 1;
                                            viewport.bottom = Math.round(bottomValueGraph) - 1;
                                            viewport.right = viewport.right + 0.1f;
                                            viewport.left = viewport.left - 0.1f;
                                            lineChartView.setMaximumViewport(viewport);
                                            lineChartView.setCurrentViewport(viewport);

                                            nomAnalyseTV.setText(nomAnalyse);
                                            valeurUnite = analyseValuesFloat.get(analyseValuesFloat.size() - 1) + " " + unite;
                                            valeurTV.setText(valeurUnite);
                                        }
                                    }
                                });
                    }
                });


        analyseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getItemAtPosition(position) == nomAnalyse)) {
                    Intent intent = new Intent(getApplicationContext(), AnalyseChart.class);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("nomAnalyse", analyseSpinner.getSelectedItem().toString());
                    intent.putExtra("DatePrelevement", datePrelevmentIntent );
                    intent.putExtra("PrelevementID", prelevementID);
                    startActivity(intent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
