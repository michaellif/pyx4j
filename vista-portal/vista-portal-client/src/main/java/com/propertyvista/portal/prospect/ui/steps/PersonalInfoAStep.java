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

import com.propertyvista.portal.prospect.ui.steps.PersonalInfoAStepView.PersonalInfoAStepPresenter;
import com.propertyvista.portal.rpc.portal.prospect.dto.PersonalInfoAStepDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizardStep;

public class PersonalInfoAStep extends CPortalEntityWizardStep<PersonalInfoAStepDTO> {

    private static final I18n i18n = I18n.get(PersonalInfoAStep.class);

    private PersonalInfoAStepPresenter presenter;

    public PersonalInfoAStep(PersonalInfoAStepView view) {
        super(PersonalInfoAStepDTO.class, view, i18n.tr("About You"), i18n.tr("Next"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        int row = -1;

        mainPanel.setH1(++row, 0, 1, i18n.tr("Select an Available Unit"));

        return mainPanel;
    }

}
