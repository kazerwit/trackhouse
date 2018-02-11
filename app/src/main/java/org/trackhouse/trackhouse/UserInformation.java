package org.trackhouse.trackhouse;

/**
 * UserInformation class to be used with Firebase read/write
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


    /**
     * Defined UserInformation method
     * @param username
     * @param email
     */
    public UserInformation(String username, String email){
        this.username = username;
        this.email = email;
    }
}