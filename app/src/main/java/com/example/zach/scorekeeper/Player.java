package com.example.zach.scorekeeper;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Player implements Serializable, Parcelable {
    private Integer id;
    private String name;
    private Integer currentScore;

    public Player(Integer id, String name, Integer currentScore) {
        this.id = id;
        this.name = name;
        this.currentScore = currentScore;
    }

    public Player(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.currentScore = in.readInt();
    }

    public final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentScore() {
        return currentScore == null ? 0 : currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Player) {
            Player other = (Player) o;
            if (this.id != null) {
                return this.id.equals(other.getId());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.id != null) {
            return this.id.hashCode();
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(currentScore);
    }
}