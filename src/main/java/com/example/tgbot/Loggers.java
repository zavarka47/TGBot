package com.example.tgbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loggers {
    public static Logger getLogger (Class c){
        return LoggerFactory.getLogger(c);
    }
}
