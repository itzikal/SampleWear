package com.itzik.samplewear.locationSimulator;

import android.os.Environment;
import android.util.Log;

import com.example.itzik.common.LocationDataSample;
import com.itzik.samplewear.locationSimulator.gpx.GPX;
import com.itzik.samplewear.locationSimulator.gpx.JDOM;
import com.itzik.samplewear.locationSimulator.gpx.ParsingException;
import com.itzik.samplewear.locationSimulator.gpx.Track;
import com.itzik.samplewear.locationSimulator.gpx.TrackSegment;
import com.itzik.samplewear.locationSimulator.gpx.Waypoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Itzik on 1/23/2015.
 */
public class LocationsLoader
{
    public static final String LOG_TAG = LocationsLoader.class.getSimpleName();
    public static ArrayList<LocationDataSample> getLocationsFromDataBase()
    {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File stv = new File(directory, "sailingLogFile.stv");
        File gpx = new File(directory, "sailingLogFile.gpx");
        if (gpx != null && gpx.exists())
        {
            Log.d(LOG_TAG, "getLocationsFromDataBase(), gpx");
            return getLocationsFromGPXFile(gpx);
        }
        else if (stv != null && stv.exists())
        {
            Log.d(LOG_TAG, "getLocationsFromDataBase(), stv");
            return getLocationsFromSTVFile(stv);
        }
        return null;
    }

    private static ArrayList<LocationDataSample> getLocationsFromGPXFile(File file)
    {
        GPX gpx;
        try
        {
            JDOM jdom = new JDOM();
            gpx = jdom.parse(file);
        }
        catch (ParsingException e)
        {
            e.printStackTrace();
            return null;
        }
        ArrayList<LocationDataSample> locations = new ArrayList<>();
        for (Track t : gpx.getTracks())
        {
            for (TrackSegment s : t.getSegments())
            {
                for (Waypoint w : s.getWaypoints())
                {
                    LocationDataSample locationDataSample = new LocationDataSample(w.getCoordinate().getLatitude(), w.getCoordinate().getLongitude());
                    locations.add(locationDataSample);
                }
            }
        }
        return locations;
    }

    private static ArrayList<LocationDataSample> getLocationsFromSTVFile(File file)
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
                LocationDataSample loc = new LocationDataSample(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
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
