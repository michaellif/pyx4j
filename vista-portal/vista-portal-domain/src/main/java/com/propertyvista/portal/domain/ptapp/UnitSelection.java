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
package com.propertyvista.portal.domain.ptapp;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface UnitSelection extends IEntity, IBoundToApplication {

    @EmbeddedEntity
    UnitSelectionCriteria selectionCriteria();

    // Found by App server
    @Transient
    AvailableUnitsByFloorplan availableUnits();

    IPrimitive<Key> selectedUnitId();

    IPrimitive<Integer> selectedLeaseTerm();

    @Caption(name = "Start Rent Date")
    @NotNull
    IPrimitive<LogicalDate> rentStart();
}
