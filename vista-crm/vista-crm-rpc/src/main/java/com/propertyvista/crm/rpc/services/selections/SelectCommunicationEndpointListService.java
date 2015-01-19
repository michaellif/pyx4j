/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 18, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.rpc.services.selections;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.AbstractListCrudService;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.dto.communication.CommunicationEndpointDTO;

public interface SelectCommunicationEndpointListService extends AbstractListCrudService<CommunicationEndpoint> {

    void getEndpointForSelection(AsyncCallback<Vector<CommunicationEndpointDTO>> callback, EntityListCriteria<CommunicationEndpointDTO> criteria);

}
