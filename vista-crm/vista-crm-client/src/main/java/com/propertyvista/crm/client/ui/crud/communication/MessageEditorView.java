/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.backoffice.ui.prime.form.IEditorView;

import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.dto.MessageDTO;

public interface MessageEditorView extends IEditorView<MessageDTO> {

    interface Presenter extends IEditorView.IEditorPresenter {

        void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO message);

        MessageCategory getCategory();

        CategoryType getCategoryType();
    }
}
