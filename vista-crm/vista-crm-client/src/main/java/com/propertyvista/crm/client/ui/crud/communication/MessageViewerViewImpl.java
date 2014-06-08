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
import com.propertyvista.dto.MessageDTO;

public class MessageViewerViewImpl extends CrmViewerViewImplBase<MessageDTO> implements MessageViewerView {

    private static final I18n i18n = I18n.get(MessageViewerViewImpl.class);

    private final MenuItem takeOwnershipAction;

    public MessageViewerViewImpl() {
        super(true);
        setForm(new MessageForm(this));

        final MessageForm form = (MessageForm) getForm();

        takeOwnershipAction = new MenuItem(i18n.tr("Assign Ownership"), new Command() {
            @Override
            public void execute() {
                form.takeOwnership();
            }
        });

        addAction(takeOwnershipAction);

        MenuItem btnPrint = new MenuItem(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(MessageViewerViewImpl.this.getForm().getPrintableElement());
            }
        });
        addAction(btnPrint);
    }

    @Override
    public void populate(MessageDTO value) {
        super.populate(value);
        setActionVisible(takeOwnershipAction, value.owner().isNull() || value.owner().isEmpty());
    }

}
