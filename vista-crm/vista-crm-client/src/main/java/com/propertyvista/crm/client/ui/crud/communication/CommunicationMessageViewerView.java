/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.prime.form.IViewer;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.dto.CommunicationMessageDTO;

public interface CommunicationMessageViewerView extends IViewer<CommunicationMessageDTO> {

    interface Presenter extends IViewer.Presenter {

        void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message);

        void takeOwnership(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message);
    }
}
