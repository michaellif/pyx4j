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
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.editors.AddressSimpleEditor;
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

public class PersonalIncomeEditor extends CEntityForm<CustomerScreeningIncome> {

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
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        int row = -1;

        main.setWidget(++row, 0, 2, inject(proto().incomeSource(), new CEnumLabel(), new FieldDecoratorBuilder(25, true).build()));
        main.setWidget(++row, 0, 2, detailsHolder);
        main.setWidget(++row, 0, 2, inject(proto().documents(), fileUpload));

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

        if (type != null && getValue() != null) {
            @SuppressWarnings("rawtypes")
            CEntityForm editor = null;
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
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder(20).build()));
                main.setWidget(++row, 0, inject(proto().employedForYears(), new FieldDecoratorBuilder(5).build()));

                row = -1;
                main.setWidget(++row, 1, inject(proto().supervisorName(), new FieldDecoratorBuilder(20).build()));
                main.setWidget(++row, 1, inject(proto().supervisorPhone(), new FieldDecoratorBuilder(15).build()));

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, 2, inject(proto().address(), new AddressSimpleEditor(false)));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, inject(proto().starts(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(row, 1, inject(proto().ends(), new FieldDecoratorBuilder(10).build()));

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);

                if (ApplicationMode.isDevelopment()) {
                    this.addDevShortcutHandler(new DevShortcutHandler() {
                        @Override
                        public void onDevShortcut(DevShortcutEvent event) {
                            if (event.getKeyCode() == 'Q') {
                                event.consume();
                                devGenerateIncomeInfoEmployer();
                            }
                        }
                    });
                }
            }

            private void devGenerateIncomeInfoEmployer() {
                get(proto().name()).setValue("Nowhere");
                get(proto().supervisorName()).setValue("Bob");
                get(proto().supervisorPhone()).setValue("1234567890");
                get(proto().monthlyAmount()).setValue(new BigDecimal("3000"));
            }
        };
    }

    private CEntityForm<IncomeInfoSeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityForm<IncomeInfoSeasonallyEmployed>(IncomeInfoSeasonallyEmployed.class) {
            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder(20).build()));

                row = -1;
                main.setWidget(++row, 1, inject(proto().supervisorName(), new FieldDecoratorBuilder(20).build()));
                main.setWidget(++row, 1, inject(proto().supervisorPhone(), new FieldDecoratorBuilder(15).build()));

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, 2, inject(proto().address(), new AddressSimpleEditor(false)));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, inject(proto().starts(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(row, 1, inject(proto().ends(), new FieldDecoratorBuilder(10).build()));

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
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, 2, inject(proto().name(), new FieldDecoratorBuilder(25).build()));

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, 2, inject(proto().address(), new AddressSimpleEditor(false)));

                main.setH3(++row, 0, 2, i18n.tr("Program Info"));
                main.setWidget(++row, 0, inject(proto().program(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(++row, 0, inject(proto().fieldOfStudy(), new FieldDecoratorBuilder(25).build()));
                main.setWidget(++row, 0, inject(proto().fundingChoices(), new FieldDecoratorBuilder(10).build()));

                row -= 3;
                main.setWidget(++row, 1, inject(proto().starts(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(++row, 1, inject(proto().ends(), new FieldDecoratorBuilder(10).build()));

                main.setWidget(++row, 0, inject(proto().monthlyAmount(), new FieldDecoratorBuilder(10).build()));

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
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder(20).build()));
                main.setWidget(++row, 0, inject(proto().employedForYears(), new FieldDecoratorBuilder(5).build()));

                row = -1;
                main.setWidget(++row, 1, inject(proto().supervisorName(), new FieldDecoratorBuilder(20).build()));
                main.setWidget(++row, 1, inject(proto().supervisorPhone(), new FieldDecoratorBuilder(15).build()));

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, 2, inject(proto().address(), new AddressSimpleEditor(false)));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, inject(proto().fullyOwned(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(++row, 0, inject(proto().monthlyRevenue(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(row, 1, inject(proto().numberOfEmployees(), new FieldDecoratorBuilder(5).build()));

                main.setWidget(++row, 0, inject(proto().starts(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(row, 1, inject(proto().ends(), new FieldDecoratorBuilder(10).build()));

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
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                int row = -1;
                main.setWidget(++row, 0, inject(proto().name(), new FieldDecoratorBuilder(20).build()));

                row = -1;
                main.setWidget(++row, 1, inject(proto().supervisorName(), new FieldDecoratorBuilder(20).build()));
                main.setWidget(++row, 1, inject(proto().supervisorPhone(), new FieldDecoratorBuilder(15).build()));

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, 2, inject(proto().address(), new AddressSimpleEditor(false)));

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, inject(proto().starts(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(row, 1, inject(proto().ends(), new FieldDecoratorBuilder(10).build()));

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
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

                CComponent<?, ?, ?> name, ends;

                int row = -1;
                main.setWidget(++row, 0, name = inject(proto().name(), new FieldDecoratorBuilder(25).build()));
                main.setWidget(++row, 0, inject(proto().monthlyAmount(), new FieldDecoratorBuilder(10).build()));
                main.setWidget(++row, 0, ends = inject(proto().ends(), new FieldDecoratorBuilder(10).build()));

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

    private static int injectIEmploymentInfo(TwoColumnFlexFormPanel main, int row, CEntityForm<? extends IEmploymentInfo> parent) {
        main.setH3(++row, 0, 2, i18n.tr("Employment Info"));
        main.setWidget(++row, 0, parent.inject(parent.proto().monthlyAmount(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(row, 1, parent.inject(parent.proto().position(), new FieldDecoratorBuilder(20).build()));
        return row;
    }
}
