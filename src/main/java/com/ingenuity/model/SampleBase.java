package com.ingenuity.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SampleBase implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum State {
        UNKNOWN, INCLUDE, EXCLUDE, REPORTED, ADDED, DELETED;
    }

    @Id
    protected int id;
    protected int sampleId;
    protected String sampleName;
    protected String author;
    protected long timeCreated;
    protected long timeModified;
    @Field(type = FieldType.Nested)
    protected List<Comment> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(long timeModified) {
        this.timeModified = timeModified;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        if (comment != null) {
            if (comments == null) {
                comments = new ArrayList<Comment>(2);
            }
            comments.add(comment);
        }
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }
}
