package com.robusta.commons.persistence;

import java.util.Date;

public interface Creatable {
    void setCreatedBy(String createdBy);
    void setCreatedDate(Date createdDate);
}

