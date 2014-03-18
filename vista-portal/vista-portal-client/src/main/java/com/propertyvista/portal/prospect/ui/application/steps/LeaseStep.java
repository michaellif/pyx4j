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

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class LeaseStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(LeaseStep.class);

    private final BasicFlexFormPanel depositPanel = new BasicFlexFormPanel();

    private final BasicFlexFormPanel featurePanel = new BasicFlexFormPanel();

    public LeaseStep() {
        super(OnlineApplicationWizardStepMeta.Lease);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("Unit"));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unit().info().number(), new CLabel<String>())).build());
        panel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().unit().building().info().address(), new CEntityLabel<AddressStructured>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unit().floorplan(), new CEntityLabel<Floorplan>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().utilities(), new CLabel<String>())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Lease Term"));
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leaseFrom(), new CDateLabel())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leaseTo(), new CDateLabel())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Lease Options"));
        panel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().selectedService().agreedPrice(), new CMoneyLabel())).customLabel(i18n.tr("Unit Rent")).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().selectedService().description(), new CLabel<String>())).build());

        panel.setWidget(++row, 0, depositPanel);
        depositPanel.setH4(0, 0, 1, i18n.tr("Unit Deposits"));
        depositPanel.setWidget(1, 0, 1, inject(proto().selectedService().deposits(), new DepositFolder() {
            @Override
            public IFolderItemDecorator<Deposit> createItemDecorator() {
                BoxFolderItemDecorator<Deposit> decor = (BoxFolderItemDecorator<Deposit>) super.createItemDecorator();
                decor.setExpended(false);
                return decor;
            }
        }));

        panel.setWidget(++row, 0, featurePanel);
        featurePanel.setH3(0, 0, 1, i18n.tr("Features"));
        featurePanel.setWidget(++row, 0, inject(proto().selectedFeatures(), new FeatureFolder()));
        get(proto().selectedFeatures()).setEditable(false);

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            panel.setH3(++row, 0, 1, i18n.tr("People"));
            panel.setWidget(++row, 0, inject(proto().tenants(), new TenantsReadonlyFolder()));
        }

        return panel;
    }

    @Override
    public void onValueSet(boolean populate) {
        // TODO Auto-generated method stub
        super.onValueSet(populate);

        depositPanel.setVisible(!getValue().selectedService().deposits().isEmpty());
        featurePanel.setVisible(!getValue().selectedFeatures().isEmpty());

        get(proto().utilities()).setVisible(!getValue().utilities().isNull());
        get(proto().selectedService().description()).setVisible(!getValue().selectedService().description().isNull());
    }
}
