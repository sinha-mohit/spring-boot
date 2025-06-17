package com.ms.my_spring_boot_project.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.Random;

@Component
public class LogGenerator {
    private static final Logger logger = LoggerFactory.getLogger(LogGenerator.class);
    private static final String[] messages = {
            "User login successful",
            "User login failed",
            "Order placed successfully",
            "Order failed due to payment error",
            "Data fetched from database",
            "Null pointer exception occurred",
            "External API call succeeded",
            "External API call failed",
            "User registration completed",
            "User registration failed"
    };
    private static final Random random = new Random();

    public LogGenerator() {
        // add time also in the log messages
        Time currentTime = new Time(System.currentTimeMillis());
        logger.info(currentTime + "LogGenerator initialized! If you see this, the bean is active and scheduled tasks should run.");
    }

    // 1 log every 10 seconds = 6 logs per minute
    @Scheduled(fixedRate = 10000)
    public void generateLogs() {
         // add time also in the log messages
        Time currentTime = new Time(System.currentTimeMillis());
        int logType = random.nextInt(4);
        String message = messages[random.nextInt(messages.length)] + " at " + currentTime;
        switch (logType) {
            case 0:
                logger.info(message);
                break;
            case 1:
                logger.warn(message);
                break;
            case 2:
                logger.error(message);
                break;
            case 3:
                logger.debug(message);
                break;
        }
    }
}
