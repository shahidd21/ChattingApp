package com.example.chattingapp.Model;

public class NoData {
    String heading="No data found";
    int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public NoData(String heading, int image) {
        this.image = image;
        this.heading = heading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
