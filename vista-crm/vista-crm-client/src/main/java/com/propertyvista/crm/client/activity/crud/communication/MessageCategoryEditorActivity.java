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
package com.propertyvista.crm.client.activity.crud.communication;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.crud.communication.MessageCategoryEditorView;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.domain.communication.MessageCategory;

public class MessageCategoryEditorActivity extends CrmEditorActivity<MessageCategory> implements MessageCategoryEditorView.Presenter {
    public MessageCategoryEditorActivity(CrudAppPlace place) {

        super(place, CrmSite.getViewFactory().getView(MessageCategoryEditorView.class), (AbstractCrudService<MessageCategory>) GWT
                .create(MessageCategoryCrudService.class), MessageCategory.class);
    }

    @Override
    protected void onApplySuccess(Key result) {
        super.onApplySuccess(result);
        AppSite.instance();
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent(MessageCategory.class));
    }

    @Override
    protected void onSaveSuccess(Key result) {
        super.onSaveSuccess(result);
        AppSite.instance();
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent(MessageCategory.class));
    }
}
