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

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.PortalFormPanel;

public class LeaseStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(LeaseStep.class);

    private PortalFormPanel depositPanel;

    private PortalFormPanel featurePanel;

    public LeaseStep() {
        super(OnlineApplicationWizardStepMeta.Lease);
    }

    @Override
    public IsWidget createStepContent() {
        PortalFormPanel formPanel = new PortalFormPanel(getWizard());
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
                .customLabel(i18n.tr("Unit Rent"));
        formPanel.append(Location.Left, proto().leaseChargesData().selectedService().description(), new CLabel<String>()).decorate();

        depositPanel = new PortalFormPanel(getWizard());
        formPanel.append(Location.Left, depositPanel);

        depositPanel.h4(i18n.tr("Unit Deposits"));
        depositPanel.append(Location.Left, proto().leaseChargesData().selectedService().deposits(), new DepositFolder() {
            @Override
            public BoxFolderItemDecorator<Deposit> createItemDecorator() {
                BoxFolderItemDecorator<Deposit> decor = super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        });

        featurePanel = new PortalFormPanel(getWizard());
        formPanel.append(Location.Left, featurePanel);
        featurePanel.h3(i18n.tr("Features"));
        featurePanel.append(Location.Left, proto().leaseChargesData().selectedFeatures(), new FeatureFolder());
        get(proto().leaseChargesData().selectedFeatures()).setEditable(false);

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            formPanel.h3(i18n.tr("People"));
            formPanel.append(Location.Left, proto().tenants(), new TenantsReadonlyFolder());
        }

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        // TODO Auto-generated method stub
        super.onValueSet(populate);

        depositPanel.setVisible(!getValue().leaseChargesData().selectedService().deposits().isEmpty());
        featurePanel.setVisible(!getValue().leaseChargesData().selectedFeatures().isEmpty());

        get(proto().utilities()).setVisible(!getValue().utilities().isNull());
        get(proto().leaseChargesData().selectedService().description()).setVisible(!getValue().leaseChargesData().selectedService().description().isNull());
    }
}
