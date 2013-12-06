/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.selections;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface BuildingForSelectionDTO extends IEntity {

    Building buildingIdStub();

    IPrimitive<String> propertyCode();

    IPrimitive<String> name();

    IPrimitive<String> address();

    // TODO add portfolios it belongs to 
}
