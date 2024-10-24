package com.example.expenseTracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class Due implements Comparable<Due> {

    private String uid;
    private String expenseId;
    private String itemName;
    private BigDecimal duePayment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dueDate;
    private String recipient;

    public Due(String uid, String itemName, BigDecimal duePayment, LocalDate dueDate, String recipient) {
        this.recipient = recipient;
        this.uid = uid;
        this.itemName = itemName;
        this.duePayment = duePayment;
        this.dueDate = dueDate;
    }

    @Override
    public int compareTo(Due due) {
        LocalDate compareDueDate = ((Due) due).getDueDate();

        // descending order
        return this.dueDate.compareTo(compareDueDate);
    }

    public static Comparator<Due> DueDateComparator = new Comparator<Due>() {

        public int compare(Due due1, Due due2) {

            LocalDate dueDate1 = due1.getDueDate();
            LocalDate dueDate2 = due2.getDueDate();

            // asscending order
            return dueDate1.compareTo(dueDate2);
        }

    };
}
