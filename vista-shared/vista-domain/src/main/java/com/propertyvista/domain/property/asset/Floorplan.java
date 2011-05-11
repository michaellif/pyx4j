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
package com.propertyvista.domain.property.asset;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.Picture;

public interface Floorplan extends IEntity {

    @Detached
    Building building();

    @Caption(name = "Plan")
    IPrimitive<String> name();

    /**
     * Contains the pictures of the floorplan / model unit
     */
    @Owned
    @Deprecated
    //TODO VladS to clean it up
    IList<Picture> pictures();

    /**
     * Min value of square ft. size of unit
     */
    IPrimitive<Integer> minArea();

    /**
     * Max value of square ft. size of unit
     */
    IPrimitive<Integer> maxArea();

}
