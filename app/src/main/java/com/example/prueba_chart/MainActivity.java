package com.example.prueba_chart;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineChart chart = (LineChart) findViewById(R.id.chart);

        DatoMQTT[] dataObjects = new DatoMQTT[10];
        Random rand = new Random();
        // Llenar el array con valores aleatorios
        for (int i = 0; i < dataObjects.length; i++) {
            float randomX = rand.nextFloat() * 100; // Número entre 0 y 100
            float randomY = rand.nextFloat() * 100;
            dataObjects[i] = new DatoMQTT(randomX, randomY);
        }

        List<Entry> entries = new ArrayList<Entry>();
        for (DatoMQTT data : dataObjects) {
            // turn your data into Entry objects
            if (data != null) {
                entries.add(new Entry(data.getValueX(),data.getValueY()));
            } else {
                Log.e("Error", "El objeto datos[0] es null");
            }
            entries.add(new Entry(data.getValueX(),data.getValueY()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

    }
}