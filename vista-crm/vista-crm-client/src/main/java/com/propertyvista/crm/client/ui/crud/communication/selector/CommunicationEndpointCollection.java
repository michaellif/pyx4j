/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2014
 * @author arminea
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;

import com.propertyvista.dto.communication.CommunicationEndpointDTO;

public interface CommunicationEndpointCollection extends IEntity {

    IList<CommunicationEndpointDTO> to();
}
