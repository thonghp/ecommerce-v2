package com.hpt.common.entity.order;

import com.hpt.common.entity.IdBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_track")
public class OrderTrack extends IdBasedEntity {
    @Column(length = 256)
    private String notes;
    private Date updatedTime;
    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private OrderStatus status;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public String toString() {
        return "OrderTrack{" + "notes='" + notes + '\'' + ", updatedTime=" + updatedTime + ", status=" + status +
                ", order=" + order + '}';
    }
    @Transient
    public String getUpdatedTimeOnForm() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        return dateFormat.format(this.updatedTime);
    }

    public void setUpdatedTimeOnForm(String updatedTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            this.updatedTime = dateFormat.parse(updatedTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
