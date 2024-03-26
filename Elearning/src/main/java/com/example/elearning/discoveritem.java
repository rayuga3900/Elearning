package com.example.elearning;

public class discoveritem {
    private String imageResource;
    private String username;

    public discoveritem(String imageResource, String username) {
        this.imageResource = imageResource;
        this.username = username;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
