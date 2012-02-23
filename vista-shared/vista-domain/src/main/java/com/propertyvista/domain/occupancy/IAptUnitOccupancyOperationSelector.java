/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.occupancy;

import java.util.List;

import com.propertyvista.domain.occupancy.operations.OpScopeAvailable;
import com.propertyvista.domain.occupancy.operations.OpScopeOffMarket;
import com.propertyvista.domain.occupancy.operations.OpScopeRenovation;

/**
 * Should be implemented on client side to configure and prepare the operations (i.e. set up the desired date or something like that) for application.
 * 
 * @author ArtyomB
 */
public interface IAptUnitOccupancyOperationSelector {

    void populate(List<IAptUnitOccupancyOperation> operations);

    void populate(IAptUnitOccupancyOperation operation);

    void populateOpScopeAvaialble(OpScopeAvailable op);

    void populateOpScopeOffMarket(OpScopeOffMarket op);

    void populateOpScopeRenovation(OpScopeRenovation op);
}
