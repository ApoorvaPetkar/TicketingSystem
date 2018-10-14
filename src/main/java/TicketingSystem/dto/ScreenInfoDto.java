package TicketingSystem.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by ApoorvaPetkar on 14-Oct-18.
 */
public class ScreenInfoDto {

    public String name;
    public Map<String, RowInfo> seatInfo;

    public static class RowInfo {
        public int numberOfSeats;
        public List<Integer> aisleSeats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, RowInfo> getSeatInfo() {
        return seatInfo;
    }

    public void setSeatInfo(Map<String, RowInfo> seatInfo) {
        this.seatInfo = seatInfo;
    }
}
