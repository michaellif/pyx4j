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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.VistaTheme.StyleName;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.DependentDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PeopleStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(PeopleStep.class);

    public PeopleStep() {
        super(OnlineApplicationWizardStepMeta.People);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setWidget(++row, 0, new HTML(i18n.tr("Everyone living in the residence must be listed below.")));
        panel.getWidget(row, 0).setStyleName(StyleName.WarningMessage.name());

        panel.setH3(++row, 0, 1, i18n.tr("Co-Applicant"));
        panel.setWidget(++row, 0, inject(proto().coapplicants(), new CoapplicantsFolder(getWizard())));

        panel.setH3(++row, 0, 1, i18n.tr("Dependents"));
        panel.setWidget(++row, 0, inject(proto().dependents(), new DependentsFolder(getWizard())));

        return panel;
    }

    private class CoapplicantsFolder extends PortalBoxFolder<CoapplicantDTO> {

        private final ApplicationWizard wizard;

        public CoapplicantsFolder(ApplicationWizard applicationWizard) {
            super(CoapplicantDTO.class, i18n.tr("Co-Applicant"));
            this.wizard = applicationWizard;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof CoapplicantDTO) {
                return new CoapplicantForm();
            }
            return super.create(member);
        }

        @Override
        public void generateMockData() {
            if (getItemCount() == 0) {
                CoapplicantDTO occupant = EntityFactory.create(CoapplicantDTO.class);
                addItem(occupant);
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
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().firstName())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().lastName())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().relationship())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().email())).build());

                return mainPanel;
            }

            @Override
            public void generateMockData() {
                get(proto().name().firstName()).setMockValue("Jane");
                get(proto().name().lastName()).setMockValue("Stiles");
                get(proto().relationship()).setMockValue(PersonRelationship.Spouse);
                get(proto().email()).setMockValue("JaneStiles" + (int) System.currentTimeMillis() + "@pyx4j.com");
            }
        }
    }

    private class DependentsFolder extends PortalBoxFolder<DependentDTO> {

        private final ApplicationWizard wizard;

        public DependentsFolder(ApplicationWizard applicationWizard) {
            super(DependentDTO.class, i18n.tr("Dependent"));
            this.wizard = applicationWizard;
        }

        public Integer ageOfMajority() {
            return wizard.getValue().ageOfMajority().getValue();
        }

        public boolean enforceAgeOfMajority() {
            return wizard.getValue().enforceAgeOfMajority().isBooleanTrue();
        }

        public boolean maturedOccupantsAreApplicants() {
            return wizard.getValue().maturedOccupantsAreApplicants().isBooleanTrue();
        }

        @Override
        protected void createNewEntity(AsyncCallback<DependentDTO> callback) {
            DependentDTO entity = EntityFactory.create(DependentDTO.class);

            callback.onSuccess(entity);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof DependentDTO) {
                return new DependentForm();
            } else {
                return super.create(member);
            }
        }

        @Override
        public void generateMockData() {
            if (getItemCount() == 0) {
                DependentDTO occupant = EntityFactory.create(DependentDTO.class);

                addItem(occupant);
            }
        }

        class DependentForm extends CEntityForm<DependentDTO> {

            public DependentForm() {
                super(DependentDTO.class);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

                int row = -1;
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().firstName())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name().lastName())).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().birthDate()), 150).build());
                mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().relationship())).build());

                return mainPanel;
            }

            @Override
            public void generateMockData() {
                get(proto().name().firstName()).setMockValue("Bob");
                get(proto().name().lastName()).setMockValue("Stiles");
                get(proto().relationship()).setMockValue(PersonRelationship.Son);
                get(proto().birthDate()).setMockValue(new LogicalDate(102, 3, 5));
            }

            @Override
            public void addValidations() {
                super.addValidations();

                get(proto().birthDate()).addValueValidator(new EditableValueValidator<LogicalDate>() {
                    @Override
                    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                        if (value != null && getValue() != null) {
                            if (maturedOccupantsAreApplicants()) {
                                if (TimeUtils.isOlderThan(value, ageOfMajority())) {
                                    return new ValidationError(component, i18n.tr(
                                            "This person is matured and should be Co-Applicant!. According to regulations age of majority is {0}.",
                                            ageOfMajority()));
                                }
                            }
                        }
                        return null;
                    }
                });
            }
        }
    }
}
