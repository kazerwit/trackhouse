package org.trackhouse.trackhouse;

/**
 * Recycler View Class for database usernames
 */

public class UserListView {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserListView(String username) {
        this.username = username;
    }

    public UserListView(){}

}
