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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsSigningDTO;

public class LeaseAgreementDocumentSigningForm extends CEntityForm<LeaseAgreementDocumentsSigningDTO> {

    private static final I18n i18n = I18n.get(LeaseAgreementDocumentSigningForm.class);

    private LeaseAgreementDocumentFolder leaseAgreementDocumentFolder;

    public LeaseAgreementDocumentSigningForm() {
        super(LeaseAgreementDocumentsSigningDTO.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 2, i18n.tr("Signing Progress"));

        panel.setH1(++row, 0, 2, i18n.tr("Digitally Signed Agreement Document"));
        panel.setWidget(++row, 0, 2, inject(proto().digitallySignedDocument(), new LeaseAgreementDocumentFolder.LeaseAgreementDocumentForm(true)));

        panel.setH1(++row, 0, 2, i18n.tr("Ink Signed Agreement Documents"));
        panel.setWidget(++row, 0, 2, inject(proto().inkSignedDocuments(), this.leaseAgreementDocumentFolder = new LeaseAgreementDocumentFolder()));

        return panel;
    }

    public void setLeaseTermParticipantsOptions(LeaseTermParticipant<?> participantsOptions) {
        this.leaseAgreementDocumentFolder.setParticipantOptions(participantsOptions);
    }

}
