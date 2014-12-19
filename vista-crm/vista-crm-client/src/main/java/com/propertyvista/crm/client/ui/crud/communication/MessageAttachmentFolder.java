/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2014
 * @author smolka
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.MessageAttachmentUploadService;
import com.propertyvista.domain.communication.MessageAttachment;

public class MessageAttachmentFolder extends VistaBoxFolder<MessageAttachment> {
    private final static I18n i18n = I18n.get(MessageAttachmentFolder.class);

    public MessageAttachmentFolder() {
        super(MessageAttachment.class, i18n.tr("Attachment"));
        setOrderable(false);
        setNoDataLabel(null);
    }

    @Override
    protected CForm<MessageAttachment> createItemForm(IObject<?> member) {
        return new MessageAttachmentViewer();
    }

    private class MessageAttachmentViewer extends CForm<MessageAttachment> {

        public MessageAttachmentViewer() {
            super(MessageAttachment.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(
                    Location.Left,
                    proto().file(),
                    new CFile(GWT.<MessageAttachmentUploadService> create(MessageAttachmentUploadService.class), new VistaFileURLBuilder(
                            MessageAttachment.class))).decorate();

            formPanel.append(Location.Left, proto().description()).decorate();

            return formPanel;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().file()).setMandatory(true);
        }
    }

}
