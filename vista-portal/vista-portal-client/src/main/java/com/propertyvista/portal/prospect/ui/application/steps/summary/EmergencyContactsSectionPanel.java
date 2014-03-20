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

import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.steps.EmergencyContactsStep;
import com.propertyvista.portal.shared.ui.util.editors.EmergencyContactFolder;

public class EmergencyContactsSectionPanel extends AbstractSectionPanel {

    private final EmergencyContactFolder emergencyContactFolder = new EmergencyContactFolder(false) {
        @Override
        public IFolderItemDecorator<EmergencyContact> createItemDecorator() {
            BoxFolderItemDecorator<EmergencyContact> decor = (BoxFolderItemDecorator<EmergencyContact>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }
    };

    public EmergencyContactsSectionPanel(int index, SummaryForm form, EmergencyContactsStep step) {
        super(index, OnlineApplicationWizardStepMeta.EmergencyContacts.toString(), form, step);

        addField(proto().applicant().emergencyContacts(), emergencyContactFolder, false);
    }
}
