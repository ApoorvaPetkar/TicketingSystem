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
public class Screen implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "screenId")
    private Long id;

    @Column(name = "screenName")
    private String name;

    @OneToMany(targetEntity = Row.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "screenId")
    private List<Row> rows;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
}
