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
package com.propertyvista.portal.prospect.ui.application.steps.summary;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;

public class SummaryStep extends ApplicationWizardStep {

    private SummaryForm form;

    public SummaryStep() {
        super(OnlineApplicationWizardStepMeta.Summary);

    }

    @Override
    public Widget createStepContent() {
        form = new SummaryForm(getWizard());
        form.initContent();
        return form.asWidget();
    }

    @Override
    public void setStepSelected(final boolean selected) {
        super.setStepSelected(selected);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                try {
                    if (selected) {
                        form.setValue(getValue());
                    } else {
                        form.reset();
                    }
                } catch (Throwable e) {
                }
            }
        });

    }

}
