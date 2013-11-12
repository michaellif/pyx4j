/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.prospect.ui.steps.PeopleStepView.PeopleStepPresenter;
import com.propertyvista.portal.rpc.portal.web.dto.application.PeopleStepDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizardStep;

public class PeopleStep extends CPortalEntityWizardStep<PeopleStepDTO> {

    private static final I18n i18n = I18n.get(PeopleStep.class);

    private PeopleStepPresenter presenter;

    public PeopleStep(PeopleStepView view) {
        super(PeopleStepDTO.class, view, i18n.tr("People"), i18n.tr("Next"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Select an Available Unit"));

        return mainPanel;
    }

}
