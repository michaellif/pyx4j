/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 29, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.agreement;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CListBox;
import com.pyx4j.forms.client.ui.CListBox.SelectionMode;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.crm.client.ui.crud.lease.agreement.LeaseAgreementDocumentFolder.LeaseAgreementSignedParticipantsViewer;
import com.propertyvista.crm.rpc.services.lease.LeaseTermAgreementDocumentUploadService;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class LeaseAgreementDocumentForm extends CForm<LeaseTermAgreementDocument> {

    private static final I18n i18n = I18n.get(LeaseAgreementDocumentForm.class);

    private final boolean viewOnly;

    private CListBox<LeaseTermParticipant<?>> signedParticipantsListBox;

    public LeaseAgreementDocumentForm(boolean viewOnly) {
        super(LeaseTermAgreementDocument.class);
        if (viewOnly) {
            setViewable(true);
            setEditable(false);
        }
        this.viewOnly = viewOnly;
    }

    @Override
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel
                .append(Location.Dual,
                        proto().file(),
                        new CFile(GWT.<UploadService<?, ?>> create(LeaseTermAgreementDocumentUploadService.class), new VistaFileURLBuilder(
                                LeaseTermAgreementDocument.class))).decorate().customLabel(i18n.tr("Agreement Document File")).componentWidth(350);

        if (viewOnly) {
            formPanel.append(Location.Dual, proto().signedParticipants(), new LeaseAgreementSignedParticipantsViewer()).decorate()
                    .customLabel(i18n.tr("Signed Participants")).componentWidth(350);
        } else {
            formPanel
                    .append(Location.Dual, proto().signedParticipants(),
                            signedParticipantsListBox = new CListBox<LeaseTermParticipant<?>>(SelectionMode.SINGLE_PANEL) {
                                @Override
                                public String getItemName(LeaseTermParticipant<?> pariticipant) {
                                    return LeaseAgreementDocumentFolder.formatParticipant(pariticipant);
                                }
                            }).decorate().customLabel(i18n.tr("Signed Participant")).componentWidth(350);
        }
        formPanel.append(Location.Dual, proto().signedEmployeeUploader().name(), new CLabel<String>()).decorate()
                .customLabel(i18n.tr("Signed Employee / Uploader")).componentWidth(350);
        return formPanel;
    }

    public void setParticipantOptions(List<LeaseTermParticipant<?>> participant) {
        signedParticipantsListBox.setOptions(participant);
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().file()).setMandatory(true);
        get(proto().signedParticipants()).addComponentValidator(new AbstractComponentValidator<List<LeaseTermParticipant<?>>>() {

            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().isEmpty()) {
                    return new FieldValidationError(getComponent(), i18n.tr("Please select signed lease participants"));
                }
                return null;
            }
        });
    }
}