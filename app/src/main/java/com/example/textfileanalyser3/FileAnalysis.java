package com.example.textfileanalyser3;

import static android.provider.MediaStore.Files.FileColumns.PARENT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Anchor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FileAnalysis extends AppCompatActivity{

    private Singleton finalFile;
    private TextView fileName;
    private Button backButton;
    private TextView wordCount;
    private TextView sentenceCount;
    private TextView uniqueTextView;
    private TextView total;
    private AnyChartView pieChart;
    private AnyChartView barChart;

    private ScrollView parentScroll;
    private ScrollView childScroll;
    private List<DataEntry> dataEntries1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_file_analysis);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        finalFile = Singleton.getInstance();
        fileName = (TextView) findViewById(R.id.fileNameView);
        backButton = (Button) findViewById(R.id.backButton2);
        wordCount = (TextView) findViewById(R.id.wordCount);
        sentenceCount = (TextView) findViewById(R.id.sentenceCount);
        uniqueTextView = (TextView) findViewById(R.id.uniqueText);
        total = (TextView) findViewById(R.id.total);
        pieChart = (AnyChartView) findViewById(R.id.pieChart);
        barChart = (AnyChartView) findViewById(R.id.barChart);

        parentScroll=(ScrollView)findViewById(R.id.parent_scroll);
        childScroll=(ScrollView)findViewById(R.id.child_scroll);

        fileName.setText(finalFile.getFileName());
        wordCount.setText("Word Count: " + finalFile.getWordCount());

        if(finalFile.getFileType().equals("Text File")){
            sentenceCount.setText("Sentence Count: " + finalFile.getSentenceCnt());
        }else{
            sentenceCount.setText("Page Count: " + finalFile.getSentenceCnt());
        }
        uniqueTextView.setText(finalFile.getUniqueText());
        total.setText("Total: " + finalFile.getUnqiueWordCount() + " words");

        dataEntries1 = finalFile.data();
        setUpChartView();
        setUpTop5();
        setUpBarChart();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileAnalysis.this,MenuPage.class);
                startActivity(intent);
            }
        });

        //disables the scrolling event for the child scrollable controls
        parentScroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("TAG","PARENT TOUCH");
                findViewById(R.id.child_scroll).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        //disables the scroll event of parent scroll view if the user touches on child scroll view
        childScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("TAG","CHILD TOUCH");
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }

    private void setUpTop5() {
        Map sortedMap = finalFile.getSorted();
        Set set = sortedMap.entrySet();
        Iterator i = set.iterator();

        int cnt=1;
        while (i.hasNext() && cnt<=5) {
            Map.Entry mp = (Map.Entry)i.next();
            TextView currentBox = findViewById(R.id.firstWord);
            if(cnt==2){
                currentBox = findViewById(R.id.secondWord);
            }else if(cnt==3){
                currentBox = findViewById(R.id.thirdWord);
            }else if(cnt==4){
                currentBox = findViewById(R.id.fourthWord);
            }else if(cnt==5){
                currentBox = findViewById(R.id.fifthWord);
            }

            String text = cnt + ": " + mp.getKey() + ", " + mp.getValue() + " times";
            currentBox.setText(text);

            cnt++;

        }
    }

    private void setUpChartView() {
        APIlib.getInstance().setActiveAnyChartView(pieChart);
        Pie pie = AnyChart.pie();
        pie.data(dataEntries1);
        pie.title("");
        pieChart.setChart(pie);
    }

    private void setUpBarChart(){
        APIlib.getInstance().setActiveAnyChartView(barChart);

        barChart.setProgressBar(findViewById(R.id.progress_bar));
        Cartesian cartesian = AnyChart.column();
        Column column = cartesian.column(dataEntries1);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(String.valueOf(Anchor.CENTER_BOTTOM))
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");
        cartesian.animation(true);
        cartesian.title("Word Frequency");

        cartesian.yScale().minimum(0d);

        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Word");
        cartesian.yAxis(0).title("Frequency");

        barChart.setChart(cartesian);
    }
}