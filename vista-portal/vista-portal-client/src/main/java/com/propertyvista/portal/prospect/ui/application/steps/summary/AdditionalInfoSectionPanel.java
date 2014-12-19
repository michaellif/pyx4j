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

import com.pyx4j.forms.client.ui.CEntityLabel;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.steps.AdditionalInfoStep;

public class AdditionalInfoSectionPanel extends AbstractSectionPanel {

    public AdditionalInfoSectionPanel(int index, SummaryForm form, AdditionalInfoStep step) {
        super(index, OnlineApplicationWizardStepMeta.AdditionalInfo.toString(), form, step);

        addField(proto().applicantData().currentAddress(), new CEntityLabel<PriorAddress>());

        addField(proto().applicantData().previousAddress(), new CEntityLabel<PriorAddress>());

    }

}
