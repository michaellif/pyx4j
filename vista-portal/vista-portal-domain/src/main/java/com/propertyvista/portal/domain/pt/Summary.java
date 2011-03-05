/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-21
 * @author jim
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Summary extends IEntity, IApplicationEntity {

    @Transient
    UnitSelection unitSelection();

    @Transient
    PotentialTenantList tenants();

    @Transient
    PotentialTenantList tenants2();

    // TODO this would be PotentialTenantFinancialList after tabs are created.
    @Transient
    @EmbeddedEntity
    PotentialTenantFinancial financial();

    @Transient
    Pets pets();

    @Transient
    @EmbeddedEntity
    Charges charges();

    LeaseTerms leaseTerms();

    @Caption(name = "I Agree")
    @NotNull
    IPrimitive<Boolean> agree();

    @Caption(name = "Type Your Full Name")
    @NotNull
    IPrimitive<String> fullName();
}
