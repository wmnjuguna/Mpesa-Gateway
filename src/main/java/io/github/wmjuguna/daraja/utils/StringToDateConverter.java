package io.github.wmjuguna.daraja.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static Date parse(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }
}
