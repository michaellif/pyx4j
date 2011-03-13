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

import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.rpc.shared.Service;

@Deprecated
public interface PotentialTenantServices {

    @Deprecated
    public interface UnitExists extends Service<UnitSelectionCriteria, Boolean> {

    }

    @Deprecated
    public interface GetCurrentApplication extends Service<UnitSelectionCriteria, CurrentApplication> {

    }

    @Deprecated
    public interface Save extends EntityServices.Save {

    };

    @Deprecated
    public interface RetrieveByPK extends EntityServices.RetrieveByPK {

    };

    @Deprecated
    public interface RetrieveUnitSelection extends Service<UnitSelectionCriteria, UnitSelection> {

    }
}
