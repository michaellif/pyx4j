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
 */
package com.propertyvista.portal.prospect.ui.application.steps.summary;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.steps.AboutYouStep;

public class AboutYouSectionPanel extends AbstractSectionPanel {

    private static final I18n i18n = I18n.get(AboutYouSectionPanel.class);

    public AboutYouSectionPanel(int index, SummaryForm form, AboutYouStep step) {
        super(index, OnlineApplicationWizardStepMeta.AboutYou.toString(), form, step);

        addCaption(i18n.tr("Personal Information"));

        addField(proto().applicantData().person().name());
        addField(proto().applicantData().person().sex());
        addField(proto().applicantData().person().birthDate());

        addCaption(i18n.tr("Contact Information"));

        addField(proto().applicantData().person().homePhone());
        addField(proto().applicantData().person().mobilePhone());
        addField(proto().applicantData().person().workPhone());
        addField(proto().applicantData().person().email());

    }

}
