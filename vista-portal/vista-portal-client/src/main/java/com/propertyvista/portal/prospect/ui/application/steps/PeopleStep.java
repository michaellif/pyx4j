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

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.theme.VistaTheme.StyleName;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.prospect.dto.CoapplicantDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.DependentDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.shared.services.dev.MockDataGenerator;

public class PeopleStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(PeopleStep.class);

    public PeopleStep() {
        super(OnlineApplicationWizardStepMeta.People);
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());

        HTML message = new HTML(i18n.tr("Everyone living in the residence must be listed below."));
        message.setStyleName(StyleName.WarningMessage.name());
        formPanel.append(Location.Left, message);

        formPanel.h3(i18n.tr("Applicant"));
        formPanel.append(Location.Left, proto().applicant(), new CEntityLabel<Name>());
        get(proto().applicant()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);

        formPanel.h3(i18n.tr("Co-Applicants"));
        formPanel.append(Location.Left, proto().coapplicants(), new CoapplicantsFolder(getWizard()));

        formPanel.h3(i18n.tr("Dependents"));
        formPanel.append(Location.Left, proto().dependents(), new DependentsFolder(getWizard()));

        return formPanel;
    }

    private class CoapplicantsFolder extends PortalBoxFolder<CoapplicantDTO> {

        public CoapplicantsFolder(ApplicationWizard applicationWizard) {
            super(CoapplicantDTO.class, i18n.tr("Co-Applicant"));
        }

        @Override
        protected CForm<CoapplicantDTO> createItemForm(IObject<?> member) {
            return new CoapplicantForm();
        }

        @Override
        public void generateMockData() {
            if (getItemCount() == 0) {
                CoapplicantDTO occupant = EntityFactory.create(CoapplicantDTO.class);
                addItem(occupant);
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            this.addComponentValidator(new AbstractComponentValidator<IList<CoapplicantDTO>>() {
                @Override
                public AbstractValidationError isValid() {
                    if (hasDuplicateEmails(getCComponent().getValue())) {
                        return new BasicValidationError(getCComponent(), i18n.tr("Co-Applicants have the same email address"));
                    }
                    return null;
                }

                private boolean hasDuplicateEmails(IList<CoapplicantDTO> value) {
                    boolean duplicate = false;
                    Collection<String> emails = new ArrayList<>();

                    for (CoapplicantDTO coap : value) {
                        if (!coap.email().isNull()) {
                            if (emails.contains(coap.email().getValue())) {
                                duplicate = true;
                                break;
                            }
                            emails.add(coap.email().getValue());
                        }
                    }

                    return duplicate;
                }
            });
        }

        class CoapplicantForm extends CForm<CoapplicantDTO> {

            public CoapplicantForm() {
                super(CoapplicantDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().name().firstName()).decorate();
                formPanel.append(Location.Left, proto().name().lastName()).decorate();
                formPanel.append(Location.Left, proto().relationship()).decorate();
                formPanel.append(Location.Left, proto().email()).decorate();

                return formPanel;
            }

            @Override
            public void generateMockData() {
                GWT.<MockDataGenerator> create(MockDataGenerator.class).getPerson(new DefaultAsyncCallback<Person>() {

                    @Override
                    public void onSuccess(Person person) {
                        get(proto().name().firstName()).setMockValue(person.name().firstName().getValue());
                        get(proto().name().lastName()).setMockValue(person.name().lastName().getValue());
                        get(proto().relationship()).setMockValue(PersonRelationship.Spouse);
                        get(proto().email()).setMockValue(person.email().getValue());
                    }
                });
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
            return wizard.getValue().enforceAgeOfMajority().getValue(false);
        }

        public boolean maturedOccupantsAreApplicants() {
            return wizard.getValue().maturedOccupantsAreApplicants().getValue(false);
        }

        @Override
        protected void createNewEntity(AsyncCallback<DependentDTO> callback) {
            DependentDTO entity = EntityFactory.create(DependentDTO.class);

            callback.onSuccess(entity);
        }

        @Override
        protected CForm<DependentDTO> createItemForm(IObject<?> member) {
            return new DependentForm();
        }

        @Override
        public void generateMockData() {
            if (getItemCount() == 0) {
                DependentDTO occupant = EntityFactory.create(DependentDTO.class);

                addItem(occupant);
            }
        }

        class DependentForm extends CForm<DependentDTO> {

            public DependentForm() {
                super(DependentDTO.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().name().firstName()).decorate();
                formPanel.append(Location.Left, proto().name().lastName()).decorate();
                formPanel.append(Location.Left, proto().relationship()).decorate();
                formPanel.append(Location.Left, proto().birthDate()).decorate().componentWidth(150);

                return formPanel;
            }

            @Override
            public void generateMockData() {
                get(proto().relationship()).setMockValue(PersonRelationship.Son);
                get(proto().birthDate()).setMockValue(new LogicalDate(102, 3, 5));

                GWT.<MockDataGenerator> create(MockDataGenerator.class).getPerson(new DefaultAsyncCallback<Person>() {

                    @Override
                    public void onSuccess(Person person) {
                        get(proto().name().firstName()).setMockValue(person.name().firstName().getValue());
                        get(proto().name().lastName()).setMockValue(person.name().lastName().getValue());
                    }
                });
            }

            @Override
            public void addValidations() {
                super.addValidations();

                get(proto().birthDate()).addComponentValidator(new BirthdayDateValidator());
                get(proto().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
                    @Override
                    public BasicValidationError isValid() {
                        if (getCComponent().getValue() != null && getValue() != null) {
                            if (maturedOccupantsAreApplicants()) {
                                if (TimeUtils.isOlderThan(getCComponent().getValue(), ageOfMajority())) {
                                    return new BasicValidationError(getCComponent(), i18n.tr(
                                            "According to internal regulations and age this person cannot be a Dependent. Age of majority is {0}",
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
