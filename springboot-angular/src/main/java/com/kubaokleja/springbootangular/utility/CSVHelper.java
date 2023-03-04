package com.kubaokleja.springbootangular.utility;

import com.kubaokleja.springbootangular.dto.UserDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class CSVHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERS = { "username","email","firstName","lastName"};

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<UserDTO> csvToUserDTO(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                             .setHeader("username","email","firstName","lastName")
                             .setSkipHeaderRecord(true)
                             .setIgnoreHeaderCase(true)
                             .setTrim(true)
                             .build().parse(fileReader)) {

            return csvParser.getRecords()
                    .stream()
                    .map(csvRecord -> UserDTO.builder()
                            .username(csvRecord.get("username"))
                            .email(csvRecord.get("email"))
                            .firstName(csvRecord.get("firstName"))
                            .lastName(csvRecord.get("lastName"))
                            .build())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
        }
    }
}
