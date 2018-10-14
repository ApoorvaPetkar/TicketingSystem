package TicketingSystem.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by ApoorvaPetkar on 14-Oct-18.
 */
public class SeatInfoDto {
    public Map<String, List<Integer>> getSeats() {
        return seats;
    }

    public void setSeats(Map<String, List<Integer>> seats) {
        this.seats = seats;
    }

    public Map<String, List<Integer>> seats;

}
