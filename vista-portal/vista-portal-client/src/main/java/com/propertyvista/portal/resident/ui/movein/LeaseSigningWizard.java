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
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class LeaseSigningWizard extends CPortalEntityWizard<LeaseAgreementDTO> {

    private final static I18n i18n = I18n.get(LeaseSigningWizard.class);

    private final BasicFlexFormPanel featurePanel = new BasicFlexFormPanel();

    public LeaseSigningWizard(LeaseSigningWizardView view) {
        super(LeaseAgreementDTO.class, view, i18n.tr("Move-In Wizard"), i18n.tr("Submit"), ThemeColor.contrast2);

        addStep(createDetailsStep(), i18n.tr("Lease Details"));
        addStep(createAgreementStep(), i18n.tr("Lease Agreement"));
        addStep(createConfirmationStep(), i18n.tr("Confirmation"));
    }

    public BasicFlexFormPanel createDetailsStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

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

        panel.setH3(++row, 0, 1, i18n.tr("Landlord Info"));
        panel.setWidget(++row, 0, inject(proto().landlordInfo().name(), new CLabel<String>(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().landlordInfo().address(), new CLabel<String>(), new FieldDecoratorBuilder().build()));

        panel.setH3(++row, 0, 1, i18n.tr("Lease Info"));
        panel.setWidget(++row, 0, inject(proto().unit().building(), buildingLabel, new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().unit(), new CEntityLabel<AptUnit>(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().unit().floorplan(), new CEntityLabel<Floorplan>(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().utilities(), new CLabel<String>(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0,
                inject(proto().leaseTerm().termFrom(), new CDateLabel(), new FieldDecoratorBuilder().customLabel(i18n.tr("Lease From")).build()));
        panel.setWidget(++row, 0, inject(proto().leaseTerm().termTo(), new CDateLabel(), new FieldDecoratorBuilder().customLabel(i18n.tr("Lease To")).build()));

        panel.setH3(++row, 0, 1, i18n.tr("Lease Options"));
        panel.setWidget(
                ++row,
                0,
                inject(proto().leaseTerm().version().leaseProducts().serviceItem().agreedPrice(), new CMoneyLabel(),
                        new FieldDecoratorBuilder().customLabel(i18n.tr("Base Rent")).build()));

        panel.setWidget(++row, 0, featurePanel);
        featurePanel.setH3(0, 0, 1, i18n.tr("Features"));
        featurePanel.setWidget(++row, 0, inject(proto().leaseTerm().version().leaseProducts().featureItems(), new FeaturesFolder()));

        panel.setH3(++row, 0, 1, i18n.tr("Tenants"));
        panel.setWidget(++row, 0, inject(proto().leaseTerm().version().tenants(), new TenantsFolder()));

        return panel;
    }

    public BasicFlexFormPanel createAgreementStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, inject(proto().legalTerms(), new LegalTermsFolder()));

        return panel;
    }

    public BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setWidget(++row, 0, inject(proto().confirmationTerms(), new ConfirmationTermsFolder()));

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        featurePanel.setVisible(!getValue().leaseTerm().version().leaseProducts().featureItems().isEmpty());
    }
}