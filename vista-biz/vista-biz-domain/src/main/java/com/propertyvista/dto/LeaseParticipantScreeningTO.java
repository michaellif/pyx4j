/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;

import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseParticipantScreeningTO.LeaseParticipantScreeningTOV;

/**
 * This is the view on CustomerScreening by LeaseParticipant id
 */
@Transient
@ExtendsBO(LeaseParticipant.class)
public interface LeaseParticipantScreeningTO extends IVersionedEntity<LeaseParticipantScreeningTOV> {

    @ToString
    CustomerScreening screening();

    LeaseParticipant<?> leaseParticipantId();

    @Transient
    public interface LeaseParticipantScreeningTOV extends IVersionData<LeaseParticipantScreeningTO> {
    }

}
