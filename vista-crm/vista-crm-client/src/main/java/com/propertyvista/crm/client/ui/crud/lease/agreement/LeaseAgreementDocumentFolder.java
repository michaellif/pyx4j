/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.agreement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.rpc.services.lease.LeaseTermAgreementDocumentUploadService;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class LeaseAgreementDocumentFolder extends VistaBoxFolder<LeaseTermAgreementDocument> {

    private static final I18n i18n = I18n.get(LeaseAgreementDocumentFolder.class);;

    public LeaseAgreementDocumentFolder() {
        super(LeaseTermAgreementDocument.class);
        setEditable(true);
        setAddable(true);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LeaseTermAgreementDocument) {
            return new LeaseAgreementDocumentForm(true);
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new LeaseAgreementDocumentUploadDialog() {
            @Override
            public void accept(LeaseTermAgreementDocument document) {
                LeaseAgreementDocumentFolder.this.addItem(document);
            }
        }.show();
    }

    public static class LeaseAgreementDocumentForm extends CEntityForm<LeaseTermAgreementDocument> {

        private final boolean viewOnly;

        public LeaseAgreementDocumentForm(boolean viewOnly) {
            super(LeaseTermAgreementDocument.class);
            if (viewOnly) {
                setViewable(true);
                setEditable(false);
            }
            this.viewOnly = viewOnly;
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
            int row = -1;
            panel.setH3(++row, 0, 2, i18n.tr("Agreement Document File"));
            panel.setWidget(
                    ++row,
                    0,
                    inject(proto().file(), new CFile(GWT.<UploadService<?, ?>> create(LeaseTermAgreementDocumentUploadService.class), new VistaFileURLBuilder(
                            LeaseTermAgreementDocument.class))));
            panel.setH3(++row, 0, 2, i18n.tr("Signed Participants"));
            if (viewOnly) {
                panel.setWidget(++row, 0, 2, inject(proto().signedParticipants(), new LeaseAgreementSignedParticipantsViewer()));
            }
            return panel;
        }
    }

    public static class LeaseAgreementSignedParticipantsViewer extends CViewer<IList<LeaseTermParticipant<?>>> {

        @Override
        public IsWidget createContent(IList<LeaseTermParticipant<?>> value) {
            FlowPanel panel = new FlowPanel();
            for (LeaseTermParticipant<?> participant : value) {
                String signerStringView = SimpleMessageFormat.format("{0} ({1})", participant.leaseParticipant().customer().person().name().getStringView(),
                        participant.role().getValue().toString());
                panel.add(new Label(signerStringView));
            }
            return panel;
        }
    }

    public abstract static class LeaseAgreementDocumentUploadDialog extends OkCancelDialog {

        private final LeaseAgreementDocumentForm form;

        public LeaseAgreementDocumentUploadDialog() {
            super(i18n.tr("Upload Agreement Document"));
            form = new LeaseAgreementDocumentForm(false);
            form.initContent();
            form.populateNew();
            setBody(form);
        }

        @Override
        public boolean onClickOk() {
            if (form.isValid()) {
                accept(form.getValue());
                return true;
            } else {
                form.setUnconditionalValidationErrorRendering(true);
                return false;
            }

        }

        public abstract void accept(LeaseTermAgreementDocument document);

    }
}
