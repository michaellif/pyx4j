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

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.steps.EmergencyContactsStep;
import com.propertyvista.portal.shared.ui.util.editors.EmergencyContactFolder;

public class ContactsSectionPanel extends AbstractSectionPanel {

    public ContactsSectionPanel(int index, SummaryForm form, EmergencyContactsStep step) {
        super(index, OnlineApplicationWizardStepMeta.EmergencyContacts.toString(), form, step);

        EmergencyContactFolder emergencyContactFolder = new EmergencyContactFolder();
        emergencyContactFolder.setViewable(true);
        addField(proto().applicant().emergencyContacts(), emergencyContactFolder, false);

    }

}
