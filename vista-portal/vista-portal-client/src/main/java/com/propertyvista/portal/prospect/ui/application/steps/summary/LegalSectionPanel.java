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
import com.propertyvista.portal.prospect.ui.application.steps.LegalStep;
import com.propertyvista.portal.prospect.ui.application.steps.LegalTermsFolder;

public class LegalSectionPanel extends AbstractSectionPanel {

    public LegalSectionPanel(int index, SummaryForm form, LegalStep step) {
        super(index, OnlineApplicationWizardStepMeta.Legal.toString(), form, step);

        LegalTermsFolder legalTermsFolder = new LegalTermsFolder();
        legalTermsFolder.setViewable(true);
        addField(proto().legalTerms(), legalTermsFolder, false);

    }

}
