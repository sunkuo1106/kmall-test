package com.kgc.kmall.service;

import com.kgc.kmall.bean.Member;

import java.util.List;

public interface MemberService {
    public List<Member> selectAllMember();

    Member login(Member member);

    void addUserToken(String token, Long memberId);
}
