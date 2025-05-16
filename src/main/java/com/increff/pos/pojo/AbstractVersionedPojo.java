package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Getter
@Setter
@MappedSuperclass
public class AbstractVersionedPojo {
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private ZonedDateTime createdAt = ZonedDateTime.now(UTC_ZONE);
    private ZonedDateTime updatedAt = ZonedDateTime.now(UTC_ZONE);
    
    @Version
    private Integer version;
}
