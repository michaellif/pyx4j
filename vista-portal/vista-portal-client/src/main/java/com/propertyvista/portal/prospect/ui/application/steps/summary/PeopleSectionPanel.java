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

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.steps.CoapplicantsReadonlyFolder;
import com.propertyvista.portal.prospect.ui.application.steps.DependentsReadonlyFolder;
import com.propertyvista.portal.prospect.ui.application.steps.PeopleStep;

public class PeopleSectionPanel extends AbstractSectionPanel {

    private static final I18n i18n = I18n.get(PeopleSectionPanel.class);

    public PeopleSectionPanel(int index, SummaryForm form, PeopleStep step) {
        super(index, OnlineApplicationWizardStepMeta.People.toString(), form, step);
        addCaption(i18n.tr("People Living with You"));
        addField(proto().coapplicants(), new CoapplicantsReadonlyFolder(), false);
        addField(proto().dependents(), new DependentsReadonlyFolder(), false);
    }
}
