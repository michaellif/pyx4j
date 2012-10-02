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

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.dashboard.gadgets.type.base.ListerGadgetBaseMetadata;

public class Util {

    public static Vector<Sort> getSortingCriteria(ListerGadgetBaseMetadata listerGadgetBaseMetadata) {
        final Vector<Sort> sortingCriteria = new Vector<Sort>();
        if (listerGadgetBaseMetadata.primarySortColumn().propertyPath().getValue() != null) {
            sortingCriteria.add(new Sort(listerGadgetBaseMetadata.primarySortColumn().propertyPath().getValue(), !listerGadgetBaseMetadata.sortAscending()
                    .isBooleanTrue()));
        }
        return sortingCriteria;
    }

}
