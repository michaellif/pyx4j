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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.client.deferred.DeferredProgressPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.LeaseAgreementDocumentsDTO;

public class LeaseAgreementDocumentSigningForm extends CForm<LeaseAgreementDocumentsDTO> {

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
    protected IsWidget createContent() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.h1(i18n.tr("Signing Progress"));

        formPanel.append(Location.Full, proto().signingProgress().stackholdersProgressBreakdown(), new LeaseAgreementSigningProgressFolder());
        get(proto().signingProgress().stackholdersProgressBreakdown()).setViewable(true);

        formPanel.h1(i18n.tr("Digitally Signed Agreement Document"));

        formPanel.append(Location.Full, proto().digitallySignedDocument(), digitallySignedDocumentForm = new LeaseAgreementDocumentForm(true));

        formPanel.append(Location.Full, notSignedDigitallyLabel = new Label(i18n.tr("A signed document will appear here when every party signs digitally")));

        formPanel.append(Location.Full, signDigitallyButton = new Button(i18n.tr("Sign Digitally"), new Command() {
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

        formPanel.append(Location.Full,
                signDigitallyExplanation = new Label(i18n.tr("Once every lease participant signs, the document will be ready for signing by Landlord")));

        sigingProgressPanelHolder = new VerticalPanel();
        sigingProgressPanelHolder.setWidth("100%");
        formPanel.append(Location.Full, sigingProgressPanelHolder);

        formPanel.h1(i18n.tr("Ink Signed Agreement Documents"));

        formPanel.append(Location.Full, proto().inkSignedDocuments(), this.leaseAgreementDocumentFolder = new LeaseAgreementDocumentFolder() {
            @Override
            public void onDocumentsChanged() {
                LeaseAgreementDocumentSigningForm.this.onDocumentsChanged();
            }
        });

        return formPanel;
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

    public void onDocumentsChanged() {

    }

    public void monitorSigningProgress(String corellationId, DeferredProgressListener callback) {
        sigingProgressPanelHolder.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        sigingProgressPanelHolder.clear();
        sigingProgressPanelHolder.setVisible(true);

        DeferredProgressPanel progressPanel = new DeferredProgressPanel(i18n.tr("Generating Signed Agreement, Please Wait..."), false, callback);
        progressPanel.setSize("100%", "5em");
        progressPanel.startProgress(corellationId);
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
