package com.beststar.storage.entity;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoResponse {
    private long lineNumber;
    private String fileName;
    private String randomLine;
    private String backwardsLine;
    private char letterMostOftenLine;

    public FileInfoResponse(long lineNumber, 
                            String fileName) {
        this.lineNumber = lineNumber;
        this.fileName = fileName;
    }

    public String toText() {
        return "lineNumber = " + lineNumber + "\n" + 
               "fileName = " + fileName + "\n" +
               "randomLine = " + randomLine + "\n" +
               "backwardsLine = " + backwardsLine + "\n" +
               "letterMostOftenLine = " + letterMostOftenLine + "\n";
    }
}
