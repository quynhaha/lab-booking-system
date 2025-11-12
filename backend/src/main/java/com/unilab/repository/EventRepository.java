package com.unilab.repository;

import com.unilab.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserId(Long userId);

    List<Event> findByLabId(Long labId);

    @Query("SELECT e FROM Event e WHERE e.lab.id = :labId AND " +
           "((e.startTime <= :startTime AND e.endTime > :startTime) OR " +
           "(e.startTime < :endTime AND e.endTime >= :endTime) OR " +
           "(e.startTime >= :startTime AND e.endTime <= :endTime))")
    List<Event> findConflictingEvents(@Param("labId") Long labId, 
                                     @Param("startTime") OffsetDateTime startTime, 
                                     @Param("endTime") OffsetDateTime endTime);

    @Query("SELECT e FROM Event e WHERE e.lab.id = :labId AND e.id != :eventId AND " +
           "((e.startTime <= :startTime AND e.endTime > :startTime) OR " +
           "(e.startTime < :endTime AND e.endTime >= :endTime) OR " +
           "(e.startTime >= :startTime AND e.endTime <= :endTime))")
    List<Event> findConflictingEventsExcluding(@Param("labId") Long labId, 
                                              @Param("eventId") Long eventId,
                                              @Param("startTime") OffsetDateTime startTime, 
                                              @Param("endTime") OffsetDateTime endTime);
}

