/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;

import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
@ToStringFormat("{0}, {1}")
public interface LeaseParticipanApprovalDTO extends IEntity {

    @ToString(index = 0)
    @SuppressWarnings("rawtypes")
    @Detached(level = AttachLevel.ToStringMembers)
    LeaseTermParticipant leaseParticipant();

    @Detached(level = AttachLevel.ToStringMembers)
    LeaseParticipantScreeningTO screening();

    @ToString(index = 1)
    CustomerCreditCheck creditCheck();
}
