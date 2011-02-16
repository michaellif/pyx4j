/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.pt;

import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.rpc.shared.Service;

public interface PotencialTenantServices {

    public interface UnitExists extends Service<UnitSelectionCriteria, Boolean> {

    }

    public interface GetCurrentApplication extends Service<UnitSelectionCriteria, Application> {

    }

    /**
     * For User in Session find current(*) Application Object.
     * 
     * For Application find UnitSelection Object. If criteria Objects are different then
     * use one from Request.
     * 
     * Assume that building is provided in request.
     * 
     * Build the AvalableUnitsByFloorplan object.
     */
    public interface GetAvalableUnits extends Service<UnitSelection, UnitSelection> {

    }

    public interface Save extends EntityServices.Save {

    };

    public interface RetrieveByPK extends EntityServices.RetrieveByPK {

    };
}
