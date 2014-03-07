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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.dto.CommunicationMessageDTO;

public class CommunicationMessageViewerViewImpl extends CrmViewerViewImplBase<CommunicationMessageDTO> implements CommunicationMessageViewerView {

    private static final I18n i18n = I18n.get(CommunicationMessageViewerViewImpl.class);

    private final MenuItem takeOwnershipAction;

    public CommunicationMessageViewerViewImpl() {
        super(true);
        setForm(new CommunicationMessageForm(this));

        final CommunicationMessageForm form = (CommunicationMessageForm) getForm();
        MenuItem replayAction = new MenuItem(i18n.tr("Reply"), new Command() {
            @Override
            public void execute() {
                form.reply();
            }
        });
        addAction(replayAction);

        takeOwnershipAction = new MenuItem(i18n.tr("Take Ownership"), new Command() {
            @Override
            public void execute() {
                form.takeOwnership();
            }
        });

        addAction(takeOwnershipAction);

        MenuItem btnPrint = new MenuItem(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(CommunicationMessageViewerViewImpl.this.getForm().getPrintableElement());
            }
        });
        addAction(btnPrint);
    }

    @Override
    public void populate(CommunicationMessageDTO value) {
        super.populate(value);
        setActionVisible(takeOwnershipAction, value.thread().responsible().isNull() || value.thread().responsible().isEmpty());
    }

}
