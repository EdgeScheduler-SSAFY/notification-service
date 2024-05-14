package com.edgescheduler.notificationservice.service;

import com.edgescheduler.notificationservice.domain.MemberInfo;
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

    public Mono<MemberInfo> upsertZoneIdOfMember(Integer memberId, ZoneId zoneId) {
        return memberInfoRepository.findById(memberId)
            .flatMap(memberInfo -> {
                memberInfo.changeZoneId(zoneId.getId());
                return memberInfoRepository.save(memberInfo);
            }).switchIfEmpty(memberInfoRepository.save(MemberInfo.builder()
                .memberId(memberId)
                .zoneId(zoneId.getId())
                .build()));
    }

    public Mono<MemberInfo> upsertEmailOfMember(Integer memberId, String email) {
        return memberInfoRepository.findById(memberId)
            .flatMap(memberInfo -> {
                memberInfo.changeEmail(email);
                return memberInfoRepository.save(memberInfo);
            }).switchIfEmpty(memberInfoRepository.save(MemberInfo.builder()
                .memberId(memberId)
                .email(email)
                .build()));
    }

    public Mono<MemberInfo> saveMemberInfo(MemberInfo memberInfo) {
        return memberInfoRepository.save(memberInfo);
    }
}
