/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;

@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface AgreementSignatures extends IEntity {

    @Owner
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    @MemberColumn(notNull = true)
    LeaseTermParticipant<?> leaseTermTenant();
}
