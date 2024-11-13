package com.gazi.gazi_renew.member.service;


import com.gazi.gazi_renew.mock.FakeMemberRepository;
import com.gazi.gazi_renew.mock.FakeMyFindRoadPathRepository;
import com.gazi.gazi_renew.mock.FakeNotificationRepository;
import com.gazi.gazi_renew.mock.TestPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;

class MemberServiceImplTest {
    private MemberServiceImpl memberService;

    @BeforeEach
    void init() {
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakeNotificationRepository fakeNotificationRepository = new FakeNotificationRepository();
        FakeMyFindRoadPathRepository fakeMyFindRoadPathRepository = new FakeMyFindRoadPathRepository();
        TestPasswordEncoder testPasswordEncoder = new TestPasswordEncoder();


    }
}