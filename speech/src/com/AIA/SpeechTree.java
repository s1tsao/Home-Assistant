package com.AIA;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class SpeechTree {
    private final String path = "C:\\Users\\Samuel Tsao\\Documents\\AIA\\Home-Assistant\\speech\\src\\com\\AIA\\speechtree.json";
    Reader reader;
    Writer writer;
    private Map<String, Set<String>> indexPseudoTraining = new HashMap<>();;

    private node root;
    public node current;


    private Map<String, Integer> missheard;
    private int countMissHeard = 0;

    JSONObject jsonObject;

    SpeechTree() {
        JSONParser parser = new JSONParser();
        missheard = new HashMap<>();
        try {
            reader = new FileReader(path);
            jsonObject = (JSONObject) parser.parse(reader);
            System.out.println(jsonObject);

            HashMap<String, Object> parsedData = new ObjectMapper().readValue(jsonObject.toJSONString(), HashMap.class);
            Map<String, List<String>> pseudotraining = ((Map<String, List<String>>) parsedData.get("pseudotraining"));
            for (String indexKey : pseudotraining.keySet()) {
                HashSet<String> trainingData = new HashSet<>();

                indexPseudoTraining.put(indexKey, trainingData);
                for (String data : pseudotraining.get(indexKey)) {
                    trainingData.add(data);
                }
            }

            root = new node("ohio oniichan", (Map<String, ArrayList<Object>>) parsedData.get("dialogue"));
            current = root;
            remember();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void remember() {
        try {
            Map<String,List> newPseudoTraining = new HashMap<String,List>();
            for(String key:indexPseudoTraining.keySet()){
                newPseudoTraining.put(key,new ArrayList(indexPseudoTraining.get(key)));
            }

            jsonObject.put("pseudotraining", newPseudoTraining);

            writer = new FileWriter("C:\\Users\\Samuel Tsao\\Documents\\AIA\\Home-Assistant\\speech\\src\\com\\AIA\\test.json");
            writer.write(jsonObject.toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDialogue() {
        current = root;
        System.out.println(root.response);
    }

    public String dialogue(String text) {
        node newDialogue = null;
        String indexFound = null;
        for(String index: current.children.keySet())
            if(indexPseudoTraining.get(index).contains(text)) {
                newDialogue = current.children.get(index);
                indexFound = index;
            }
        if (newDialogue == null) {
            if (!missheard.containsKey(text) && !text.contains(",")) {
                missheard.put(text, 1);
                countMissHeard++;
            } else {
                countMissHeard = 0;
                missheard.put(text, missheard.get(text) + 1);
            }
            return "sorry oniichan uwu i didnt understand";
        } else {
            for (String word : missheard.keySet()) {
                if (missheard.get(word) > countMissHeard / 2) {
                    indexPseudoTraining.get(indexFound).add(word);
                }
            }
        }
        current = newDialogue;
        return newDialogue.response;

    }

    public class node {
        public node parent;
        public Map<String, node> children = new HashMap<>();
        public String response;

        node(String raw) {
            response = raw;
        }

        node(String raw, Map<String, ArrayList<Object>> json) {
            this.response = raw;
            for (String keyIndex : json.keySet()) {
                node temp;
                if (json.get(keyIndex).size() == 1) {
                    temp = new node((String) json.get(keyIndex).get(0));
                } else {
                    temp = new node((String) json.get(keyIndex).get(0), ((Map<String, ArrayList<Object>>) json.get(keyIndex).get(1)));
                }
                ArrayList<String> keylist = new ArrayList<>(indexPseudoTraining.get(keyIndex));
                for (String key : keylist) {
                    children.put(key, temp);
                }
            }
        }
    }
}
