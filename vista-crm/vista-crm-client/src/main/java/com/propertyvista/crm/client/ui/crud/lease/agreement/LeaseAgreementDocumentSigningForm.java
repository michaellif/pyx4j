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

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.client.deferred.DeferredProgressPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.ui.crud.lease.agreement.LeaseAgreementDocumentFolder.LeaseAgreementDocumentForm;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;

public class LeaseAgreementDocumentSigningForm extends CEntityForm<LeaseAgreementDocumentsDTO> {

    private static final I18n i18n = I18n.get(LeaseAgreementDocumentSigningForm.class);

    private LeaseAgreementDocumentFolder leaseAgreementDocumentFolder;

    private LeaseAgreementDocumentForm digitallySignedDocumentForm;

    private Label notSignedDigitallyLabel;

    private Button signDigitallyButton;

    private Label signDigitallyExplanation;

    private boolean canBeSignedDigitally;

    private VerticalPanel sigingProgressPanelHolder;

    public LeaseAgreementDocumentSigningForm() {
        super(LeaseAgreementDocumentsDTO.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        panel.setWidth("100%");
        int row = -1;
        panel.setH1(++row, 0, 2, i18n.tr("Signing Progress"));
        panel.setWidget(++row, 0, 2, inject(proto().signingProgress().stackholdersProgressBreakdown(), new LeaseAgreementSigningProgressFolder()));
        get(proto().signingProgress().stackholdersProgressBreakdown()).setViewable(true);

        panel.setH1(++row, 0, 2, i18n.tr("Digitally Signed Agreement Document"));
        panel.setWidget(++row, 0, 2,
                inject(proto().digitallySignedDocument(), digitallySignedDocumentForm = new LeaseAgreementDocumentFolder.LeaseAgreementDocumentForm(true)));
        panel.setWidget(++row, 0, 2, notSignedDigitallyLabel = new Label(i18n.tr("A signed document will appear here when every party signs digitally")));
        panel.setWidget(++row, 0, 2, signDigitallyButton = new Button(i18n.tr("Sign Digitally"), new Command() {
            @Override
            public void execute() {
                MessageDialog.confirm(i18n.tr("Sign Agreement Document"), i18n.tr("Are you sure?"), new Command() {
                    @Override
                    public void execute() {
                        onSignDigitally();
                    }
                });
            }
        }));
        signDigitallyButton.getElement().getStyle().setMarginTop(2, Unit.EM);
        panel.setWidget(++row, 0, 2,
                signDigitallyExplanation = new Label(i18n.tr("Once every lease participant signs, the document will be ready for signing by Landlord")));

        sigingProgressPanelHolder = new VerticalPanel();
        sigingProgressPanelHolder.setWidth("100%");
        panel.setWidget(++row, 0, 2, sigingProgressPanelHolder);

        panel.setH1(++row, 0, 2, i18n.tr("Ink Signed Agreement Documents"));
        panel.setWidget(++row, 0, 2, inject(proto().inkSignedDocuments(), this.leaseAgreementDocumentFolder = new LeaseAgreementDocumentFolder()));

        return panel;
    }

    public void setLeaseTermParticipantsOptions(List<LeaseTermParticipant<?>> participantsOptions) {
        this.leaseAgreementDocumentFolder.setParticipantOptions(participantsOptions);
    }

    public void setUploader(CrmUser uploader) {
        this.leaseAgreementDocumentFolder.setUploaderEmployee(uploader);
    }

    public void setCanBeSignedDigitally(boolean canBeSignedDigitally) {
        this.canBeSignedDigitally = canBeSignedDigitally;
    }

    public void onSignDigitally() {

    }

    public void monitorSigningProgress(String corellationId, DeferredProgressListener callback) {
        sigingProgressPanelHolder.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        sigingProgressPanelHolder.clear();
        sigingProgressPanelHolder.setVisible(true);

        Label messageLabel = new Label(i18n.tr("Generating Signed Agreement, Please Wait..."));
        messageLabel.setWidth("100%");
        sigingProgressPanelHolder.add(messageLabel);
        sigingProgressPanelHolder.setCellHorizontalAlignment(messageLabel, HasHorizontalAlignment.ALIGN_CENTER);

        DeferredProgressPanel progressPanel = new DeferredProgressPanel("30em", "5em", false, callback);
        progressPanel.startProgress(corellationId);
        progressPanel.setWidth("100%");
        sigingProgressPanelHolder.add(progressPanel);

        signDigitallyButton.setVisible(false);
        signDigitallyExplanation.setVisible(false);
        notSignedDigitallyLabel.setVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        sigingProgressPanelHolder.setVisible(false);

        boolean hasDigitallySignedDocument = !getValue().digitallySignedDocument().isNull();
        digitallySignedDocumentForm.setVisible(hasDigitallySignedDocument);
        notSignedDigitallyLabel.setVisible(!hasDigitallySignedDocument);
        signDigitallyButton.setVisible(!hasDigitallySignedDocument);
        signDigitallyExplanation.setVisible(!hasDigitallySignedDocument);

        signDigitallyButton.setEnabled(canBeSignedDigitally);
        signDigitallyExplanation.setVisible(!canBeSignedDigitally);

    }
}
