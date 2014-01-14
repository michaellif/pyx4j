/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.property;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.note.HasNotesAndAttachments;
import com.propertyvista.domain.property.asset.building.Building;

@DiscriminatorValue("Landlord")
public interface Landlord extends IEntity, HasNotesAndAttachments {

    @NotNull
    @ToString
    @Caption(name = "Legal Name")
    IPrimitive<String> name();

    AddressStructured address();

    IPrimitive<String> website();

    @Owned
    @Detached
    LandlordMedia logo();

    @Owned
    @Detached
    LandlordMedia signature();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = Building.class)
    @OrderBy(PrimaryKey.class)
    IList<Building> buildings();

    @Timestamp
    IPrimitive<Date> updated();
}
