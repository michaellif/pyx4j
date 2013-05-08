/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.maintenance.MaintenanceRequest;

@Transient
@ExtendsDBO
public interface MaintenanceRequestDTO extends MaintenanceRequest {

    @Caption(name = "Problem in my Apartment")
    IPrimitive<Boolean> reportedForOwnUnit();
}
