/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

@Transient
public interface PropertyDetailsDTO extends PropertyDTO {

    IList<FloorplanDTO> floorplans();

    IList<AmenityDTO> amenities();
}
