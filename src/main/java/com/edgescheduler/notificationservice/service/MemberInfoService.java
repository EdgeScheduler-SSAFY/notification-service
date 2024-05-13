package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberInfoService {

    private final MemberInfoRepository memberInfoRepository;

    public Mono<ZoneId> getZoneIdOfMember(Integer memberId) {
        return memberInfoRepository.findById(memberId)
            .map(memberInfo -> ZoneId.of(memberInfo.getZoneId()))
            .switchIfEmpty(Mono.just(ZoneOffset.UTC));
    }
}
