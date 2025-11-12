package com.unilab.repository;

import com.unilab.model.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    Optional<EventParticipant> findByEvent_IdAndUser_Id(Long eventId, Long userId);

    List<EventParticipant> findByUser_Id(Long userId);

    List<EventParticipant> findByEvent_Id(Long eventId);

    boolean existsByEvent_IdAndUser_Id(Long eventId, Long userId);

    @Query("SELECT ep FROM EventParticipant ep WHERE ep.user.id = :userId AND ep.status = 'JOINED'")
    List<EventParticipant> findActiveParticipationsByUserId(@Param("userId") Long userId);
}

