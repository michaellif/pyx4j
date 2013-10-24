/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.domain.tenant.insurance;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IUserEntity;
import com.propertyvista.domain.tenant.lease.Tenant;

@AbstractEntity
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
@DiscriminatorValue("InsurancePolicy")
public interface InsurancePolicy<INSURANCE_CERTIFICATE extends InsuranceCertificate<?>> extends IEntity, IUserEntity {

    @Caption(name = "Owned By")
    @Owner
    @JoinColumn
    @ReadOnly
    @Detached
    Tenant tenant();

    @Owned
    INSURANCE_CERTIFICATE certificate();

    /**
     * Indicates if this Certificate is deleted or never Activated
     */
    IPrimitive<Boolean> isDeleted();

}
