/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports;

import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class Util {

    // TODO this function must be destroyed:  this has to be refactored on more generic level (for now it's just a HACK)
    @Deprecated
    public static Vector<Building> asStubs(Vector<Key> selectedBuildings) {
        Vector<Building> stubs = new Vector<Building>();
        for (Key key : selectedBuildings) {
            Building stub = EntityFactory.create(Building.class);
            stub.setPrimaryKey(key);
            stub.setAttachLevel(AttachLevel.IdOnly);
        }
        return stubs;
    }

    public static Vector<Sort> getSortingCriteria(ListerGadgetBaseMetadata listerGadgetBaseMetadata) {
        final Vector<Sort> sortingCriteria = new Vector<Sort>();
        if (listerGadgetBaseMetadata.primarySortColumn().propertyPath().getValue() != null) {
            sortingCriteria.add(new Sort(listerGadgetBaseMetadata.primarySortColumn().propertyPath().getValue(), !listerGadgetBaseMetadata.sortAscending()
                    .isBooleanTrue()));
        }
        return sortingCriteria;
    }

}
