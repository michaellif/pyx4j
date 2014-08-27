/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent.NoteStyle;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(LeaseStep.class);

    private FormPanel adjustmentPanel;

    private FormPanel depositPanel;

    private FormPanel featurePanel;

    public LeaseStep() {
        super(OnlineApplicationWizardStepMeta.Lease);
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());
        formPanel.h3(i18n.tr("Landlord Info"));
        formPanel.append(Location.Left, proto().landlordInfo().name(), new CLabel<String>()).decorate();
        formPanel.append(Location.Left, proto().landlordInfo().address(), new CLabel<String>()).decorate();

        formPanel.h3(i18n.tr("Unit"));
        formPanel.append(Location.Left, proto().unit().info().number(), new CLabel<String>()).decorate();
        formPanel.append(Location.Left, proto().unit().info().legalAddress(), new CEntityLabel<InternationalAddress>()).decorate();
        formPanel.append(Location.Left, proto().unit().floorplan(), new CEntityLabel<Floorplan>()).decorate();
        formPanel.append(Location.Left, proto().utilities(), new CLabel<String>()).decorate();

        formPanel.h3(i18n.tr("Lease Term"));
        formPanel.append(Location.Left, proto().leaseFrom(), new CDateLabel()).decorate();
        formPanel.append(Location.Left, proto().leaseTo(), new CDateLabel()).decorate();

        formPanel.h3(i18n.tr("Lease Options"));
        formPanel.append(Location.Left, proto().leaseChargesData().selectedService().agreedPrice(), new CMoneyLabel()).decorate()
                .customLabel(i18n.tr("Monthly Unit Rent"));
        formPanel.append(Location.Left, proto().leaseChargesData().selectedService().description(), new CLabel<String>()).decorate();

        adjustmentPanel = new FormPanel(getWizard());
        adjustmentPanel.h4(i18n.tr("Unit Adjustments"));
        adjustmentPanel.append(Location.Dual, proto().leaseChargesData().selectedService().adjustments(), new AdjustmentFolder());
        formPanel.append(Location.Left, adjustmentPanel);

        depositPanel = new FormPanel(getWizard());
        depositPanel.h4(i18n.tr("Unit Deposits"));
        depositPanel.append(Location.Left, proto().leaseChargesData().selectedService().deposits(), new DepositFolder());
        formPanel.append(Location.Left, depositPanel);

        featurePanel = new FormPanel(getWizard());
        featurePanel.h3(i18n.tr("Features"));
        featurePanel.append(Location.Left, proto().leaseChargesData().selectedFeatures(), new FeatureFolder());
        formPanel.append(Location.Left, featurePanel);

        get(proto().leaseChargesData().selectedFeatures()).setEditable(false);

        if (!SecurityController.check(PortalProspectBehavior.Applicant)) {
            formPanel.h3(i18n.tr("People"));
            formPanel.append(Location.Left, proto().tenants(), new TenantsReadonlyFolder());
        }

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        // TODO Auto-generated method stub
        super.onValueSet(populate);

        adjustmentPanel.setVisible(!getValue().leaseChargesData().selectedService().adjustments().isEmpty());
        depositPanel.setVisible(!getValue().leaseChargesData().selectedService().deposits().isEmpty());
        featurePanel.setVisible(!getValue().leaseChargesData().selectedFeatures().isEmpty());

        get(proto().utilities()).setVisible(!getValue().utilities().isNull());
        get(proto().leaseChargesData().selectedService().description()).setVisible(!getValue().leaseChargesData().selectedService().description().isNull());

        if (VistaFeatures.instance().yardiIntegration()) {
            if (getValue().leaseFrom().getValue().getDate() != 1) {
                get(proto().leaseTo()).setNote(i18n.tr("Additional Rent May be payable if the lease commences prior to the 1st of the given month"),
                        NoteStyle.Warn);
            }
        }
    }
}
