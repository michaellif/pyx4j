/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.financial;

import com.propertyvista.domain.tenant.lease.BillableItem;

public class AbstractProcessor {

    protected boolean sameBillableItem(BillableItem billableItem1, BillableItem billableItem2) {
        return billableItem1.uid().equals(billableItem2.uid());
    }
}
