/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 */
package com.propertyvista.portal.resident.ui.leasesigning;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.resident.ui.leasesigning.LeaseSigningView.LeaseSigningPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.shared.ui.AbstractFormView;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.CBuildingLabel;
import com.propertyvista.portal.shared.ui.util.folders.FeatureReadOnlyFolder;
import com.propertyvista.portal.shared.ui.util.folders.TenantsReadonlyFolder;

public class LeaseSigningForm extends CPortalEntityForm<LeaseAgreementDTO> {

    private static final I18n i18n = I18n.get(LeaseSigningForm.class);

    private FormPanel featurePanel;

    private final static Button submitBtn = new Button(i18n.tr("Submit"));

    public LeaseSigningForm(AbstractFormView<LeaseAgreementDTO> view) {
        super(LeaseAgreementDTO.class, view, i18n.tr("Lease Agreement"), submitBtn, ThemeColor.contrast2);

        submitBtn.setCommand(new Command() {
            @Override
            public void execute() {
                setVisitedRecursive();
                if (!isValid()) {
                    MessageDialog.error(i18n.tr("Error"), i18n.tr("There has been an error. Please check your data and try again."));
                } else {
                    ((LeaseSigningPresenter) getView().getPresenter()).submit();
                }
            }
        });
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h3(i18n.tr("Landlord Info"));
        formPanel.append(Location.Left, proto().landlordInfo().name(), new CLabel<String>()).decorate();
        formPanel.append(Location.Left, proto().landlordInfo().address(), new CLabel<String>()).decorate();

        formPanel.h3(i18n.tr("Lease Info"));
        formPanel.append(Location.Left, proto().unit().building(), new CBuildingLabel()).decorate();
        formPanel.append(Location.Left, proto().unit(), new CEntityLabel<AptUnit>()).decorate();
        formPanel.append(Location.Left, proto().unit().floorplan(), new CEntityLabel<Floorplan>()).decorate();
        formPanel.append(Location.Left, proto().utilities(), new CLabel<String>()).decorate();

        formPanel.append(Location.Left, proto().leaseTerm().termFrom(), new CDateLabel()).decorate().customLabel(i18n.tr("Lease From"));
        formPanel.append(Location.Left, proto().leaseTerm().termTo(), new CDateLabel()).decorate().customLabel(i18n.tr("Lease To"));
        formPanel.append(Location.Left, proto().leaseTerm().version().leaseProducts().serviceItem().agreedPrice(), new CMoneyLabel()).decorate()
                .customLabel(i18n.tr("Base Rent"));

        formPanel.append(Location.Left, featurePanel = new FormPanel(this));

        featurePanel.h3(i18n.tr("Lease Options"));
        featurePanel.append(Location.Left, proto().leaseTerm().version().leaseProducts().featureItems(), new FeatureReadOnlyFolder());

        formPanel.h3(i18n.tr("Tenants"));
        formPanel.append(Location.Left, proto().leaseTerm().version().tenants(), new TenantsReadonlyFolder());

        formPanel.h3(i18n.tr("Terms"));
        formPanel.append(Location.Left, proto().legalTerms(), new LegalTermsFolder());

        formPanel.h3(i18n.tr("Signature"));
        formPanel.append(Location.Left, proto().confirmationTerms(), new ConfirmationTermsFolder());

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        featurePanel.setVisible(!getValue().leaseTerm().version().leaseProducts().featureItems().isEmpty());
    }
}
