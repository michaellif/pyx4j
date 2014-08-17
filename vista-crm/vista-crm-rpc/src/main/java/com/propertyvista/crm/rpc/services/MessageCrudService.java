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
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;

import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.dto.MessageDTO;

public interface MessageCrudService extends AbstractCrudService<MessageDTO> {

    void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO source, ThreadStatus threadStatus);

    void assignOwnership(AsyncCallback<MessageDTO> callback, MessageDTO source, IEntity employee);

    void listForHeader(AsyncCallback<EntitySearchResult<MessageDTO>> callback);

    @Transient
    public static interface MessageInitializationData extends InitializationData {

        MessageDTO forwardedMessage();

        MessageCategory messageCategory();

        IPrimitive<MessageGroupCategory> categoryType();
    }
}
