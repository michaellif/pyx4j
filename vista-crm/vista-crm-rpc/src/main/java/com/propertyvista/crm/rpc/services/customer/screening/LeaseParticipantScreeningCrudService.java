/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.customer.screening;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;

import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

/**
 * Actual data is retried by LeaseParticipant id versioned part is CustomerScreening
 */
public interface LeaseParticipantScreeningCrudService extends AbstractVersionedCrudService<LeaseParticipantScreeningTO> {

    @Transient
    public interface CustomerScreeningInitializationData extends InitializationData {

        LeaseParticipant<?> leaseParticipantId();

    }
}
