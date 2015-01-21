/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 16, 2015
 * @author michaellif
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.communication.BroadcastTemplate;

public class BroadcastTemplateEditorViewImpl extends CrmEditorViewImplBase<BroadcastTemplate> implements BroadcastTemplateEditorView {

    private static final I18n i18n = I18n.get(BroadcastTemplateEditorViewImpl.class);

    public BroadcastTemplateEditorViewImpl() {
        setForm(new BroadcastTemplateForm(this));
    }

    @Override
    public void populate(BroadcastTemplate value) {
        super.populate(value);
        String caption = i18n.tr("New")
                + (((BroadcastTemplateEditorView.BroadcastTemplateEditorPresenter) getForm().getParentView().getPresenter()).getType() != null ? " "
                        + ((BroadcastTemplateEditorView.BroadcastTemplateEditorPresenter) getForm().getParentView().getPresenter()).getType() : "")
                + " Broadcast Template";
        setCaption(caption);
    }

}