package com.likelion.GloBuddyBackend.service;

import com.likelion.GloBuddyBackend.domain.MatchingMember;
import com.likelion.GloBuddyBackend.domain.Member;
import com.likelion.GloBuddyBackend.domain.MemberDetail;
import com.likelion.GloBuddyBackend.domain.Post;
import com.likelion.GloBuddyBackend.dto.MatchingMemberDto;
import com.likelion.GloBuddyBackend.dto.MemberDto;
import com.likelion.GloBuddyBackend.dto.PostDto;
import com.likelion.GloBuddyBackend.exception.MatchingNotFountException;
import com.likelion.GloBuddyBackend.exception.MemberNotFoundException;
import com.likelion.GloBuddyBackend.exception.PostNotFoundException;
import com.likelion.GloBuddyBackend.repository.MatchingMemberRepository;
import com.likelion.GloBuddyBackend.repository.MemberDetailRepository;
import com.likelion.GloBuddyBackend.repository.MemberRepository;
import com.likelion.GloBuddyBackend.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingMemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final MatchingMemberRepository matchingMemberRepository;

    public MatchingMemberDto createMatchingRequest(MatchingMemberDto dto , Long postId) {

        Member sender = memberRepository.findById(dto.getSenderId()).orElseThrow(MemberNotFoundException::new);

        MemberDetail senderInfo= memberDetailRepository.findAllByMember(sender.getMemberId());

        Post receiverPost = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);


        MatchingMember matchingMember = MatchingMember.of(sender,receiverPost,dto);

        MatchingMember saved = matchingMemberRepository.save(matchingMember);

        return MatchingMemberDto.of(saved,senderInfo);
    }


    public List<MatchingMemberDto> getAllsentMail(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        List<MatchingMember> sentMail = matchingMemberRepository.findAllByMemberIdAndIfNotChecked(member);
        return sentMail.stream().map(MatchingMemberDto::of).toList();
    }


    public List<MatchingMemberDto> getAllReceiveMail(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        List<MatchingMember> receiveMail = matchingMemberRepository.findAllByMemberIdAndIfNotMatched(member);

        return receiveMail.stream()
                .map(matchingMember -> {
                    MemberDetail senderInfo = memberDetailRepository.findAllByMember(matchingMember.getMember().getMemberId());
                    return MatchingMemberDto.of(matchingMember, senderInfo);
                })
                .toList();
    }




    public Long getNumOfReceiveMail(Long receiverId) {
        Member receiver = memberRepository.findById(receiverId).orElseThrow(MemberNotFoundException::new);
        Long received = matchingMemberRepository.getNumOfReceiveMail(receiver);

        return received;
    }


    public Long getNumOfSentMail(Long senderId) {
        Member sender = memberRepository.findById(senderId).orElseThrow(MemberNotFoundException::new);
        Long sent = matchingMemberRepository.getNumOfSentMail(sender);

        return sent;
    }


    @Transactional
    public void choiceMatching(MatchingMemberDto dto, Long matchingId) {
        MatchingMember matchingMember = matchingMemberRepository.findById(matchingId).orElseThrow(MatchingNotFountException::new);
        matchingMember.setIfMatched(dto.getIfMatched());
        matchingMember.update(MatchingMemberDto.of(matchingMember));
    }

    @Transactional
    public void checkMatching(Long matchingId) {
        MatchingMember matchingMember = matchingMemberRepository.findById(matchingId).orElseThrow(MatchingNotFountException::new);
        matchingMember.setIfChecked(true);
        matchingMember.update(MatchingMemberDto.of(matchingMember));

    }

}

