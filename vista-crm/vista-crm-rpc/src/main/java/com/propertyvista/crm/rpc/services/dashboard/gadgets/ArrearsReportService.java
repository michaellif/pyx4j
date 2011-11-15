/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.dashboard.gadgets;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;

public interface ArrearsReportService extends IService {

    /**
     * Calculate arrears.
     * 
     * @param callback
     * @param buidlings
     *            PK's of buildings to choose from.
     * @param when
     *            calculate arrears as on this date.
     */
    void arrearsList(AsyncCallback<EntitySearchResult<MockupArrearsState>> callback, Vector<Key> buildingPKs, LogicalDate when, Vector<Sort> sortingCriteria,
            int pageNumber, int pageSize);
}
