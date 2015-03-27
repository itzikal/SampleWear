package com.itzik.samplewear.locationSimulator;

import com.example.itzik.common.LocationDataSample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Itzik on 12/22/2014.
 */
public class StvParser
{

    public ArrayList<LocationDataSample> parseFile(File file)
    {
        ArrayList<LocationDataSample> list = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.contains("epoch")) continue;
                String[] split = line.split("\\| ");
                LocationDataSample loc = new LocationDataSample(Double.parseDouble(split[1]),Double.parseDouble(split[2]));
                loc.setTime(Long.parseLong(split[0]));
//                loc.setAltitude(Double.parseDouble(split[3]));
                loc.setSpeed(Float.parseFloat(split[4]));
                list.add(loc);
            }
        }
        catch (IOException e)
        {
            //You'll need to add proper error handling here
        }
        return list;
    }
}
