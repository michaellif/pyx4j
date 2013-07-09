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
package com.propertyvista.common.client.ui.components.editors;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.ProofOfEmploymentUploaderFolder;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
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

public class PersonalIncomeEditor extends CEntityDecoratableForm<CustomerScreeningIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeEditor.class);

    protected final SimplePanel detailsHolder = new SimplePanel();

    private ProofOfEmploymentUploaderFolder fileUpload;

    public PersonalIncomeEditor() {
        super(CustomerScreeningIncome.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        int row = -1;
        if (isEditable()) {
            @SuppressWarnings("unchecked")
            CComboBox<IncomeSource> incomeSource = (CComboBox<IncomeSource>) inject(proto().incomeSource());
            incomeSource.addValueChangeHandler(new ValueChangeHandler<IncomeSource>() {
                @Override
                public void onValueChange(ValueChangeEvent<IncomeSource> event) {
                    selectDetailsEditor(event.getValue());
                }
            });
            main.setWidget(++row, 0, new DecoratorBuilder(incomeSource, 25).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().incomeSource()), 25).build());
        }

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, detailsHolder);
        main.setWidget(++row, 0, inject(proto().documents(), fileUpload = new ProofOfEmploymentUploaderFolder()));

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
        return new CEntityDecoratableForm<IncomeInfoEmployer>(IncomeInfoEmployer.class) {

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().employedForYears()), 4).build());

                row = -1;
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorName()), 20).build());
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorPhone()), 15).build());

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressStructuredEditor()));
                main.getFlexCellFormatter().setColSpan(row, 0, 2);

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().starts()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().ends()), 9).build());

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
        return new CEntityDecoratableForm<IncomeInfoSeasonallyEmployed>(IncomeInfoSeasonallyEmployed.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());

                row = -1;
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorName()), 20).build());
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorPhone()), 15).build());

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressStructuredEditor()));
                main.getFlexCellFormatter().setColSpan(row, 0, 2);

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().starts()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().ends()), 9).build());

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
        return new CEntityDecoratableForm<IncomeInfoStudentIncome>(IncomeInfoStudentIncome.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());

                main.setH3(++row, 0, 1, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressStructuredEditor()));

                main.setH3(++row, 0, 1, i18n.tr("Program Info"));
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().program()), 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fieldOfStudy()), 25).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fundingChoices()), 10).build());

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().monthlyAmount()), 10).build());

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().starts()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().ends()), 9).build());

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
        return new CEntityDecoratableForm<IncomeInfoSelfEmployed>(IncomeInfoSelfEmployed.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().employedForYears()), 4).build());

                row = -1;
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorName()), 20).build());
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorPhone()), 15).build());

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressStructuredEditor()));
                main.getFlexCellFormatter().setColSpan(row, 0, 2);

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fullyOwned()), 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().monthlyRevenue()), 10).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfEmployees()), 4).build());

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().starts()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().ends()), 9).build());

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
        return new CEntityDecoratableForm<IncomeInfoSocialServices>(IncomeInfoSocialServices.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());

                row = -1;
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorName()), 20).build());
                main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().supervisorPhone()), 15).build());

                main.setH3(++row, 0, 2, proto().address().getMeta().getCaption());
                main.setWidget(++row, 0, inject(proto().address(), new AddressStructuredEditor()));
                main.getFlexCellFormatter().setColSpan(row, 0, 2);

                row = injectIEmploymentInfo(main, row, this);

                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().starts()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().ends()), 9).build());

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
        return new CEntityDecoratableForm<IncomeInfoOther>(IncomeInfoOther.class) {

            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                CComponent<?> name, ends;

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(name = inject(proto().name()), 25).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().monthlyAmount()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(ends = inject(proto().ends()), 9).build());

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

    private static int injectIEmploymentInfo(FormFlexPanel main, int row, CEntityDecoratableForm<? extends IEmploymentInfo> parent) {
        main.setH3(++row, 0, 2, i18n.tr("Employment Info"));
        main.setWidget(++row, 0, parent.new DecoratorBuilder(parent.inject(parent.proto().monthlyAmount()), 9).build());
        main.setWidget(++row, 0, parent.new DecoratorBuilder(parent.inject(parent.proto().position()), 20).build());
        return row;
    }
}
