/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.policy;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;

public interface PolicyPresetAtNode extends IEntity {
    public enum NodeType {
        unit, building, complex, region, country, organization;
    }

    IPrimitive<NodeType> nodeType();

    @Detached
    AptUnit unit();

    @Detached
    Building building();

    @Detached
    Complex complex();

    @Detached
    Country country();

    @Detached
    PolicyPreset policyPreset();

}
