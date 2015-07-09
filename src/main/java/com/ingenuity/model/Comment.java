package com.ingenuity.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private long timeCreated;
    private long timeModified;
    private String commentData;
    private String author;
    private ObjectType objectType;
    private int objectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (id != comment.id) return false;
        if (objectId != comment.objectId) return false;
        if (timeCreated != comment.timeCreated) return false;
        if (timeModified != comment.timeModified) return false;
        if (commentData != null ? !commentData.equals(comment.commentData) : comment.commentData != null) return false;
        if (author != null ? !author.equals(comment.author) : comment.author != null) return false;
        if (objectType != comment.objectType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (timeCreated ^ (timeCreated >>> 32));
        result = 31 * result + (int) (timeModified ^ (timeModified >>> 32));
        result = 31 * result + (commentData != null ? commentData.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (objectType != null ? objectType.hashCode() : 0);
        result = 31 * result + objectId;
        return result;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCommentData() {
        return commentData;
    }

    public void setCommentData(String commentData) {
        this.commentData = ((commentData == null) ? commentData : commentData.trim());
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }
}

