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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.ProofOfEmploymentUploaderFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoOther;
import com.propertyvista.domain.tenant.income.IncomeInfoSeasonallyEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSocialServices;
import com.propertyvista.domain.tenant.income.IncomeInfoStudentIncome;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.domain.tenant.income.PersonalIncome;

public class PersonalIncomeEditor extends CEntityDecoratableForm<PersonalIncome> {

    private static final I18n i18n = I18n.get(PersonalIncomeEditor.class);

    private ProofOfEmploymentUploaderFolder fileUpload;

    public PersonalIncomeEditor() {
        super(PersonalIncome.class);
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
                    setVisibility(event.getValue());
                }
            });
            main.setWidget(++row, 0, new DecoratorBuilder(incomeSource, 25).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().incomeSource()), 25).build());
        }

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, inject(proto().employer(), createEmployerEditor()));
        main.setWidget(++row, 0, inject(proto().seasonallyEmployed(), createSeasonallyEmployedEditor()));
        main.setWidget(++row, 0, inject(proto().selfEmployed(), createSelfEmployedEditor()));
        main.setWidget(++row, 0, inject(proto().studentIncome(), createStudentIncomeEditor()));
        main.setWidget(++row, 0, inject(proto().socialServices(), createSocialServicesEditor()));
        main.setWidget(++row, 0, inject(proto().otherIncomeInformation(), createOtherIncomeInfoEditor()));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().documents(), fileUpload = new ProofOfEmploymentUploaderFolder())).componentWidth(30)
                .build());

        return main;
    }

    @Override
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

        setVisibility(getValue().incomeSource().getValue());
    }

    @SuppressWarnings("unchecked")
    private void setVisibility(IncomeSource incomeSource) {
        get(proto().employer()).setVisible(false);
        get(proto().seasonallyEmployed()).setVisible(false);
        get(proto().selfEmployed()).setVisible(false);
        get(proto().studentIncome()).setVisible(false);
        get(proto().socialServices()).setVisible(false);
        get(proto().otherIncomeInformation()).setVisible(false);
        fileUpload.setVisible(false);

        if (incomeSource != null) {
            switch (incomeSource) {
            case fulltime:
            case parttime:
                get(proto().employer()).setVisible(true);
                fileUpload.setVisible(true);
                break;
            case selfemployed:
                get(proto().selfEmployed()).setVisible(true);
                fileUpload.setVisible(true);
                break;
            case seasonallyEmployed:
                get(proto().seasonallyEmployed()).setVisible(true);
                fileUpload.setVisible(true);
                break;
            case socialServices:
                get(proto().socialServices()).setVisible(true);
                fileUpload.setVisible(true);
                break;
            case student:
                get(proto().studentIncome()).setVisible(true);
                fileUpload.setVisible(true);
                break;
            default:
                @SuppressWarnings("rawtypes")
                CEntityForm comp = (CEntityForm) get(proto().otherIncomeInformation());
                comp.setVisible(true);
                applyOtherLables(incomeSource, comp);
            }
        }
    }

    private void applyOtherLables(IncomeSource incomeSource, CEntityForm<IncomeInfoOther> comp) {
        switch (incomeSource) {
        case pension:
        case retired:
            comp.get(comp.proto().name()).setVisible(false);
            comp.get(comp.proto().ends()).setVisible(false);
            break;
        case odsp:
        case dividends:
        case other:
            comp.get(comp.proto().name()).setVisible(true);
            comp.get(comp.proto().ends()).setVisible(false);
            break;

        default:
            comp.get(comp.proto().name()).setVisible(true);
            comp.get(comp.proto().ends()).setVisible(true);
        }
    }

    private void validationOfStartStopDates(final CEntityForm<? extends IIncomeInfo> comp) {
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

                main.getColumnFormatter().setWidth(0, "50%");
                main.getColumnFormatter().setWidth(1, "50%");
                main.setWidth("100%");
                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
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

    private CEntityForm<IncomeInfoOther> createOtherIncomeInfoEditor() {
        return new CEntityDecoratableForm<IncomeInfoOther>(IncomeInfoOther.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();

                int row = -1;
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 25).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().monthlyAmount()), 9).build());
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().ends()), 9).build());

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
