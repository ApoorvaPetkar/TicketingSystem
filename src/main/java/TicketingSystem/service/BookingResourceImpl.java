package TicketingSystem.service;

import static TicketingSystem.common.Constants.RESERVED;
import static TicketingSystem.dto.ReservationResponseDto.Seat.Seatstatus.AVAILABLE;
import static TicketingSystem.dto.ReservationResponseDto.Seat.Seatstatus.UNAVAILABLE;
import static TicketingSystem.dto.ReservationResponseDto.Status.FAILED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import TicketingSystem.common.Constants;
import TicketingSystem.common.Utility;
import TicketingSystem.dto.AvailableSeatsDto;
import TicketingSystem.dto.ReservationResponseDto;
import TicketingSystem.dto.ScreenInfoDto;
import TicketingSystem.dto.ScreenInfoDto.RowInfo;
import TicketingSystem.dto.SeatInfoDto;
import TicketingSystem.persistence.Row;
import TicketingSystem.persistence.Screen;
import TicketingSystem.persistence.Seat;
import TicketingSystem.persistence.Seat.Status;
import TicketingSystem.repository.ScreenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by ApoorvaPetkar on 14-Oct-18.
 */

@Component
class BookingResourceImpl {

    @Autowired
    private ScreenRepository screenRepository;

    //Function which checks and updates the reservation details
    Screen getUpdatedScreen(SeatInfoDto reserveData, Screen screenByName, ReservationResponseDto responseDto) {
        Map<String, List<ReservationResponseDto.Seat>> responseSeatMap = new HashMap<>();
        List<Row> rowList = new ArrayList<>();

        reserveData.getSeats().forEach((rowName, seats) -> {
            List<ReservationResponseDto.Seat> responseSeats = new ArrayList<>();

            List<Row> rows = screenByName.getRows()
                    .stream()
                    .filter(r -> rowName.equalsIgnoreCase(r.getRowName()))
                    .collect(Collectors.toList());

            //Condition to check if invalid row name is provided while reserving tickets
            if (rows.isEmpty()) {
                List<ReservationResponseDto.Seat> seatList = Utility.getInvalidRowResponse(responseDto, seats);
                responseSeatMap.put(rowName, seatList);
            } else {
                rowList.addAll(rows.stream().map(row -> {
                    if (row.getRowName().equalsIgnoreCase(rowName)) {
                        List<Seat> seatList = row.getSeats().stream().map(s -> {
                            if (seats.contains(s.getSeatId())) {
                                ReservationResponseDto.Seat rSeat = new ReservationResponseDto.Seat();
                                rSeat.seatNo = s.getSeatId();
                                if (s.getStatus().equals(Status.UN_RESERVED)) {
                                    s.setStatus(Status.RESERVED);
                                    rSeat.seatstatus = AVAILABLE;
                                } else {
                                    responseDto.setStatus(FAILED);
                                    rSeat.seatstatus = UNAVAILABLE;
                                }
                                responseSeats.add(rSeat);
                            }
                            return s;
                        }).collect(Collectors.toList());
                        row.setSeats(seatList);
                    }
                    return row;
                }).collect(Collectors.toList()));
                responseSeatMap.put(rowName, responseSeats);
            }
        });
        screenByName.setRows(rowList);
        responseDto.setSeats(responseSeatMap);
        return screenByName;
    }

    //Converts API exposed DTO to persistance Object
    //So that this can be used to save in database
    Screen getScreenPersistenceObject(@RequestBody ScreenInfoDto screenData) {
        Screen screen = new Screen();
        List<Row> rows = new ArrayList<>();
        screen.setName(screenData.name);
        Map<String, RowInfo> seatInfo = screenData.seatInfo;

        seatInfo.forEach((rowName, rowInfo) -> {
            List<Seat> seats = new ArrayList<>();
            Row row = new Row();
            row.setRowName(rowName);
            row.setNoOfSeats(rowInfo.numberOfSeats);

            //Create row entities as per number of seats provided
            IntStream.range(0, rowInfo.numberOfSeats).forEach(seatNumber -> {
                Seat seat = new Seat();
                seat.setStatus(Status.UN_RESERVED);
                seat.setAisle(rowInfo.aisleSeats.contains(seatNumber) ? Boolean.TRUE : Boolean.FALSE);
                seat.setSeatId(seatNumber);
                seats.add(seat);
            });

            row.setSeats(seats);
            rows.add(row);
        });

        screen.setRows(rows);
        return screen;
    }

    //Get available seat info without any particular choice
    void updateSeatAvailabilityInfo(Screen screenByName, SeatInfoDto seatInfoDto, String status) {
        Map<String, List<Integer>> seats = new HashMap<>();
        screenByName.getRows().forEach(row -> {
            List<Integer> availableSeats = row.getSeats().stream()
                    .filter(s -> s.getStatus().equals(status.equals(RESERVED) ? Status.RESERVED : Status.UN_RESERVED))
                    .map(Seat::getSeatId).collect(Collectors.toList());
            seats.put(row.getRowName(), availableSeats);
        });
        seatInfoDto.setSeats(seats);
    }

    //Get available seat info with choice of seats provided
    Boolean checkSeatAvailablity(Screen screenByName,
                                 AvailableSeatsDto availableSeatsDto,
                                 Integer numSeats,
                                 String rowName,
                                 Integer seatNo) {

        boolean isAvailable = Boolean.TRUE;
        Map<String, List<Integer>> availableSeats = new HashMap<>();
        List<Integer> responseSeats = new ArrayList<>();
        for (Row row : screenByName.getRows()) {
            if (row.getRowName().equalsIgnoreCase(rowName) && numSeats < row.getNoOfSeats()
                    && seatNo < row.getNoOfSeats() && seatNo >= 0 && numSeats >= 0) {
                //forward seats
                if ((seatNo + numSeats) <= row.getNoOfSeats()) {
                    for (int i = seatNo; i < numSeats + seatNo; i++) {
                        Seat seat = row.getSeats().get(i);
                        responseSeats.add(i);
                        if (Utility.isForwardSeatAvailable(numSeats, seatNo, i, seat)) {
                            isAvailable = Boolean.FALSE;
                        }
                    }
                }
                // backward seats
                if (((seatNo - (numSeats - 1)) >= 0) && isAvailable == Boolean.FALSE) {
                    isAvailable = Boolean.TRUE;
                    responseSeats.clear();
                    for (int i = 0; i < seatNo; i++) {
                        Seat seat = row.getSeats().get(i);
                        responseSeats.add(i);
                        if (Utility.isBackwardSeatAvailable(numSeats, seatNo, i, seat)) {
                            isAvailable = Boolean.FALSE;
                        }
                    }
                }
                availableSeats.put(rowName, responseSeats);
            }
        }
        availableSeatsDto.setAvailableSeats(availableSeats);
        return isAvailable;
    }
}
