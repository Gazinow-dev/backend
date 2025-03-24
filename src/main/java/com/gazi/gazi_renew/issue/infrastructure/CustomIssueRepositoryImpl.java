package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.domain.dto.QIssueStationDetail;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.CustomIssueRepository;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.gazi.gazi_renew.issue.infrastructure.entity.QIssueCommentEntity.issueCommentEntity;
import static com.gazi.gazi_renew.issue.infrastructure.entity.QIssueEntity.issueEntity;
import static com.gazi.gazi_renew.issue.infrastructure.entity.QIssueLineEntity.issueLineEntity;
import static com.gazi.gazi_renew.issue.infrastructure.entity.QIssueStationEntity.issueStationEntity;
import static com.gazi.gazi_renew.station.infrastructure.QLineEntity.lineEntity;
import static com.gazi.gazi_renew.station.infrastructure.QStationEntity.stationEntity;
@Repository
@RequiredArgsConstructor
public class CustomIssueRepositoryImpl implements CustomIssueRepository {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<IssueStationDetail> findAllByOrderByStartDateDesc(Pageable pageable) {
        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(issueEntity.count())
                .from(issueEntity);

        List<Long> issueIds = jpaQueryFactory.select(issueEntity.id)
                .from(issueEntity)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(issueEntity.startDate.desc())
                .fetch();

        List<IssueStationDetail> result = jpaQueryFactory.select(new QIssueStationDetail(
                        issueEntity.id,
                        issueEntity.title,
                        issueEntity.content,
                        issueEntity.likeCount,
                        Expressions.asNumber(
                                JPAExpressions.select(issueCommentEntity.count())
                                        .from(issueCommentEntity)
                                        .where(issueCommentEntity.issueEntity.id.eq(issueEntity.id))
                        ).intValue(), // 서브쿼리 결과를 int로 변환
                        Expressions.constant(Boolean.FALSE),
                        Expressions.constant(Boolean.FALSE),
                        issueEntity.keyword,
                        issueEntity.startDate,
                        issueEntity.expireDate,
                        stationEntity.line,
                        stationEntity.name))
                .from(issueStationEntity)
                .join(issueStationEntity.issueEntity, issueEntity)
                .join(issueStationEntity.stationEntity, stationEntity)
                .where(issueEntity.id.in(issueIds))
                .orderBy(issueEntity.startDate.desc())
                .fetch();
        // count 쿼리가 필요하지 않을 땐 쿼리를 날리지 않음
        return PageableExecutionUtils.getPage(result, pageable, totalCount::fetchOne);
    }

    @Override
    public Optional<IssueEntity> findByIssueKey(String issueKey) {
        IssueEntity issue = jpaQueryFactory
                .selectFrom(issueEntity)
                .where(issueEntity.issueKey.eq(issueKey))
                .fetchOne();

        return Optional.ofNullable(issue);
    }

    @Override
    public List<IssueStationDetail> getIssueById(Long id) {
        List<IssueStationDetail> issueStationDetailList = jpaQueryFactory.select(new QIssueStationDetail(
                        issueEntity.id,
                        issueEntity.title,
                        issueEntity.content,
                        issueEntity.likeCount,
                        Expressions.asNumber(
                                JPAExpressions.select(issueCommentEntity.count())
                                        .from(issueCommentEntity)
                                        .where(issueCommentEntity.issueEntity.id.eq(issueEntity.id))
                        ).intValue(), // 서브쿼리 결과를 int로 변환
                        Expressions.constant(Boolean.FALSE),
                        Expressions.constant(Boolean.FALSE),
                        issueEntity.keyword,
                        issueEntity.startDate,
                        issueEntity.expireDate,
                        stationEntity.line,
                        stationEntity.name))
                .from(issueStationEntity)
                .join(issueStationEntity.issueEntity, issueEntity)
                .join(issueStationEntity.stationEntity, stationEntity)
                .where(issueEntity.id.eq(id))
                .fetch();
        return issueStationDetailList;
    }
    @Override
    public Page<IssueStationDetail> getIssueByLineName(String lineName, Pageable pageable) {
        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(issueEntity.count())
                .from(issueEntity);

        List<Long> issueIds = jpaQueryFactory.select(issueEntity.id)
                .from(issueEntity)
                .join(issueLineEntity).on(issueEntity.eq(issueLineEntity.issueEntity))
                .where(issueLineEntity.lineEntity.id.in(
                        JPAExpressions.select(lineEntity.id)
                                .from(lineEntity)
                                .where(lineEntity.lineName.eq(lineName))
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(issueEntity.startDate.desc())
                .fetch();

        List<IssueStationDetail> issueStationDetails = jpaQueryFactory.select(new QIssueStationDetail(
                        issueEntity.id,
                        issueEntity.title,
                        issueEntity.content,
                        issueEntity.likeCount,
                        Expressions.asNumber(
                                JPAExpressions.select(issueCommentEntity.count())
                                        .from(issueCommentEntity)
                                        .where(issueCommentEntity.issueEntity.id.eq(issueEntity.id))
                        ).intValue(), // 서브쿼리 결과를 int로 변환
                        Expressions.constant(Boolean.FALSE),
                        Expressions.constant(Boolean.FALSE),
                        issueEntity.keyword,
                        issueEntity.startDate,
                        issueEntity.expireDate,
                        stationEntity.line,
                        stationEntity.name))
                .from(issueStationEntity)
                .join(issueStationEntity.issueEntity, issueEntity)
                .join(issueStationEntity.stationEntity, stationEntity)
                .where(issueEntity.id.in(issueIds))
                .orderBy(issueEntity.startDate.desc())
                .fetch();

        return PageableExecutionUtils.getPage(issueStationDetails, pageable, totalCount::fetchOne);
    }
    @Override
    public List<IssueStationDetail> findTopIssuesByLikesCount(Pageable pageable) {

        List<Long> issueIds = jpaQueryFactory.select(issueEntity.id)
                .from(issueEntity)
                .orderBy(issueEntity.likeCount.desc()) // 좋아요 개수 내림차순 정렬
                .limit(4) // 상위 4개만 가져오기
                .fetch();

        return jpaQueryFactory.select(new QIssueStationDetail(
                        issueEntity.id,
                        issueEntity.title,
                        issueEntity.content,
                        issueEntity.likeCount,
                        Expressions.asNumber(
                                JPAExpressions.select(issueCommentEntity.count())
                                        .from(issueCommentEntity)
                                        .where(issueCommentEntity.issueEntity.id.eq(issueEntity.id))
                        ).intValue(), // 서브쿼리 결과를 int로 변환
                        Expressions.constant(Boolean.FALSE),
                        Expressions.constant(Boolean.FALSE),
                        issueEntity.keyword,
                        issueEntity.startDate,
                        issueEntity.expireDate,
                        stationEntity.line,
                        stationEntity.name))
                .from(issueStationEntity)
                .join(issueStationEntity.issueEntity, issueEntity)
                .join(issueStationEntity.stationEntity, stationEntity)
                .where(issueEntity.id.in(issueIds))
                .orderBy(issueEntity.likeCount.desc())
                .fetch();
    }
}
