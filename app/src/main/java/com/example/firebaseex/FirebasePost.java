package com.example.firebaseex;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class FirebasePost {

    public String id;
    public String name;
    public String email;
    public String tel;

    public FirebasePost() {
    }

    public FirebasePost(String id, String name, String email, String tel) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tel = tel;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("tel", tel);
        return result;
    }
}
