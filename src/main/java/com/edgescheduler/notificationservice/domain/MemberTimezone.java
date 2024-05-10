package com.edgescheduler.notificationservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@Document(collection = "memberTimezone")
public class MemberTimezone {

    @Id
    private Integer memberId;
    private String zoneId;

    public void changeZoneId(String timezone) {
        this.zoneId = timezone;
    }
}
