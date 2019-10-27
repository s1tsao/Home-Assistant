package com.AIA;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpeechTree
{
    public node root;
    SpeechTree(){
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("/Users/stsao/Documents/speech/speech/src/com/AIA/speechtree.json")) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            System.out.println(jsonObject);
            root = new node(new ObjectMapper().readValue(jsonObject.toJSONString(), HashMap.class));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public node startDialogue(){
        return root;
    }

    public node dialogue(String text, node current){
        node newDialogue = current.children.get(text);
        if(newDialogue==null){
            System.out.println("sorry oniichan uwu i didnt understand");
            return null;
        }
        System.out.println(newDialogue.response);
        return newDialogue;
    }

    public class node{
        public node parent;
        public Map<String,node> children = new HashMap<>();
        public String response;
        node(String raw){
            response = raw;
        }

        node(String raw,Map<String,ArrayList<Object>> json){
            this.response = raw;
            for(String keys: json.keySet()){
                if (json.get(keys).size() == 1) {
                    children.put(keys, new node((String) json.get(keys).get(0)));
                }
                else {
                    node temp = new node((String) json.get(keys).get(0), (Map<String, ArrayList<Object>>) json.get(keys).get(1));
                    for (String key : keys.split(",")) {
                        children.put(key, temp);
                    }
                }
            }
        }

        node(Map<String,ArrayList<Object>> json){
            for(String keys: json.keySet()){
                String[] keylist = keys.split(",");
                if (keylist.length == 1) {
                    children.put(keys, new node((String) json.get(keys).get(0)));
                }
                else {
                    node temp = new node((String) json.get(keys).get(0), ((Map<String, ArrayList<Object>>) json.get(keys).get(1)));
                    for (String key : keylist) {
                        children.put(key, temp);
                    }
                }
            }
        }
    }
}
