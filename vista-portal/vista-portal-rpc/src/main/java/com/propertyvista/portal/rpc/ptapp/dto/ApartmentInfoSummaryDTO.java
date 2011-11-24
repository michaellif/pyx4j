/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface ApartmentInfoSummaryDTO extends IEntity {
    IPrimitive<String> floorplan();

    IPrimitive<String> address();

    IPrimitive<String> bedrooms();

    IPrimitive<String> dens();

    @Caption(name = "Landlord")
    IPrimitive<String> landlordName();
}
