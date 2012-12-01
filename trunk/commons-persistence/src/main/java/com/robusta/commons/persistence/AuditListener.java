package com.robusta.commons.persistence;

import com.robusta.commons.context.UserContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public class AuditListener {
    @PrePersist
    public void creation(Object entity) {
        checkArgument(entity != null);
        if(Creatable.class.isAssignableFrom(entity.getClass())) {
            Date now = new Date();
            Creatable creatable = (Creatable) entity;
            creatable.setCreatedBy(UserContextHolder.getCurrentUsername());
            creatable.setCreatedDate(now);
        }
    }
    @PreUpdate
    public void updation(Object entity) {
        checkArgument(entity != null);
        if(Updatable.class.isAssignableFrom(entity.getClass())) {
            Date now = new Date();
            Updatable updatable = (Updatable) entity;
            updatable.setUpdatedBy(UserContextHolder.getCurrentUsername());
            updatable.setUpdatedDate(now);
        }
    }
}
