package org.trackhouse.trackhouse;

/**
 * UserInformation class to be used with Firebase read/write
 */

public class UserInformation {

    public String username;
    public String email;
    public Double latitude;
    public Double longitude;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    //default constructor required for calls to DataSnapshot.getValue(User.class)
    public UserInformation() {
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Defined UserInformation method
     * @param username
     * @param email
     */
    public UserInformation(String username, String email, Double latitude, Double longitude){
        this.username = username;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}