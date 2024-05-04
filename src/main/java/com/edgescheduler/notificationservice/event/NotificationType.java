package com.edgescheduler.notificationservice.event;

public enum NotificationType {
    MEETING_CREATED("meeting-created"),
    MEETING_UPDATED("meeting-updated"),
    MEETING_DELETED("meeting-deleted"),
    ATTENDEE_RESPONSE("attendee-response"),
    ATTENDEE_PROPOSAL("attendee-proposal");

    NotificationType(String name) {
    }
}
