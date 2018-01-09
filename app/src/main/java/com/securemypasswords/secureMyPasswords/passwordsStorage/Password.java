package com.securemypasswords.secureMyPasswords.passwordsStorage;

public class Password implements AppElements{

    private String password;
    private String name;
    private String url;
    private String userName;
    private String note;

    public Password(String password, String name, String url, String userName, String note) {
        this.password = password;
        this.name = name;
        this.url = url;
        this.userName = userName;
        this.note = note;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
