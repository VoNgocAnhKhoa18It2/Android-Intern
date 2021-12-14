package com.vnakhoa.midtest_intern.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;

public class Login implements Serializable {
    private String id;
    private String birth;
    private String email;
    private String password;

    public Login(String id, String birth, String email, String password) {
        this.id = id;
        this.birth = birth;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Login convectJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<Login>(){}.getType());
    }
}
