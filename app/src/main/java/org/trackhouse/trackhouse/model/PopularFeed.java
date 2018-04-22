package org.trackhouse.trackhouse.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.trackhouse.trackhouse.model.entry.Entry;

import java.io.Serializable;
import java.util.List;

/**
 * Class for Popular Feed - has different Elements from the Feed class.
 */

@Root(name = "feed", strict = false)
public class PopularFeed implements Serializable {

    @Element(name = "id")
    private String id;

    @Element(name = "title")
    private String title;

    @Element(name = "updated")
    private String updated;

    @ElementList(inline = true, name = "entry")
    private List<Entry> entries;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    // saves entries to string type
    @Override
    public String toString() {
        return "PopularFeed: \n [Entries: \n" + entries +"]";
    }
}

