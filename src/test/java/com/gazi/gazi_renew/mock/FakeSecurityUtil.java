package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;

import java.util.ArrayList;
import java.util.List;

public class FakeSecurityUtil implements SecurityUtilService {

    private final List<String> data = new ArrayList<>();

    public void addEmail(String email) {
        data.add(email);
    }

    public String getCurrentUserEmail() {
        if (data.isEmpty()) {
            throw new RuntimeException("이메일이 존재하지 않습니다.");
        }
        return data.get(data.size()-1);
    }
}



