package com.gazi.gazi_renew.member.domain.enums;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    Role(String roleUser) {
    }
}
