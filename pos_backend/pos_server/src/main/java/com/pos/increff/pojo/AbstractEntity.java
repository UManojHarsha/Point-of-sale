package com.pos.increff.pojo;

import java.time.ZonedDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.pos.increff.util.DateTimeUtils;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {
    @Column(name = "created_date", nullable = false, updatable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @Column(name = "updated_date", nullable = false)
    private ZonedDateTime updatedDate = ZonedDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedDate = DateTimeUtils.getCurrentUtcDate();
    }
}
