package com.tduch.alarm.repository.mongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tduch.alarm.entity.mongdb.AlarmEmailInfoMongoEntity;

@Repository
public interface AlarmEmailInfoMongoRepository extends MongoRepository<AlarmEmailInfoMongoEntity, Integer> {
	
	
//	 @Query("{'sentTmstmp' : {$gt : ?0, $lt : ?1}}")
	public List<AlarmEmailInfoMongoEntity> findBySentTmstmpBetween(Long dateFrom, Long dateTo);
}
