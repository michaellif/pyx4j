/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface ApartmentFloorplan extends IEntity {

//    @Detached
//    Building building();
//
    @Caption(name = "Plan")
    IPrimitive<String> name();

//
//    /**
//     * Contains the pictures of the floorplan / model unit
//     */
//    @Owned
//    IList<Picture> pictures();
//
    /**
     * Approximate value of square ft. size of unit
     */
    IPrimitive<Integer> area();
}
