package com.edgescheduler.notificationservice.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateMessage {

    private Integer id;
    private String email;
    private String zoneId;
}
