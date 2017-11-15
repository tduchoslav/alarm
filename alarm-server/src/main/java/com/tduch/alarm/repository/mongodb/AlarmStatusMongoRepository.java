package com.tduch.alarm.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tduch.alarm.entity.mongdb.AlarmStatusMongoEntity;

@Repository
public interface AlarmStatusMongoRepository extends MongoRepository<AlarmStatusMongoEntity, Integer> {

}
