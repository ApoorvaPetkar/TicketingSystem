package TicketingSystem.service;


import static TicketingSystem.dto.ReservationResponseDto.Status.SUCCESS;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import TicketingSystem.NotFoundException;
import TicketingSystem.dto.AvailableSeatsDto;
import TicketingSystem.dto.ReservationResponseDto;
import TicketingSystem.dto.ScreenInfoDto;
import TicketingSystem.dto.SeatInfoDto;
import TicketingSystem.persistence.Row;
import TicketingSystem.persistence.Screen;
import TicketingSystem.repository.RowRepository;
import TicketingSystem.repository.ScreenRepository;
import TicketingSystem.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class BookingResource {

	@Autowired
	private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private RowRepository rowRepository;

    @Autowired
    BookingResourceImpl bookingResourceImpl;

	@GetMapping("/screen")
	public List<Screen> retrieveAllScreens() {
		return screenRepository.findAll();
	}

    @GetMapping("/screen/rows")
    public List<Row> getAllRows() {
        return rowRepository.findAll();
    }

    @GetMapping("/screen/{screenName}")
    public Screen getScreen(@PathVariable String screenName) {
        Screen screenByName = screenRepository.getScreenByName(screenName);
        if (screenByName == null){
            throw new NotFoundException(screenName);
        }
        return screenByName;
    }

    @GetMapping("/screens/{screenName}/seatss")
    public ResponseEntity<?> getAvailableSeats(@PathVariable String screenName,
                                    @RequestParam(required = true, value = "status") String status) {

	    SeatInfoDto seatInfoDto = new SeatInfoDto();

        if (Objects.equals(status, "reserved") || Objects.equals(status, "unreserved")){
            Screen screenByName = screenRepository.getScreenByName(screenName);
            if (screenByName == null){
                throw new NotFoundException(screenName);
            }
            bookingResourceImpl.updateSeatAvailabilityInfo(screenByName, seatInfoDto, status);
            return new ResponseEntity<>(seatInfoDto, HttpStatus.OK);
        }

        return new ResponseEntity<>("Valid status is required as paramter", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @GetMapping("/screens/{screenName}/seats")
    public ResponseEntity<?> getAvailableSeatsAtGivePosition(@PathVariable String screenName,
                                                             @RequestParam(required = true, value = "numSeats") Integer numSeats,
                                                             @RequestParam(required = true, value = "choice") String choice) {
        AvailableSeatsDto availableSeatsDto = new AvailableSeatsDto();
        Screen screenByName = screenRepository.getScreenByName(screenName);
        if (screenByName == null) {
            throw new NotFoundException(screenName);
        }
        char rowName = choice.charAt(0);
        if ((rowName >= 'A' && rowName <= 'Z')){
            Integer seatNo = Integer.valueOf(choice.substring(1));
            Boolean isAvailable = bookingResourceImpl.updateAvailableSeatsInfoPerChoice(screenByName, availableSeatsDto,
                    numSeats, choice.substring(0, 1), seatNo);
            if(isAvailable){
                return new ResponseEntity<>(availableSeatsDto, HttpStatus.OK);
            }
            AvailableSeatsDto emptyDto = new AvailableSeatsDto();
            emptyDto.setAvailableSeats(Collections.emptyMap());
            return new ResponseEntity<>(emptyDto, HttpStatus.OK);

        }else{
            return new ResponseEntity<>("Invalid Row Name Added as choice", HttpStatus.OK);
        }
    }

    @PostMapping("/screen/{screenName}/reserve")
    public ResponseEntity<?> reserveSeats(@RequestBody SeatInfoDto reserveData, @PathVariable String screenName) {

        Screen screenByName = screenRepository.getScreenByName(screenName);
        if (screenByName == null){
            throw new NotFoundException(screenName);
        }
        ReservationResponseDto responseDto = new ReservationResponseDto();
        responseDto.status = SUCCESS;
        Screen updatedScreen = bookingResourceImpl.getUpdatedScreen(reserveData, screenByName, responseDto);

        if (responseDto.getStatus().equals(SUCCESS)) {
            screenRepository.save(updatedScreen);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/screen")
    public ResponseEntity<Object> createScreen(@RequestBody ScreenInfoDto screenData) {
        Screen screen = screenRepository.save(bookingResourceImpl.getScreenPersistenceObject(screenData));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(screen.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

}
