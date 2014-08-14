/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.dto.MessageDTO;

public class MessageEditorViewImpl extends CrmEditorViewImplBase<MessageDTO> implements MessageEditorView {
    private static final I18n i18n = I18n.get(MessageEditorViewImpl.class);

    public MessageEditorViewImpl() {
        setForm(new MessageEditForm(this));
        setBtnSaveCaption(i18n.tr("Send"));
        setBtnApplyEnabled(false);

    }

    @Override
    public void populate(MessageDTO value) {
        super.populate(value);
        MessageGroupCategory ct = ((MessageEditorView.Presenter) getForm().getParentView().getPresenter()).getCategoryType();
        String caption = i18n.tr("New") + " " + (ct == null ? MessageGroupCategory.Message.toString() : ct.toString());
        setCaption(caption);
    }
}
