/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.tenant;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Showing;

@Transient
@ExtendsBO(Showing.class)
public interface ShowingDTO extends Showing {

    /**
     * Unit filter data for unit selection dialog
     */
    Building building();

    Floorplan floorplan();

    IPrimitive<LogicalDate> moveInDate();
}
