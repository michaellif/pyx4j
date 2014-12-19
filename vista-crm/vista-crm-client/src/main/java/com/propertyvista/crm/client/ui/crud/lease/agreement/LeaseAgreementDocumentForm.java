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
 */
package com.propertyvista.crm.client.ui.crud.lease.agreement;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CListBox;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.crm.rpc.services.lease.LeaseTermAgreementDocumentUploadService;
import com.propertyvista.domain.tenant.lease.LeaseTermAgreementDocument;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public class LeaseAgreementDocumentForm extends CForm<LeaseTermAgreementDocument> {

    private static final I18n i18n = I18n.get(LeaseAgreementDocumentForm.class);

    private final CListBox<LeaseTermParticipant<?>> signedParticipantsListBox = new CListBox<LeaseTermParticipant<?>>() {
        @Override
        public String getItemName(LeaseTermParticipant<?> pariticipant) {
            return LeaseAgreementSignedParticipantsViewer.formatParticipant(pariticipant);
        }
    };

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
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel
                .append(Location.Dual,
                        proto().file(),
                        new CFile(GWT.<UploadService<?, ?>> create(LeaseTermAgreementDocumentUploadService.class), new VistaFileURLBuilder(
                                LeaseTermAgreementDocument.class))).decorate().customLabel(i18n.tr("Agreement Document File")).labelWidth(200)
                .componentWidth(220);

        if (viewOnly) {
            formPanel.append(Location.Dual, proto().signedParticipants(), new LeaseAgreementSignedParticipantsViewer()).decorate()
                    .customLabel(i18n.tr("Signed Participants")).labelWidth(200).componentWidth(250);
        } else {
            formPanel.append(Location.Dual, proto().signedParticipants(), signedParticipantsListBox).decorate().customLabel(i18n.tr("Signed Participant"))
                    .labelWidth(200).componentWidth(250);
        }
        formPanel.append(Location.Dual, proto().signedEmployeeUploader().name(), new CLabel<String>()).decorate()
                .customLabel(i18n.tr("Signed Employee / Uploader")).labelWidth(200).componentWidth(250);
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
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && getCComponent().getValue().isEmpty()) {
                    return new BasicValidationError(getCComponent(), i18n.tr("Please select signed lease participants"));
                }
                return null;
            }
        });
    }

    private static class LeaseAgreementSignedParticipantsViewer extends CViewer<IList<LeaseTermParticipant<?>>> {

        public LeaseAgreementSignedParticipantsViewer() {
            setFormatter(new IFormatter<IList<LeaseTermParticipant<?>>, IsWidget>() {
                @Override
                public IsWidget format(IList<LeaseTermParticipant<?>> value) {
                    FlowPanel panel = new FlowPanel();
                    if (value != null) {
                        for (LeaseTermParticipant<?> participant : value) {
                            String signerStringView = formatParticipant(participant);
                            panel.add(new Label(signerStringView));
                        }
                    }
                    return panel;
                }
            });
        }

        public static String formatParticipant(LeaseTermParticipant<?> participant) {
            return SimpleMessageFormat.format("{0} ({1})", participant.leaseParticipant().customer().person().name().getStringView(), participant.role()
                    .getValue().toString());
        }
    }
}