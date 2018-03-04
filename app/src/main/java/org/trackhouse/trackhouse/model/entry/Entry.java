package org.trackhouse.trackhouse.model.entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Class to handle RSS entries
 */

//TODO: Author is not getting pulled, is showing "null" in output. Fix.

@Root(name = "entry", strict = false)
public class Entry implements Serializable{


    @Element(name = "content")
    private String content;

    @Element(required = false, name = "author")
    private String author;

    @Element(name = "id")
    private String id;


    @Element(name = "title")
    private String title;

    @Element(name = "updated")
    private String updated;

    public Entry(){}

    public Entry(String content, String author, String id, String title, String updated) {
        this.content = content;
        this.author = author;
        this.id = id;
        this.title = title;
        this.updated = updated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "\n\n Entry{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", updated='" + updated + '\'' +
                '}' + "\n" +
                "------------------------------------------------------------------------------------------------\n";
    }
}
