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
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.prospect.dto;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface UnitSelectionDTO extends IEntity {

    Building building();

    Floorplan floorplan();

    @Caption(name = "Move-in Date")
    IPrimitive<LogicalDate> moveIn();

    //---------------------------------------------

    IPrimitive<Integer> bedrooms();

    IPrimitive<Integer> bathrooms();

    @Transient
    public interface UnitTO extends IEntity {

        @Editor(type = Editor.EditorType.label)
        IPrimitive<String> display();

        @ToString(index = 0)
        IPrimitive<String> number();

        IPrimitive<String> floorplan();

        IPrimitive<Integer> floor();

        @ToString(index = 1)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<Integer> bedrooms();

        @ToString(index = 2)
        @Editor(type = Editor.EditorType.label)
        IPrimitive<Integer> bathrooms();

        @ToString(index = 3)
        IPrimitive<LogicalDate> available();

        @ToString(index = 4)
        @Format("#,##0.00")
        @Editor(type = EditorType.label)
        IPrimitive<BigDecimal> price();
    }

    IList<UnitTO> currentUnits();

    IList<UnitTO> availableUnits();

    @Caption(name = "Unit")
    @Editor(type = Editor.EditorType.label)
    UnitTO selectedUnit();
}
