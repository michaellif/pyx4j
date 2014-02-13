/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 22, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps.summary;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.steps.CoapplicantsReadonlyFolder;
import com.propertyvista.portal.prospect.ui.application.steps.DependentsReadonlyFolder;
import com.propertyvista.portal.prospect.ui.application.steps.LeaseStep;

public class LeaseSectionPanel extends AbstractSectionPanel {

    private static final I18n i18n = I18n.get(LeaseSectionPanel.class);

    public LeaseSectionPanel(int index, SummaryForm form, LeaseStep step) {
        super(index, OnlineApplicationWizardStepMeta.Lease.toString(), form, step);
        addCaption(i18n.tr("Unit"));
        addField(proto().unit().info().number());
        addField(proto().unit().building().info().address());
        addField(proto().utilities());

        addCaption(i18n.tr("Lease Term"));
        addField(proto().leaseFrom());
        addField(proto().leaseTo());

        addCaption(i18n.tr("Lease Options"));
        addField(proto().selectedService().agreedPrice());

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            addCaption(i18n.tr("People Living with You"));
            addField(proto().coapplicants(), new CoapplicantsReadonlyFolder(), false);
            addField(proto().dependents(), new DependentsReadonlyFolder(), false);
        }
    }

}
