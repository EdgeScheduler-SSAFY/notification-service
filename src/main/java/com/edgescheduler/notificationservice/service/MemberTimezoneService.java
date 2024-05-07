package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.MemberTimezone;
import com.edgescheduler.notificationservice.repository.MemberTimezoneRepository;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberTimezoneService {

    private final Map<Integer, ZoneId> memberTimezoneMap = new ConcurrentHashMap<>();
    private final MemberTimezoneRepository memberTimezoneRepository;

    public Mono<ZoneId> getZoneIdOfMember(Integer memberId) {
        return Mono.justOrEmpty(memberTimezoneMap.get(memberId))
            .switchIfEmpty(memberTimezoneRepository.findById(memberId)
                .map(memberTimezone -> {
                    ZoneId zoneId = ZoneId.of(memberTimezone.getTimezone());
                    memberTimezoneMap.put(memberId, zoneId);
                    return zoneId;
                }));
    }

    public Mono<Void> upsertMemberTimezone(Integer memberId, String timezone) {
        return memberTimezoneRepository.save(new MemberTimezone(memberId, timezone))
            .doOnSuccess(memberTimezone -> memberTimezoneMap.put(memberId, ZoneId.of(timezone)))
            .then();
    }
}
