package com.robusta.commons.persistence;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class Entity {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "createdBy", column = @Column(name = "CREATED_BY")),
            @AttributeOverride(name = "createdDate", column = @Column(name = "CREATED_DATE")),
            @AttributeOverride(name = "updatedBy", column = @Column(name = "UPDATED_BY")),
            @AttributeOverride(name = "updatedDate", column = @Column(name = "UPDATED_DATE"))
    })
    private AuditFields auditFields;

    public Entity() {
        this.auditFields = new AuditFields();
    }

    public void setCreatedBy(String createdBy) {
        auditFields.setCreatedBy(createdBy);
    }

    public void setCreatedDate(Date createdDate) {
        auditFields.setCreatedDate(createdDate);
    }

    public void setUpdatedBy(String updatedBy) {
        auditFields.setUpdatedBy(updatedBy);
    }

    public void setUpdatedDate(Date updatedDate) {
        auditFields.setUpdatedDate(updatedDate);
    }
}
