package org.trackhouse.trackhouse;

/**
 * Created by lordis on 1/29/2018.
 */

public class UserInformation {

    public String username;
    public String email;

    //default constructor required for calls to DataSnapshot.getValue(User.class)
    public UserInformation() {
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public UserInformation(String username, String email){
        this.username = username;
        this.email = email;
    }
}