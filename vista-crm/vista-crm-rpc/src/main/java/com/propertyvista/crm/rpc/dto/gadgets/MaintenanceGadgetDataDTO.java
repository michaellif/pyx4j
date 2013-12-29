/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface MaintenanceGadgetDataDTO extends IEntity {

    IPrimitive<Integer> openWorkOrders();

    IPrimitive<Integer> urgentWorkOrders();

    @Caption(name = "Outstanding Work Orders 1 to 2 Days")
    IPrimitive<Integer> outstandingWorkOrders1to2days();

    @Caption(name = "Outstanding Work Orders 2 to 3 Days")
    IPrimitive<Integer> outstandingWorkOrders2to3days();

    @Caption(name = "Outstanding Work Orders Over 3 Days")
    IPrimitive<Integer> outstandingWorkOrdersMoreThan3days();

}
