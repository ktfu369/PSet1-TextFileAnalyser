package com.example.textfileanalyser3;

import android.content.Intent;
import android.os.Bundle;
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

public class MenuPage extends AppCompatActivity {
    private Singleton finalFile;
    private TextView fileName;
    private Button backButton;
    private ScrollView scrollBox;
    private Button analysisButton;
    private Button randomParaButton;
    private TextView paragraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        finalFile = Singleton.getInstance();
        fileName = (TextView) findViewById(R.id.fileNameView);
        backButton = (Button) findViewById(R.id.backButton1);
        scrollBox = (ScrollView) findViewById(R.id.scrollBox1);
        analysisButton = (Button) findViewById(R.id.analysisButton);
        randomParaButton = (Button) findViewById(R.id.randomParagraphButton);
        paragraph = (TextView) findViewById(R.id.paragraph);

        fileName.setText(finalFile.getFileName());
        paragraph.setText(finalFile.getWholeText());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalFile.reset();
                Intent intent = new Intent(MenuPage.this,MainActivity.class); // switch to new page
                startActivity(intent);
            }
        });

        analysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPage.this,FileAnalysis.class);
                startActivity(intent);
            }
        });
    }


}