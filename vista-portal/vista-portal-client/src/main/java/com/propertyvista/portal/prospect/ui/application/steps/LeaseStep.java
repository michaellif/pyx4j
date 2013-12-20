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
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.portal.prospect.ui.application.ApplicationOptionsFolder;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class LeaseStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(LeaseStep.class);

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Lease Information"));
        int row = -1;
        panel.setH1(++row, 0, 1, panel.getTitle());

        panel.setH3(++row, 0, 1, i18n.tr("Unit"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unit().info().number(), new CLabel<String>())).build());
        panel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().unit().building().info().address(), new CEntityLabel<AddressStructured>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().unit().floorplan(), new CEntityLabel<Floorplan>())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().utilities(), new CLabel<String>())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Lease Term"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leaseFrom(), new CDateLabel())).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leaseTo(), new CDateLabel())).build());

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().leasePrice(), new CMoneyLabel())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Lease Options"));

        panel.setWidget(++row, 0, inject(proto().options(), new ApplicationOptionsFolder(getView())));

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            panel.setH3(++row, 0, 1, i18n.tr("People"));

            panel.setWidget(++row, 0, inject(proto().coapplicants(), new CoapplicantsFolder(getView())));
        }

        return panel;
    }
}
