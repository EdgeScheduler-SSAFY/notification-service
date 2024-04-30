package com.edgescheduler.notificationservice.repository;

public interface CustomNotificationRepository {

    void markAsRead(Integer notificationId);

    void markAllAsRead(Integer receiverId);
}
