package ru.unn.agile.TemperatureConverter.infrastructure;

import ru.unn.agile.TemperatureConverter.viewmodel.TemperatureConverterLogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TemperatureConverterTxtLogger implements TemperatureConverterLogger {
    private static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm:ss";
    private final String filename;
    private final BufferedWriter writerOfLog;

    public TemperatureConverterTxtLogger(final String filename) {
        this.filename = filename;

        BufferedWriter logWriter = null;
        try {
            logWriter = new BufferedWriter(new FileWriter(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }

        writerOfLog = logWriter;
    }

    @Override
    public void log(final String s) {
        try {
            writerOfLog.write(currentTime() + " > " + s);
            writerOfLog.newLine();
            writerOfLog.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<String> getFullLog() {
        BufferedReader readerOfLog;

        ArrayList<String> log = new ArrayList<String>();

        try {
            readerOfLog = new BufferedReader(new FileReader(filename));
            String line = readerOfLog.readLine();

            while (line != null) {
                log.add(line);
                line = readerOfLog.readLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return log;
    }

    private static String currentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT_NOW, Locale.ENGLISH);
        return dataFormat.format(calendar.getTime());
    }
}
