package com.kgc.kmall.service;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.bean.MemberReceiveAddress;

import java.util.List;

public interface MemberService {
    public List<Member> selectAllMember();

    Member login(Member member);

    void addUserToken(String token, Long memberId);

    Member checkOauthUser(Long sourceUid);

    void addOauthUser(Member umsMember);

    List<MemberReceiveAddress> getReceiveAddressByMemberId(Long valueOf);
}
