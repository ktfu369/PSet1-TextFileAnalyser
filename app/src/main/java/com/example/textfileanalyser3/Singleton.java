package com.example.textfileanalyser3;

import android.content.res.AssetManager;
import android.util.Log;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Singleton {
    private static final Singleton instance = new Singleton();

    private String fileName;
    private String fileType;
    private static ArrayList<String> commonWords;
    private ArrayList<String> words;
    private ArrayList<String> processedWords;
    private int sentenceCnt;
    private TreeMap<String, Integer> uniqueWords = new TreeMap<>();


    public static <K, V extends Comparable<V>> Map<K, V> valueSort(final Map<K, V> map) {
        // Create a list from the map entries
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());

        // Sort the list based on the values in descending order
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Create a LinkedHashMap to preserve the insertion order while iterating
        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private Singleton(){
        commonWords = new ArrayList<String>();
        words = new ArrayList<String>();
        processedWords = new ArrayList<String>();
        sentenceCnt = 0;
    }

    public static Singleton getInstance() {
        return instance;
    }

    public void reset(){
        words = new ArrayList<String>();
        processedWords = new ArrayList<String>();
        uniqueWords = new TreeMap<>();
    }

    public void updateCommonWords(String word){
        commonWords.add(word);
    }

    private static boolean checkValid(String word){
        if(word.equals("") || word.equals(" ")) return false;
        for(String str:commonWords){
            if(word.equals(str)) return false;
        }
        return true;
    }

    public void addWord(String word){
        words.add(word);

        if(word.equals("I")) return;

        String processedWord = "";
        for(int i=0;i<word.length();i++){
            char curChar = word.charAt(i);
            if((curChar>='a'&&curChar<='z')||(curChar>='A'&&curChar<='Z')){
                processedWord += curChar + "";
            }
        }
        processedWord = processedWord.toLowerCase();
        if(!checkValid(processedWord)){
            return;
        }
        processedWords.add(processedWord);

        if(uniqueWords.containsKey(processedWord)){
            int currentValue = uniqueWords.get(processedWord);
            uniqueWords.put(processedWord, currentValue + 1);
        }else{
            uniqueWords.put(processedWord,1);
        }
    }

    public void setFileType(String type){
        fileType = type;
    }

    public String getFileType(){
        return fileType;
    }

    public void setFileName(String word){
        fileName = word;
    }

    public String getFileName(){
        return fileName;
    }

    public void setSentenceCnt(int cnt){
        sentenceCnt = cnt;
    }

    public int getSentenceCnt(){
        return sentenceCnt;
    }

    public String getWholeText(){
        String text = "";
        for(String word:words){
            text += word;
            text += " ";
        }
        return text;
    }

    public int getWordCount(){
        return processedWords.size();
    }

    public int getUnqiueWordCount(){
        return uniqueWords.size();
    }

    public String getUniqueText(){
        String text = "";
        Boolean flag = false;
        for(String key:uniqueWords.keySet()){
            if(flag){
                text += "\n";
            }
            text += key;
            flag = true;
        }
        return text;
    }

    public List<DataEntry> data(){
        List<DataEntry> dataEntries = new ArrayList<>();
        for(Map.Entry<String,Integer> entry: uniqueWords.entrySet()){
            String word = entry.getKey();
            int num = entry.getValue();
            dataEntries.add(new ValueDataEntry(word,num));
        }
        return dataEntries;
    }

    public Map getSorted(){
        Map sortedMap = valueSort(uniqueWords);

        Set set = sortedMap.entrySet();
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry mp = (Map.Entry)i.next();

            String text = mp.getKey() + ", " + mp.getValue();
            Log.d("check",text);

        }
        return sortedMap;
    }

}