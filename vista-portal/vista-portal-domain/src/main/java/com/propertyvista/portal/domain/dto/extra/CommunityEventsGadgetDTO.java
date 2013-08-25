/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.domain.dto.extra;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.portal.domain.dto.CommunityEventDTO;

@Transient
public interface CommunityEventsGadgetDTO extends ExtraGadgetDTO {

    IList<CommunityEventDTO> events();

}