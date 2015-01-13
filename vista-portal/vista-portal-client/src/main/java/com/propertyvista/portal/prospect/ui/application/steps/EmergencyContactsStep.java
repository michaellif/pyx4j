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
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.EmergencyContact;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.shared.ui.util.folders.EmergencyContactFolder;

public class EmergencyContactsStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(EmergencyContactsStep.class);

    private final EmergencyContactFolder emergencyContactFolder = new EmergencyContactFolder();

    public EmergencyContactsStep() {
        super(OnlineApplicationWizardStepMeta.EmergencyContacts);
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());
        formPanel.append(Location.Left, proto().applicantData().emergencyContacts(), emergencyContactFolder);
        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        emergencyContactFolder.addComponentValidator(new AbstractComponentValidator<IList<EmergencyContact>>() {
            @Override
            public BasicValidationError isValid() {
                int contactsAmount = getValue().emergencyContactsNumberRequired().getValue(1);
                if (getValue().emergencyContactsIsMandatory().getValue(false) && getCComponent().getValue().size() < contactsAmount) {
                    return new BasicValidationError(getCComponent(), i18n.tr("At least {0} Emergency Contact(s) should be specified", contactsAmount));
                }
                return null;
            }
        });
    }
}
