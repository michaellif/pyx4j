/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface PropertySearchCriteria extends IEntity {

    @Caption(name = "Province")
    IPrimitive<String> province();

    @Caption(name = "City")
    IPrimitive<String> city();

    @Caption(name = "Beds")
    IPrimitive<Integer> numOfBeds();

    @Caption(name = "Baths")
    IPrimitive<Integer> numOfBath();

    @Caption(name = "Min Price")
    IPrimitive<Double> minPrice();

    @Caption(name = "Max Price")
    IPrimitive<Double> maxPrice();
}
