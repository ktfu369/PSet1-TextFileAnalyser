package com.example.textfileanalyser3;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private EditText fileName;
    private Spinner fileType;
    private Button continueBtn;
    private TextView errorMessage;
    private Singleton finalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        finalFile = Singleton.getInstance();
        fileName = (EditText) findViewById(R.id.editTextText);
        fileType = (Spinner) findViewById(R.id.spinner);
        continueBtn = (Button) findViewById(R.id.button);
        errorMessage = (TextView) findViewById(R.id.textView2);

        try {
            readCommonWords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileNam = (fileName.getText().toString());
                String fileTyp = fileType.getSelectedItem().toString();

                finalFile.setFileType(fileTyp);
                if(fileTyp.equals("Text File")){
                    readTxt(fileNam);
                }else{
                    readPdf(fileNam);

                }

            }
        });

    }

    private void readCommonWords() throws IOException {
        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open("commonWords.txt");
        Scanner input = new Scanner(is);
        input.useDelimiter("[\\n ]+");

        while(input.hasNext()){
            String word = input.next();
            finalFile.updateCommonWords(word);
        }
        input.close(); // Don't forget to close the Scanner
        is.close(); // Close the InputStream
    }

    private void readTxt(String fileNam){
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open(fileNam);
            Scanner input = new Scanner(is);
            input.useDelimiter("[\n ]+");

            finalFile.setFileName(fileNam);

            while(input.hasNext()){
                String word = input.next();
                finalFile.addWord(word);
            }

            input.close(); // Don't forget to close the Scanner
            is.close(); // Close the InputStream

            int senCount = 0;
            is = getApplicationContext().getAssets().open(fileNam);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (reader.readLine() != null) {
                senCount++;
            }
            finalFile.setSentenceCnt(senCount);
            // Close the reader
            reader.close();

            Intent intent = new Intent(MainActivity.this,MenuPage.class); // switch to new page
            startActivity(intent);

        } catch (IOException e) {
            errorMessage.setText("Cannot find file. Please try again!");
            e.printStackTrace();
        }
    }

    private void readPdf(String fileNam){
        try {
            finalFile.setFileName(fileNam);

            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open(fileNam);
            PdfReader pdfReader = new PdfReader(is);
            int pages = pdfReader.getNumberOfPages();
            for(int i=1; i<=pages; i++) {
                //Extract the page content using PdfTextExtractor.
                String pageContent =
                        PdfTextExtractor.getTextFromPage(pdfReader, i);
                processPDF(pageContent);
            }
            finalFile.setSentenceCnt(pages);
            pdfReader.close();

            Intent intent = new Intent(MainActivity.this,MenuPage.class); // switch to new page
            startActivity(intent);
        } catch (Exception e) {
            errorMessage.setText("Cannot find file. Please try again!");
            e.printStackTrace();
        }
    }

    private void processPDF(String line){
        String[] dic = line.split("[\\n ]+");
        for(String word:dic){
            finalFile.addWord(word);
        }
    }
}