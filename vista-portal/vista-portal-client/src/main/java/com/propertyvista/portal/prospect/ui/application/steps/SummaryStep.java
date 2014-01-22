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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;

public class SummaryStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(SummaryStep.class);

    private SummaryPanel panel;

    public SummaryStep() {
        super(OnlineApplicationWizardStepMeta.Summary);
    }

    @Override
    public Widget createStepContent() {
        panel = new SummaryPanel();
        return panel.asWidget();
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);
        panel.setValue(getValue());
    }

    class SummaryPanel extends CEntityForm<OnlineApplicationDTO> {

        public SummaryPanel() {
            super(OnlineApplicationDTO.class);
        }

        @Override
        public IsWidget createContent() {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
