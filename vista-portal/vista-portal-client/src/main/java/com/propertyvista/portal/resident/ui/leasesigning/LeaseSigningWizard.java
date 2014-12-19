/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 */
package com.propertyvista.portal.resident.ui.leasesigning;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.folders.FeatureReadOnlyFolder;
import com.propertyvista.portal.shared.ui.util.folders.TenantsReadonlyFolder;

public class LeaseSigningWizard extends CPortalEntityWizard<LeaseAgreementDTO> {

    private final static I18n i18n = I18n.get(LeaseSigningWizard.class);

    private FormPanel featurePanel;

    public LeaseSigningWizard(LeaseSigningWizardView view) {
        super(LeaseAgreementDTO.class, view, i18n.tr("Lease Agreement"), i18n.tr("Submit"), ThemeColor.contrast2);

        addStep(createDetailsStep(), i18n.tr("Details"));
        addStep(createAgreementStep(), i18n.tr("Agreement"));
        addStep(createConfirmationStep(), i18n.tr("Confirmation"));
    }

    public IsWidget createDetailsStep() {
        FormPanel formPanel = new FormPanel(this);
//        formPanel.h1(i18n.tr("Details"));

        CEntityLabel<Building> buildingLabel = new CEntityLabel<Building>();
        buildingLabel.setFormatter(new IFormatter<Building, String>() {
            @Override
            public String format(Building value) {
                StringBuilder builder = new StringBuilder();
                builder.append(value.marketing().name().getValue());
                builder.append(" (").append(value.info().address().getStringView()).append(")");
                return builder.toString();
            }
        });

        formPanel.h3(i18n.tr("Landlord Info"));
        formPanel.append(Location.Left, proto().landlordInfo().name(), new CLabel<String>()).decorate();
        formPanel.append(Location.Left, proto().landlordInfo().address(), new CLabel<String>()).decorate();

        formPanel.h3(i18n.tr("Lease Info"));
        formPanel.append(Location.Left, proto().unit().building(), buildingLabel).decorate();
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

        return formPanel;
    }

    public IsWidget createAgreementStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().legalTerms(), new LegalTermsFolder());
        return formPanel;
    }

    public IsWidget createConfirmationStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().confirmationTerms(), new ConfirmationTermsFolder());
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        featurePanel.setVisible(!getValue().leaseTerm().version().leaseProducts().featureItems().isEmpty());
    }
}