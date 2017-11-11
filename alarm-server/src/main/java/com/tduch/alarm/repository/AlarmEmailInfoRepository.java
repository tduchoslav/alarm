package com.tduch.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tduch.alarm.entity.AlarmEmailInfoEntity;

@Repository
public interface AlarmEmailInfoRepository extends JpaRepository<AlarmEmailInfoEntity, Integer> {

	@Query("SELECT e FROM AlarmEmailInfoEntity e WHERE e.sentTmstmp > :dateFrom AND e.sentTmstmp < :dateTo")
	public List<AlarmEmailInfoEntity> getRecordsInPeriod(@Param("dateFrom") Long dateFrom, @Param("dateTo") long dateTo);
}
