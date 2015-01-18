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
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.dto.communication.CommunicationThreadDTO;

public class CommunicationEditorViewImpl extends CrmEditorViewImplBase<CommunicationThreadDTO> implements CommunicationEditorView {
    private static final I18n i18n = I18n.get(CommunicationEditorViewImpl.class);

    public CommunicationEditorViewImpl() {
        setForm(new CommunicationEditForm(this));
        setBtnSaveCaption(i18n.tr("Send"));
        setApplyButtonVisible(false);

    }

    @Override
    public void populate(CommunicationThreadDTO value) {
        super.populate(value);
        String caption = i18n.tr("New") + " " + ((CommunicationEditorView.Presenter) getForm().getParentView().getPresenter()).getEntityName();
        setCaption(caption);
    }
}
