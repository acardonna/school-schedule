package com.solvd.schoolschedule.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.schoolschedule.model.Conflict;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConflictJSONParser {
    public static final Logger LOGGER = LogManager.getLogger(ConflictJSONParser.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static String filePath = "src/main/resources/conflicts.json";

    public static void serealize(List<Conflict> conflicts) {

        try {
            // writerWithDefaultPrettyPrinter() it's used to format output with indent and newlines.
            String jsonString = MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(conflicts);

            Path path = Paths.get(filePath);
            // Creates or overwrite4s the file.
            Files.writeString(path, jsonString);

            LOGGER.info("updated: " + filePath);

        } catch (IOException e) {
            LOGGER.info("Error:", e);
        }

    }


}