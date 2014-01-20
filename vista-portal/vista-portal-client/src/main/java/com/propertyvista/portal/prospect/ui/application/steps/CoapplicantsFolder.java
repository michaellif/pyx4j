/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class CoapplicantsFolder extends PortalBoxFolder<CoapplicantDTO> {

    private static final I18n i18n = I18n.get(CoapplicantsFolder.class);

    private final ApplicationWizard wizard;

    public CoapplicantsFolder(ApplicationWizard applicationWizard) {
        super(CoapplicantDTO.class, i18n.tr("Occupant"));
        this.wizard = applicationWizard;
    }

    public boolean isOccupantsOver18areApplicants() {
        return wizard.getValue().occupantsOver18areApplicants().isBooleanTrue();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof CoapplicantDTO) {
            return new CoapplicantForm();
        } else {
            return super.create(member);
        }
    }

    class CoapplicantForm extends CEntityForm<CoapplicantDTO> {

        public CoapplicantForm() {
            super(CoapplicantDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

            int row = -1;
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().dependent())).useLabelSemicolon(false).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().matured())).useLabelSemicolon(false).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().firstName())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().lastName())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().birthDate()), 150).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().relationship())).build());
            mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().email())).build());

            // tweaks:
            get(proto().dependent()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    updateBirthDateVisibility();
                }
            });
            get(proto().matured()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    updateBirthDateVisibility();
                }
            });

            return mainPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().matured()).setVisible(isOccupantsOver18areApplicants());
            updateBirthDateVisibility();
        }

        private void updateBirthDateVisibility() {
            if (isOccupantsOver18areApplicants()) {
                get(proto().birthDate()).setVisible(getValue().dependent().getValue() && !getValue().matured().getValue());
            } else {
                get(proto().birthDate()).setVisible(getValue().dependent().getValue());
            }
        }
    }
}
