package TicketingSystem.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by ApoorvaPetkar on 14-Oct-18.
 */
public class ReservationResponseDto {

    public Map<String, List<Seat>> seats;
    public Status status;

    public enum Status {
        SUCCESS, FAILED
    }

    public static class Seat{
        public Integer seatNo;
        public Seatstatus seatstatus;

        public enum Seatstatus {
            AVAILABLE, UNAVAILABLE
        }
    }

    public Map<String, List<Seat>> getSeats() {
        return seats;
    }

    public void setSeats(Map<String, List<Seat>> seats) {
        this.seats = seats;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
