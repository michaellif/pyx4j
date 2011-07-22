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
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.portal.domain.dto.AptUnitDTO;

public interface Summary extends IEntity, IBoundToApplication {

    @Transient
    UnitSelection unitSelection();

    @Transient
    AptUnitDTO selectedUnit();

    @Transient
    PotentialTenantList tenantList();

    @Transient
    PotentialTenantList tenantsWithInfo();

    @Transient
    @EmbeddedEntity
    IList<SummaryPotentialTenantFinancial> tenantFinancials();

    @Transient
    PetsDTO pets();

    @Transient
    @EmbeddedEntity
    Charges charges();

    LeaseTerms leaseTerms();

    @NotNull
    @Caption(name = "I Agree")
    IPrimitive<Boolean> agree();

    @NotNull
    @Caption(name = "Type Your Full Name")
    IPrimitive<String> fullName();
}
