package com.wingedtech.common.domain;

public interface AuditingEntity {
    String FIELD_CREATED_BY = "createdBy";
    String FIELD_CREATED_DATE = "createdDate";
    String FIELD_LAST_MODIFIED_BY = "lastModifiedBy";
    String FIELD_LAST_MODIFIED_DATE = "lastModifiedDate";

    String getCreatedBy();

    java.time.Instant getCreatedDate();

    String getLastModifiedBy();

    java.time.Instant getLastModifiedDate();
}
