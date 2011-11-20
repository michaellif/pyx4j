/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.ptapp;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IUserEntity;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Status;

/**
 * This is an application progress for tenant, secondary tenant and guarantors.
 */
public interface Application extends IUserEntity {

    @Owned
    IList<ApplicationWizardStep> steps();

    IPrimitive<Status> status();

    @Detached
    Lease lease();

    @Owner
    @Detached
    @ReadOnly
    MasterApplication belongsTo();

    DigitalSignature signature();
}
