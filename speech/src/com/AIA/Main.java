package com.AIA;

import com.AIA.SpeechTree.node;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class Main
{

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration();

        // Set path to acoustic model.
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        // Set path to dictionary.
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        // Set language model.
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        try {
            LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
            // Start recognition process pruning previously cached data.
            System.out.println("starting...");
            SpeechTree st = new SpeechTree();
            node current = st.startDialogue();
            recognizer.startRecognition(true);
            System.out.println("analyzing...");

            // Get individual words and their times.
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                System.out.format("you said: %s\n", result.getHypothesis());
                node temp = st.dialogue(result.getHypothesis(), current);
                if(temp!=null)
                    current = temp;
                /*System.out.format("Hypothesis: %s\n", result.getHypothesis());

                System.out.println("List of recognized words and their times:");
                for (WordResult r : result.getWords()) {
                    System.out.println(r);
                }

                System.out.println("Best 3 hypothesis:");
                for (String s : result.getNbest(3))
                    System.out.println(s);*/

            }
            while(result.getHypothesis()!="exit"){

            }
            // Pause recognition process. It can be resumed then with startRecognition(false).
            recognizer.stopRecognition();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
