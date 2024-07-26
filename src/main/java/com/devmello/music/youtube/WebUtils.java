package com.devmello.music.youtube;



import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebUtils {


    public static String agent1 = "User-Agent";
    public static final Logger logger = LogUtils.getLogger();
    public static String agent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0";
    public static boolean launchedFromOfficalLauncher;
    public static String visitSiteThreaded(final String urly){
        final List<String> lines = new ArrayList<String>();
        String stuff = "";
        (new Thread(new Runnable()
        {
            public void run()
            {
                URL url;
                try {
                    String line;


                    url = new URL(urly);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.addRequestProperty(agent1, agent2);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = in.readLine()) != null) {
                        lines.add(line);
                    }

                }

                catch (Exception e) {

                }

            }
        })).start();
        for(String s : lines){
            stuff += s;
        }       return stuff;

    }

    public void playMusicLink(final String urly){
//        List<URL> lines = new ArrayList<URL>();
//        String stuff = "";
//        (new Thread(() -> {
//            URL url;
//            try {
//                logger.info("Playing " + urly);
////
//                System.out.println("Attempting to play video with ID " + urly);
////                logger.consoleLogInfo("Attempting to play video with ID " + urly);
////
////                logger.consoleLogInfo("Parsed JSON");
//
//                //System.out.println(Duration.parse("PT1H1M13S").getSeconds());
//                Jello.jgui.music.currentSongLength = Integer.valueOf(getMetadata(urly));
//                Player.play("https://server.py4.repl.co/"+urly);
//
//            }
//
//            catch (Exception e) {
//                logger.consoleLogError(String.valueOf(e));
//            }
//
//        })).start();


    }

    public static List<String> visitSiteThreadedFriends(final String urly){
        final List<String> lines = new ArrayList<String>();
        try
        {
            (new Thread(new Runnable()
            {
                public void run()
                {
                    URL url;
                    try {
                        String line;
                        url = new URL(urly);

                        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                        connection.addRequestProperty(agent1, agent2);
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = in.readLine()) != null) {
                            if(!line.isEmpty() && !line.equals(" ") && !line.equals("   ")){
                                lines.add(line.contains(" ") ? line.replace(" ", "") : line);

                                //  for(char c : line.toCharArray()){
                                //Jello.addChatMessage(String.valueOf(c));
                                //   }
                            }

                        }

                    }

                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }
            })).start();
        }
        catch (RuntimeException runtimeexception)
        {
            System.out.println("Error: " + runtimeexception.getMessage());
        }

        return lines;

    }

    public static String visitSite(String urly){
        ArrayList<String> lines = new ArrayList<String>();
        String stuff = "";
        URL url;
        try {
            String line;
            url = new URL(urly);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty(agent1, agent2);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            //System.out.println("HEY");
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }

        }

        catch (Exception e) {

        }
        for(String s : lines){
            stuff += s;
        }
        return stuff;

    }



}
