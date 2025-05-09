package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
public class AbstractVersionedPojo {
    private ZonedDateTime createdAt = ZonedDateTime.now();
    private ZonedDateTime updatedAt = ZonedDateTime.now();
    @Version
    private Integer version;
}
