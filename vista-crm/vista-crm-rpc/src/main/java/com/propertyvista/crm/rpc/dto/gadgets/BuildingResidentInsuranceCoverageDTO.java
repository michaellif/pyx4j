/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface BuildingResidentInsuranceCoverageDTO extends IEntity {

    Building buildingFilter();

    IPrimitive<String> building();

    IPrimitive<String> complex();

    IPrimitive<Integer> units();

    @Format("#,##0")
    @Caption(name = "# with insurance")
    IPrimitive<Integer> unitsWithInsuranceCount();

    @Editor(type = EditorType.percentage)
    @Caption(name = "% with insurance")
    @Format("#0.00%")
    IPrimitive<Double> unitsWithInsuranceShare();

}
