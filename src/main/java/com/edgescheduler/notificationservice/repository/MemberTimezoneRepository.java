package com.edgescheduler.notificationservice.repository;

import com.edgescheduler.notificationservice.domain.MemberTimezone;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MemberTimezoneRepository extends ReactiveMongoRepository<MemberTimezone, Integer>{

}
