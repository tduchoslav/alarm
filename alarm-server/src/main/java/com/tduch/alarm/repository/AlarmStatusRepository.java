package com.tduch.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tduch.alarm.entity.AlarmStatusEntity;

@Repository
public interface AlarmStatusRepository extends JpaRepository<AlarmStatusEntity, Integer> {

}
