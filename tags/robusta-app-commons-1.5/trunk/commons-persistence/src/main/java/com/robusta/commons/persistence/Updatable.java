package com.robusta.commons.persistence;

import java.util.Date;

public interface Updatable {
    void setUpdatedBy(String updatedBy);
    void setUpdatedDate(Date updatedDate);
}
