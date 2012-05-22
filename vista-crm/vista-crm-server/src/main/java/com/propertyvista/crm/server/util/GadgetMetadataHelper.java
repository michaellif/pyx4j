/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.dashboard.gadgets.type.ListerGadgetBaseMetadata;

public class GadgetMetadataHelper {

    public static List<Sort> getSortingCriteria(ListerGadgetBaseMetadata metadata) {

        List<Sort> sortingCriteria = new ArrayList<Sort>();

        if (!metadata.primarySortColumn().isNull()) {
            sortingCriteria.add(new Sort(metadata.primarySortColumn().propertyPath().getValue(), !metadata.sortAscending().getValue()));
        }

        return sortingCriteria;
    }

}
