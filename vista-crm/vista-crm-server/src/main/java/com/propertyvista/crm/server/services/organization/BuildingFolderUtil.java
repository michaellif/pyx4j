/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.organization;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.property.asset.building.Building;

class BuildingFolderUtil {

    static void stripExtraData(IList<Building> buildings) {
        List<Building> r = new ArrayList<Building>();
        for (Building b : buildings) {
            Building bs = b.createIdentityStub();
            bs.setAttachLevel(AttachLevel.Attached);
            bs.propertyCode().setValue(b.propertyCode().getValue());
            bs.info().name().setValue(b.info().name().getValue());
            bs.info().type().setValue(b.info().type().getValue());
            r.add(bs);
        }
        buildings.clear();
        buildings.addAll(r);
    }
}
