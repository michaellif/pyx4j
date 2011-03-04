/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-24
 * @author aroytbur
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

/**
 * Contains rules to be used for a given property. The data will come from PM configs
 * during building setup.
 * 
 * TODO For now just add petChargeRule there, but need more tules
 * 
 * @author aroytbur
 * 
 */
public interface PropertyProfile extends IEntity {

    /**
     * Distinguished by name. PM can create a number of profiles in the system.
     * 
     * TODO perhaps we need to have parallel object in the domain that is associated with
     * the building
     * 
     * @return
     */

    PetChargeRule petCharge();

    IPrimitive<Integer> petsMaximum();

}
