package com.mecoo.spider.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Instagram GraphQL API响应数据模型
 */
public class InstagramGraphQLResponse {
    
    @SerializedName("data")
    private Data data;
    
    public Data getData() { return data; }
    
    public static class Data {
        @SerializedName("xdt_api__v1__clips__user__connection_v2")
        private Connection connection;
        
        public Connection getConnection() { return connection; }
    }
    
    public static class Connection {
        @SerializedName("edges")
        private List<Edge> edges;
        
        public List<Edge> getEdges() { return edges; }
    }
    
    public static class Edge {
        @SerializedName("node")
        private Node node;
        
        public Node getNode() { return node; }
    }
    
    public static class Node {
        @SerializedName("media")
        private Media media;
        
        public Media getMedia() { return media; }
    }
    
    public static class Media {
        @SerializedName("code")
        private String code;
        
        @SerializedName("pk")
        private String pk;
        
        @SerializedName("media_type")
        private Integer mediaType;
        
        @SerializedName("play_count")
        private Integer playCount;
        
        @SerializedName("like_count")
        private Integer likeCount;
        
        @SerializedName("comment_count")
        private Integer commentCount;
        
        @SerializedName("user")
        private User user;
        
        // Getters
        public String getCode() { return code; }
        public String getPk() { return pk; }
        public Integer getMediaType() { return mediaType; }
        public Integer getPlayCount() { return playCount; }
        public Integer getLikeCount() { return likeCount; }
        public Integer getCommentCount() { return commentCount; }
        public User getUser() { return user; }
    }
    
    public static class User {
        @SerializedName("pk")
        private String pk;
        
        public String getPk() { return pk; }
    }
}