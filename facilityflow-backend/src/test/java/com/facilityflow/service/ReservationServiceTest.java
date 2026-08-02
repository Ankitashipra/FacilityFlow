package com.facilityflow.service;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.ReservationRequest;
import com.facilityflow.entity.Reservation;
import com.facilityflow.entity.ReservationStatus;
import com.facilityflow.entity.Room;
import com.facilityflow.entity.User;
import com.facilityflow.exception.BusinessRuleException;
import com.facilityflow.mapper.ReservationMapper;
import com.facilityflow.repository.ReservationRepository;
import com.facilityflow.repository.RoomRepository;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.facilityflow.security.UserPrincipal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private NotificationService notificationService;
    @Mock private AuditService auditService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Room room;
    private User requester;

    @BeforeEach
    void setUp() {
        room = Room.builder().name("Falcon Meeting Room").build();
        room.setId(10L);

        requester = User.builder().fullName("Karan Verma").email("employee@facilityflow.com").build();
        requester.setId(4L);

        UserPrincipal principal = new UserPrincipal(requester);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void create_throwsBusinessRuleException_whenRoomAlreadyBookedForOverlappingSlot() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .roomId(10L)
                .purpose("Sprint planning")
                .startTime(start)
                .endTime(end)
                .attendeeCount(5)
                .build();

        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));
        when(reservationRepository.findOverlapping(eq(10L), any(), any(), isNull()))
                .thenReturn(List.of(mock(Reservation.class)));

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("already booked");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void create_succeeds_whenNoOverlap() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(1);

        ReservationRequest request = ReservationRequest.builder()
                .roomId(10L)
                .purpose("Sprint planning")
                .startTime(start)
                .endTime(end)
                .attendeeCount(5)
                .build();

        when(roomRepository.findById(10L)).thenReturn(Optional.of(room));
        when(reservationRepository.findOverlapping(eq(10L), any(), any(), isNull())).thenReturn(List.of());
        when(userRepository.findById(4L)).thenReturn(Optional.of(requester));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        reservationService.create(request);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void create_throwsBusinessRuleException_whenEndTimeBeforeStartTime() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        ReservationRequest request = ReservationRequest.builder()
                .roomId(10L)
                .purpose("Invalid")
                .startTime(start)
                .endTime(start.minusHours(1))
                .build();

        assertThatThrownBy(() -> reservationService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("End time must be after start time");

        verifyNoInteractions(reservationRepository);
    }

}
