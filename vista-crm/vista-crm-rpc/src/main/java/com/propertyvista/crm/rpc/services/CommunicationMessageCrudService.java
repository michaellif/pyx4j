/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.AbstractCrudService;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.dto.MessagesDTO;

public interface CommunicationMessageCrudService extends AbstractCrudService<CommunicationMessageDTO> {
    void retreiveCommunicationMessages(AsyncCallback<MessagesDTO> callback, boolean newOnly);

    void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage source);

    void takeOwnership(AsyncCallback<CommunicationMessage> callback, CommunicationMessage source);

    @Transient
    interface CommunicationMessageInitializationData extends InitializationData {

        Building building();

        AptUnit unit();

        Tenant tenant();
    }
}
