package com.edgescheduler.notificationservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "memberInfo")
public class MemberInfo {

    @Id
    private Integer memberId;
    private String zoneId;
    private String email;

    public void changeZoneId(String timezone) {
        this.zoneId = timezone;
    }
    public void changeEmail(String email) {
        this.email = email;
    }
}
