/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author stanp
 */
package com.propertyvista.biz.legal.eviction;

import java.util.List;

import com.propertyvista.domain.eviction.EvictionCase;
import com.propertyvista.domain.eviction.EvictionCaseStatus;
import com.propertyvista.domain.eviction.EvictionDocument;
import com.propertyvista.domain.tenant.lease.Lease;

/**
 * Provides Eviction Case Management interface for a Lease
 */
public interface EvictionCaseFacade {

    EvictionCase openEvictionCase(Lease leaseId, String note);

    void closeEvictionCase(EvictionCase caseId, String note);

    EvictionCase getLastEvictionCase(Lease leaseId);

    EvictionCase getCurrentEvictionCase(Lease leaseId);

    EvictionCaseStatus getCurrentEvictionStatus(EvictionCase evictionCase);

    EvictionCaseStatus getCurrentEvictionStatus(Lease leaseId);

    void addEvictionStatusDetails(EvictionCaseStatus evictionStatus, String note, List<EvictionDocument> attachments);

    EvictionCaseStatus addEvictionStatusDetails(EvictionCase evictionCase, String statusName, String note, List<EvictionDocument> attachments);

    List<EvictionCase> getEvictionHistory(Lease leaseId);

}
