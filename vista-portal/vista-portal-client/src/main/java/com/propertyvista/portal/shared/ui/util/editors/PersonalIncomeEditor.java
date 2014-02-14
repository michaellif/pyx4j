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
package com.propertyvista.portal.shared.ui.util.editors;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
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
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PersonalIncomeEditor extends CEntityForm<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeEditor.class);

    private final SimplePanel detailsHolder = new SimplePanel();

    private final ProofOfEmploymentUploaderFolder fileUpload = new ProofOfEmploymentUploaderFolder();

    public PersonalIncomeEditor(ApplicationDocumentationPolicy policy) {
        super(CustomerScreeningIncome.class);
        setDocumentsPolicy(policy);
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        fileUpload.setDocumentsPolicy(policy);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();
        int row = -1;

        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().incomeSource(), new CEnumLabel()), 250).build());
        main.setWidget(++row, 0, detailsHolder);
        main.setWidget(++row, 0, inject(proto().documents(), fileUpload));

        return main;
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

        fileUpload.setVisible(false);
        if (type != null && getValue() != null) {
            @SuppressWarnings("rawtypes")
            CEntityForm editor = null;
            CustomerScreeningIncomeInfo details = getValue().details();

            switch (type) {
            case fulltime:
            case parttime:
                editor = createEmployerEditor();
                fileUpload.setVisible(true);
                break;
            case selfemployed:
                editor = createSelfEmployedEditor();
                fileUpload.setVisible(true);
                break;
            case seasonallyEmployed:
                editor = createSeasonallyEmployedEditor();
                fileUpload.setVisible(true);
                break;
            case socialServices:
                editor = createSocialServicesEditor();
                fileUpload.setVisible(true);
                break;
            case student:
                editor = createStudentIncomeEditor();
                fileUpload.setVisible(true);
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

    private void validationOfStartStopDates(final CEntityForm<? extends CustomerScreeningIncomeInfo> comp) {
        new StartEndDateValidation(comp.get(comp.proto().starts()), comp.get(comp.proto().ends()));
        comp.get(comp.proto().starts()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().ends())));
        comp.get(comp.proto().ends()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().starts())));
    }

    //
    //  Incomes variants:
    //
    private CEntityForm<IncomeInfoEmployer> createEmployerEditor() {
        return new CEntityForm<IncomeInfoEmployer>(IncomeInfoEmployer.class) {

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().employedForYears()), 60).build());

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorName()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorPhone()), 180).build());

                main.setH3(++row, 0, 1, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressSimpleEditor()));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().starts()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().ends()), 120).build());

                return main;
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

    private CEntityForm<IncomeInfoSeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityForm<IncomeInfoSeasonallyEmployed>(IncomeInfoSeasonallyEmployed.class) {
            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name()), 250).build());

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorName()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorPhone()), 180).build());

                main.setH3(++row, 0, 1, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressSimpleEditor()));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().starts()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().ends()), 120).build());

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityForm<IncomeInfoStudentIncome> createStudentIncomeEditor() {
        return new CEntityForm<IncomeInfoStudentIncome>(IncomeInfoStudentIncome.class) {
            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name()), 250).build());

                main.setH3(++row, 0, 1, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressSimpleEditor()));

                main.setH3(++row, 0, 1, i18n.tr("Program Info"));
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().program()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().fieldOfStudy()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().fundingChoices()), 120).build());

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().starts()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().ends()), 120).build());

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().monthlyAmount()), 120).build());

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityForm<IncomeInfoSelfEmployed> createSelfEmployedEditor() {
        return new CEntityForm<IncomeInfoSelfEmployed>(IncomeInfoSelfEmployed.class) {
            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().employedForYears()), 60).build());

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorName()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorPhone()), 180).build());

                main.setH3(++row, 0, 1, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressSimpleEditor()));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().fullyOwned()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().monthlyRevenue()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().numberOfEmployees()), 60).build());

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().starts()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().ends()), 120).build());

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityForm<IncomeInfoSocialServices> createSocialServicesEditor() {
        return new CEntityForm<IncomeInfoSocialServices>(IncomeInfoSocialServices.class) {
            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().name()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorName()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().supervisorPhone()), 180).build());

                main.setH3(++row, 0, 1, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressSimpleEditor()));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().starts()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().ends()), 120).build());

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityForm<IncomeInfoOther> createOtherIncomeInfoEditor(final IncomeSource incomeSource) {
        return new CEntityForm<IncomeInfoOther>(IncomeInfoOther.class) {

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel main = new BasicFlexFormPanel();

                CComponent<?> name, ends;

                int row = -1;
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(name = inject(proto().name()), 250).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().monthlyAmount()), 120).build());
                main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(ends = inject(proto().ends()), 120).build());

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
                return main;
            }
        };
    }

    private static int injectIEmploymentInfo(BasicFlexFormPanel main, int row, CEntityForm<? extends IEmploymentInfo> parent) {
        main.setH3(++row, 0, 1, i18n.tr("Employment Info"));
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(parent.inject(parent.proto().monthlyAmount()), 120).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(parent.inject(parent.proto().position()), 250).build());
        return row;
    }
}
