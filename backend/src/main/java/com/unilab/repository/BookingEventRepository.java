package com.unilab.repository;

import com.unilab.model.BookingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingEventRepository extends JpaRepository<BookingEvent, Long> {

    Optional<BookingEvent> findByBooking_Id(Long bookingId);

    Optional<BookingEvent> findByEvent_Id(Long eventId);

    boolean existsByBooking_Id(Long bookingId);

    @Query("SELECT be FROM BookingEvent be WHERE be.event.id = :eventId")
    List<BookingEvent> findByEventId(@Param("eventId") Long eventId);
}

