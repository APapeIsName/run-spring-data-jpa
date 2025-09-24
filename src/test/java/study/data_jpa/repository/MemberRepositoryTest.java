package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository
                .findById(savedMember.getId())
                .get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        List<Member> members = memberRepository.findByUsername("AAA");
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("team A");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto  = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
//        Member m1 = new Member("AAA", 10);
//        Member m2 = new Member("BBB", 20);
//        memberRepository.save(m1);
//        memberRepository.save(m2);

//        List<Member> aaa = memberRepository.findListByUsername("AAA");

//        Member member = memberRepository.findMemberByUsername("AAA");
//        System.out.println("member = " + member);

//        Optional<Member> member = memberRepository.findOptionalMemberByUsername("AAA");
//        System.out.println("member = " + member.orElseThrow());

//        List<Member> result = memberRepository.findListByUsername("asdf");
//        System.out.println("result = " + result.size()); -> 없으면 empty list 반환

//        Member member = memberRepository.findMemberByUsername("asdf");
//        System.out.println("member = " + member); -> null 반환

//        Optional<Member> member = memberRepository.findOptionalMemberByUsername("asdf");
//        System.out.println("member = " + member); // -> Optional.empty 반환

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        Optional<Member> member = memberRepository.findOptionalMemberByUsername("AAA");
//        System.out.println("member = " + member); // -> List 아닌데 2개 이상 오면 exception 발생
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));
        memberRepository.save(new Member("member7", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        /* Page */
        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // Entity Page로 Dto 를 만들기 쉬운 방법
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
//        long totalElements = page.getTotalElements();

//        for (Member member : content) {
//            System.out.println("member = " + member);
//        }
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(7);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        /* Slice */
//        //when
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//
//        //then
//        List<Member> content = page.getContent();
//
//        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.isFirst()).isTrue();
//        assertThat(page.hasNext()).isTrue();

        /* List */
        //when
//        List<Member> page = memberRepository.findByAge(age, pageRequest);
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        // N+1 문제 - 1은 select Member, N은 Member 별 팀 정보를 전부 구해옴 -> 성능 문제 발생
        // -> JPA 에서는 fetch join 으로 해결 가능
//        List<Member> members = memberRepository.findMemberFetchJoin();

        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
//        Member findMember = memberRepository.findById(member.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        List<Member> findMembers = memberRepository.findLockByUsername("member1");

        em.flush();
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);
//        for (UsernameOnlyDto userNameOnly : result) {
//            System.out.println("userNameOnly = " + userNameOnly.getUsername());
//        }
        List<NestedClosedProjections> result =
                memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections projections : result) {
            String username = projections.getUsername();
            System.out.println("username = " + username);
            String teamName = projections.getTeam().getName();
            System.out.println("teamName = " + teamName);

            System.out.println("projections = " + projections);
        }

    }
}