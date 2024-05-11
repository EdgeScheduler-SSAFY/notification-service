package com.edgescheduler.notificationservice.repository;

import com.edgescheduler.notificationservice.domain.MemberInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MemberInfoRepository extends ReactiveMongoRepository<MemberInfo, Integer>{

}
