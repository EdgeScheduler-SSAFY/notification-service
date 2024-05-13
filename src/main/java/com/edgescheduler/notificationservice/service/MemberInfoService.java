package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.MemberInfo;
import com.edgescheduler.notificationservice.repository.MemberInfoRepository;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberInfoService {

    private final Map<Integer, ZoneId> memberTimezoneMap = new ConcurrentHashMap<>();
    private final MemberInfoRepository memberInfoRepository;

    public Mono<ZoneId> getZoneIdOfMember(Integer memberId) {
        return Mono.justOrEmpty(memberTimezoneMap.get(memberId))
            .switchIfEmpty(memberInfoRepository.findById(memberId)
                .map(memberInfo -> {
                    ZoneId zoneId = ZoneId.of(memberInfo.getZoneId());
                    memberTimezoneMap.put(memberId, zoneId);
                    return zoneId;
                })
                .switchIfEmpty(Mono.just(ZoneOffset.UTC)));
    }

    public Mono<Void> upsertMemberTimezone(Integer memberId, String timezone) {
        return memberInfoRepository.save(MemberInfo.builder()
                .memberId(memberId)
                .zoneId(timezone)
                .build())
            .doOnSuccess(memberInfo -> memberTimezoneMap.put(memberId, ZoneId.of(timezone)))
            .then();
    }
}
