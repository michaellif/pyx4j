/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.editors;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncomeInfo;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoOther;
import com.propertyvista.domain.tenant.income.IncomeInfoSeasonallyEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSocialServices;
import com.propertyvista.domain.tenant.income.IncomeInfoStudentIncome;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.propertyvista.portal.shared.ui.util.editors.InternationalAddressEditor;

public class PersonalIncomeEditor extends CForm<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeEditor.class);

    private final SimplePanel detailsHolder = new SimplePanel();

    private final ProofOfIncomeUploaderFolder fileUpload = new ProofOfIncomeUploaderFolder();

    public PersonalIncomeEditor(ApplicationDocumentationPolicy policy) {
        super(CustomerScreeningIncome.class);
        setDocumentsPolicy(policy);
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        fileUpload.setDocumentsPolicy(policy);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().incomeSource(), new CEnumLabel()).decorate().componentWidth(250);
        formPanel.append(Location.Left, detailsHolder);
        formPanel.append(Location.Left, proto().documents(), fileUpload);

        return formPanel;
    }

    @Override
    protected void onValuePropagation(CustomerScreeningIncome value, boolean fireEvent, boolean populate) {
        selectDetailsEditor(value.incomeSource().getValue());
        super.onValuePropagation(value, fireEvent, populate);
    }

    @SuppressWarnings("unchecked")
    protected void selectDetailsEditor(IncomeSource type) {
        if (this.contains(proto().details())) {
            this.unbind(proto().details());
            detailsHolder.setWidget(null);
        }

        if (type != null && getValue() != null) {
            @SuppressWarnings("rawtypes")
            CForm editor = null;
            CustomerScreeningIncomeInfo details = getValue().details();

            switch (type) {
            case fulltime:
            case parttime:
                editor = createEmployerEditor();
                break;
            case selfemployed:
                editor = createSelfEmployedEditor();
                break;
            case seasonallyEmployed:
                editor = createSeasonallyEmployedEditor();
                break;
            case socialServices:
                editor = createSocialServicesEditor();
                break;
            case student:
                editor = createStudentIncomeEditor();
                break;
            default:
                editor = createOtherIncomeInfoEditor(type);
            }

            if (details.getInstanceValueClass() != editor.proto().getValueClass()) {
                details.set(EntityFactory.create(editor.proto().getValueClass()));
            }
            if (editor != null) {
                this.inject(proto().details(), editor);
                editor.populate(details.cast());
                detailsHolder.setWidget(editor);
            }
        }
    }

    private void validationOfStartStopDates(final CForm<? extends CustomerScreeningIncomeInfo> comp) {
        new StartEndDateValidation(comp.get(comp.proto().starts()), comp.get(comp.proto().ends()));
        comp.get(comp.proto().starts()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().ends())));
        comp.get(comp.proto().ends()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().starts())));
    }

    //
    //  Incomes variants:
    //
    private CForm<IncomeInfoEmployer> createEmployerEditor() {
        return new CForm<IncomeInfoEmployer>(IncomeInfoEmployer.class) {

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().name()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().employedForYears()).decorate().componentWidth(60);

                formPanel.append(Location.Left, proto().supervisorName()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().supervisorPhone()).decorate().componentWidth(180);

                formPanel.h3(proto().address().getMeta().getCaption());
                formPanel.append(Location.Left, proto().address(), new InternationalAddressEditor());

                injectIEmploymentInfo(formPanel, this);

                formPanel.append(Location.Left, proto().starts()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ends()).decorate().componentWidth(120);

                return formPanel;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }

            @Override
            public void generateMockData() {
                get(proto().name()).setMockValue("Nowhere");
                get(proto().supervisorName()).setMockValue("Bob");
                get(proto().supervisorPhone()).setMockValue("1234567890");
                get(proto().monthlyAmount()).setMockValue(new BigDecimal("3000"));
                get(proto().position()).setMockValue("Director");
            }
        };
    }

    private CForm<IncomeInfoSeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CForm<IncomeInfoSeasonallyEmployed>(IncomeInfoSeasonallyEmployed.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().name()).decorate().componentWidth(250);

                formPanel.append(Location.Left, proto().supervisorName()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().supervisorPhone()).decorate().componentWidth(180);

                formPanel.h3(proto().address().getMeta().getCaption());
                formPanel.append(Location.Left, proto().address(), new InternationalAddressEditor());

                injectIEmploymentInfo(formPanel, this);

                formPanel.append(Location.Left, proto().starts()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ends()).decorate().componentWidth(120);

                return formPanel;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CForm<IncomeInfoStudentIncome> createStudentIncomeEditor() {
        return new CForm<IncomeInfoStudentIncome>(IncomeInfoStudentIncome.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().name()).decorate().componentWidth(250);

                formPanel.h3(proto().address().getMeta().getCaption());
                formPanel.append(Location.Left, proto().address(), new InternationalAddressEditor());

                formPanel.h3(i18n.tr("Program Info"));
                formPanel.append(Location.Left, proto().program()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().fieldOfStudy()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().fundingChoices()).decorate().componentWidth(120);

                formPanel.append(Location.Left, proto().starts()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ends()).decorate().componentWidth(120);

                formPanel.append(Location.Left, proto().monthlyAmount()).decorate().componentWidth(120);

                return formPanel;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CForm<IncomeInfoSelfEmployed> createSelfEmployedEditor() {
        return new CForm<IncomeInfoSelfEmployed>(IncomeInfoSelfEmployed.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().name()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().employedForYears()).decorate().componentWidth(60);

                formPanel.append(Location.Left, proto().supervisorName()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().supervisorPhone()).decorate().componentWidth(180);

                formPanel.h3(proto().address().getMeta().getCaption());
                formPanel.append(Location.Left, proto().address(), new InternationalAddressEditor());

                injectIEmploymentInfo(formPanel, this);

                formPanel.append(Location.Left, proto().fullyOwned()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().monthlyRevenue()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().numberOfEmployees()).decorate().componentWidth(60);

                formPanel.append(Location.Left, proto().starts()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ends()).decorate().componentWidth(120);

                return formPanel;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CForm<IncomeInfoSocialServices> createSocialServicesEditor() {
        return new CForm<IncomeInfoSocialServices>(IncomeInfoSocialServices.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().name()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().supervisorName()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().supervisorPhone()).decorate().componentWidth(180);

                formPanel.h3(proto().address().getMeta().getCaption());
                formPanel.append(Location.Left, proto().address(), new InternationalAddressEditor());

                injectIEmploymentInfo(formPanel, this);

                formPanel.append(Location.Left, proto().starts()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ends()).decorate().componentWidth(120);

                return formPanel;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CForm<IncomeInfoOther> createOtherIncomeInfoEditor(final IncomeSource incomeSource) {
        return new CForm<IncomeInfoOther>(IncomeInfoOther.class) {

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().name()).decorate().componentWidth(250);
                formPanel.append(Location.Left, proto().monthlyAmount()).decorate().componentWidth(120);
                formPanel.append(Location.Left, proto().ends()).decorate().componentWidth(120);

                CComponent<?, ?, ?> name = get(proto().name());
                CComponent<?, ?, ?> ends = get(proto().ends());

                // some tune-up:
                switch (incomeSource) {
                case pension:
                case retired:
                    name.setVisible(false);
                    ends.setVisible(false);
                    break;

                case disabilitySupport:
                case dividends:
                case other:
                    name.setVisible(true);
                    ends.setVisible(false);
                    break;

                default:
                    name.setVisible(true);
                    ends.setVisible(true);
                }
                return formPanel;
            }
        };
    }

    private static void injectIEmploymentInfo(FormPanel formPanel, CForm<? extends IEmploymentInfo> parent) {
        formPanel.h3(i18n.tr("Employment Info"));
        formPanel.append(Location.Left, parent.proto().monthlyAmount()).decorate().componentWidth(120);
        formPanel.append(Location.Left, parent.proto().position()).decorate().componentWidth(250);
    }
}
