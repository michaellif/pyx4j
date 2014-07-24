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
package com.propertyvista.portal.rpc.portal.resident.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.EntitySearchResult;

import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;

public interface MessagePortalCrudService extends AbstractCrudService<MessageDTO> {
    void saveChildMessage(AsyncCallback<MessageDTO> callback, MessageDTO source);

    void listForHeader(AsyncCallback<EntitySearchResult<MessageDTO>> callback);

    @Transient
    public static interface MessageInitializationData extends InitializationData {

        IPrimitive<String> initalizedText();
    }
}