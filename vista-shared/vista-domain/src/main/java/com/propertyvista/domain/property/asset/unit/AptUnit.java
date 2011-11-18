/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.shared.adapters.FloorplanCountersUpdateAdapter;

@DiscriminatorValue("Unit_BuildingElement")
public interface AptUnit extends BuildingElement {

    @NotNull
    @Detached
    @MemberColumn(modificationAdapters = { FloorplanCountersUpdateAdapter.class })
    @Indexed(group = { "b,11", "f" })
    Floorplan floorplan();

    /**
     * Denormalized field used for search, derived from @see AptUnitOccupancy
     * TODO should be calculated during Entity save
     * 
     * @deprecated remove deprecated once it is calculated and filled properly.
     */
    @Deprecated
    @Indexed
    @Format("MM/dd/yyyy")
    @Caption(name = "Availability")
    IPrimitive<LogicalDate> availableForRent();

    @ToString
    @EmbeddedEntity
    @Caption(name = "Information")
    AptUnitInfo info();

    @EmbeddedEntity
    AptUnitFinancial financial();

    @EmbeddedEntity
    Marketing marketing();
}
