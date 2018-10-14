package TicketingSystem.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by slk on 14-Oct-18.
 */
public class AvailableSeatsDto {
    public Map<String, List<Integer>> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Map<String, List<Integer>> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Map<String, List<Integer>> availableSeats;

}
