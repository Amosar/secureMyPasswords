package com.securemypasswords.securemypasswords.passwordsStorage;

import java.util.HashMap;

public class Group implements AppElements{

    private String name;

    private final HashMap<String, Password> passwords;
    private final HashMap<String, Group> groups;

    public Group(String name){
        this.name = name;
        groups = new HashMap<>();
        passwords = new HashMap<>();
    }

    public Group(String name, HashMap<String ,Password> passwords, HashMap<String, Group> groups){
        this.name = name;
        this.groups = groups;
        this.passwords=passwords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addGroup(Group group){
        groups.put(group.getName(), group);
    }

    public Group getGroup(String name) {
        return groups.get(name);
    }

    public Group[] getGroups() {
        return groups.values().toArray(new Group[]{});
    }


    public void addPassword(Password password){
        passwords.put(password.getName(), password);
    }

    public Password getPassword(String name){
        return passwords.get(name);
    }

    public Password[] getPasswords(){
        return passwords.values().toArray(new Password[]{});
    }
}
