package com.chrisgruber.thinmatrixgame.engine.utils;

import java.io.*;

public class FileUtils {

    private FileUtils() {
    }

    public static String loadAsString(String file) {
        StringBuilder result = new StringBuilder();

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream stream = classloader.getResourceAsStream(file);
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String buffer = "";

            while ((buffer = reader.readLine()) != null) {
                result.append(buffer).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read shader source file: " + file);
            e.printStackTrace();
            System.exit(-1);
        }

        return result.toString();
    }

}
