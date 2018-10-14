package TicketingSystem.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Created by ApoorvaPetkar on 14-Oct-18.
 */
@Entity
public class Row implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "rowId")
    private Long id;

    @Column(name = "rowName")
    private String rowName;

    @Column(name = "noOfSeats")
    private int noOfSeats;

    @OneToMany(targetEntity = Seat.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "rowId")
    private List<Seat> seats;

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seat) {
        this.seats = seat;
    }
}
