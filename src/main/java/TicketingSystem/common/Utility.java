package TicketingSystem.common;

import static TicketingSystem.dto.ReservationResponseDto.Seat.Seatstatus.UNAVAILABLE;
import static TicketingSystem.dto.ReservationResponseDto.Status.FAILED;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import TicketingSystem.dto.AvailableSeatsDto;
import TicketingSystem.dto.ReservationResponseDto;
import TicketingSystem.dto.ReservationResponseDto.Seat;
import TicketingSystem.persistence.Seat.Status;

/**
 * Created by ApoorvaPetkar on 14-Oct-18.
 */
public class Utility {

    public static List<Seat> getInvalidRowResponse(ReservationResponseDto responseDto, List<Integer> seats) {
        responseDto.status = FAILED;
        return seats.stream().map(seatNo -> {
            ReservationResponseDto.Seat seat = new ReservationResponseDto.Seat();
            seat.seatstatus = UNAVAILABLE;
            seat.seatNo = seatNo;
            return seat;
        }).collect(Collectors.toList());
    }

    public static boolean isForwardSeatAvailable(Integer numSeats, Integer seatNo, int i, TicketingSystem.persistence.Seat seat) {
        return seat.getStatus().equals(Status.RESERVED) ||
                (i != seatNo && (numSeats == 2 || (i != numSeats + seatNo - 1)) && seat.isAisle());
    }

    public static boolean isBackwardSeatAvailable(Integer numSeats, Integer seatNo, int i, TicketingSystem.persistence.Seat seat) {
        return seat.getStatus().equals(Status.RESERVED) || (i > 0 && (numSeats == 2 || i < seatNo) && seat.isAisle());
    }

    public static AvailableSeatsDto getEmptyAvailabilityResponseDto() {
        AvailableSeatsDto emptyDto = new AvailableSeatsDto();
        emptyDto.setAvailableSeats(Collections.emptyMap());
        return emptyDto;
    }
}
