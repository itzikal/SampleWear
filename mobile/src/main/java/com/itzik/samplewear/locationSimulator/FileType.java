package com.itzik.samplewear.locationSimulator;

import java.io.File;

/**
 * Created by Itzik on 12/22/2014.
 */
public enum FileType
{
    GPX,STV,OTHER;

    public static FileType fromString(String string)
    {
        if(string.toLowerCase().equals("gpx"))
            return FileType.GPX;
        if(string.toLowerCase().equals("stv"))
            return FileType.STV;
        if(string.toLowerCase().equals("log"))
            return FileType.STV;
        return FileType.OTHER;
    }

    public static FileType getFileType(File file)
    {
        String filenameArray[] = file.getName().split("\\.");
        String ext = filenameArray[filenameArray.length - 1].toLowerCase();
        return FileType.fromString(ext);
    }
}