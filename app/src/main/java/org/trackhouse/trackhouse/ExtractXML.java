package org.trackhouse.trackhouse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bonnie on 3/9/2018. Custom class to extract "content" tag information
 * from entries.
 */

public class ExtractXML {

    private static final String TAG = "ExtractXML";

    private String tag;
    private String endtag;
    private String xml;

    /**
     * Pass the xml tag, for example "a href=" and then pass the xml string with the tag, for example,
     * "entries.get(0).getContent".
     * @param tag
     * @param xml
     */
    public ExtractXML(String tag, String xml) {
        this.tag = tag;
        this.xml = xml;
        this.endtag = "NONE";
    }

    public ExtractXML(String tag, String xml, String endtag) {
        this.tag = tag;
        this.xml = xml;
        this.endtag = endtag;
    }


    public List<String> start(){
        List<String> result = new ArrayList<>();
        String[] splitXML = null;
        String marker = null;

        if(endtag.equals("NONE")){
            marker = "\"";
            splitXML = xml.split(tag + marker);
        }
        else {
            marker = endtag;
            splitXML = xml.split(tag);
        }

        int count = splitXML.length;

        for(int i = 1; i < count; i++){

            String temp = splitXML[i];
            int index = temp.indexOf(marker);
            //Log.d(TAG, "start: index: " + index);
            //Log.d(TAG, "start: extracted " + temp);

            temp = temp.substring(0,index);
            //Log.d(TAG, "start: snipped: " + temp);
            result.add(temp);
        }

        return result;
    }

}
