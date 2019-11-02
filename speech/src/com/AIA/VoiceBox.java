package com.AIA;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class VoiceBox {
    Configuration configuration;
    SpeechTree speechTree;

    public VoiceBox(Configuration configuration, SpeechTree speechTree){
        this.configuration = configuration;
        this.speechTree = speechTree;
    }

    public void start(){
        try {
            LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
            // Start recognition process pruning previously cached data.
            System.out.println("starting...");
            speechTree.startDialogue();
            recognizer.startRecognition(true);

            // Get individual words and their times.
            SpeechResult result;
            while ((result = recognizer.getResult()) != null && result.getHypothesis()!="exit" ) {
                System.out.format("you said: %s\n", result.getHypothesis());
                speechTree.dialogue(result.getHypothesis());

                /*System.out.format("Hypothesis: %s\n", result.getHypothesis());

                System.out.println("List of recognized words and their times:");
                for (WordResult r : result.getWords()) {
                    System.out.println(r);
                }

                System.out.println("Best 3 hypothesis:");
                for (String s : result.getNbest(3))
                    System.out.println(s);*/
            }
            speechTree.remember();
            recognizer.stopRecognition();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
