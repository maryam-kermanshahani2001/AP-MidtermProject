package network.client;

import java.util.TimerTask;


import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

public class test {
    private String str = "";

    TimerTask task = new TimerTask() {
        public void run() {
            if (str.equals("")) {

            }
        }
    };

    public String getInput() {
        Timer timer = new Timer();
        timer.schedule(task, 60 * 1000);
        System.out.println("Input a string within 10 seconds: ");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
        try {
            str = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.cancel();
        return str;
    }

}


