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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.editors.EmergencyContactFolder;

public class EmergencyContactsStep extends ApplicationWizardStep {

    public EmergencyContactsStep() {
        super(OnlineApplicationWizardStepMeta.EmergencyContacts);
    }

    @Override
    public IsWidget createStepContent() {
        PortalFormPanel formPanel = new PortalFormPanel(getWizard());
        formPanel.append(Location.Left, proto().applicant().emergencyContacts(), new EmergencyContactFolder());
        return formPanel;
    }
}
