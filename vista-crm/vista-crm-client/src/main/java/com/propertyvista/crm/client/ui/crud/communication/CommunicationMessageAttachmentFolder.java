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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.AccessoryEntityForm;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.CommunicationMessageAttachmentUploadService;
import com.propertyvista.domain.communication.CommunicationMessageAttachment;

public class CommunicationMessageAttachmentFolder extends VistaBoxFolder<CommunicationMessageAttachment> {
    private final static I18n i18n = I18n.get(CommunicationMessageAttachmentFolder.class);

    public CommunicationMessageAttachmentFolder() {
        super(CommunicationMessageAttachment.class, i18n.tr("Attachment"));
        setOrderable(false);
    }

    @Override
    protected CForm<CommunicationMessageAttachment> createItemForm(IObject<?> member) {
        return new CommunicationMessageAttachmentViewer();
    }

    private class CommunicationMessageAttachmentViewer extends AccessoryEntityForm<CommunicationMessageAttachment> {

        public CommunicationMessageAttachmentViewer() {
            super(CommunicationMessageAttachment.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(
                    ++row,
                    0,
                    injectAndDecorate(proto().file(),
                            new CFile(GWT.<CommunicationMessageAttachmentUploadService> create(CommunicationMessageAttachmentUploadService.class),
                                    new VistaFileURLBuilder(CommunicationMessageAttachment.class)), 30));

            content.setWidget(++row, 0, injectAndDecorate(proto().description(), 20));

            return content;
        }

        @Override
        public void addValidations() {
            super.addValidations();
            get(proto().file()).setMandatory(true);
        }
    }

}
