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
package com.propertyvista.crm.client.ui.crud.tenant;

import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.components.ApplicationDocumentsFolderUploader;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.domain.media.ApplicationDocument.DocumentType;
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

public class TenantFinancialViewIncomeForm extends CEntityFolderItem<PersonalIncome> {

    private static I18n i18n = I18n.get(TenantFinancialViewIncomeForm.class);

    private final boolean readOnlyMode;

    private ApplicationDocumentsFolderUploader fileUpload;

    public TenantFinancialViewIncomeForm(boolean readOnlyMode) {
        super(PersonalIncome.class);
        this.readOnlyMode = readOnlyMode;
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

        if (!readOnlyMode) {
            @SuppressWarnings("unchecked")
            CComboBox<IncomeSource> incomeSource = (CComboBox<IncomeSource>) inject(proto().incomeSource());
            incomeSource.addValueChangeHandler(new ValueChangeHandler<IncomeSource>() {
                @Override
                public void onValueChange(ValueChangeEvent<IncomeSource> event) {
                    setVisibility(event.getValue());
                }
            });
            main.add(incomeSource, 20);
        } else {
            main.add(inject(proto().incomeSource()), 20);
        }

        main.add(inject(proto().employer(), createEmployerEditor()));
        main.add(inject(proto().seasonallyEmployed(), createSeasonallyEmployedEditor()));
        main.add(inject(proto().selfEmployed(), createSelfEmployedEditor()));
        main.add(inject(proto().studentIncome(), createStudentIncomeEditor()));
        main.add(inject(proto().socialServices(), createSocialServicesEditor()));
        main.add(inject(proto().otherIncomeInfo(), createOtherIncomeInfoEditor()));

        if (!readOnlyMode) {
            main.add(inject(proto().documents(), fileUpload = new ApplicationDocumentsFolderUploader(DocumentType.income)));
            fileUpload.asWidget().getElement().getStyle().setMarginLeft(14, Unit.EM);
            fileUpload.asWidget().getElement().getStyle().setMarginTop(1, Unit.EM);
            fileUpload.asWidget().getElement().getStyle().setMarginBottom(1, Unit.EM);
        }

        return main;
    }

    @Override
    public IFolderItemDecorator<PersonalIncome> createDecorator() {
        return new BoxFolderItemDecorator<PersonalIncome>(CrmImages.INSTANCE, i18n.tr("Remove income"));
    }

    @Override
    public void populate(PersonalIncome value) {
        super.populate(value);
        setVisibility(value.incomeSource().getValue());
        if (value != null && fileUpload != null) {
            fileUpload.setTenantID(((IEntity) (value.getParent().getParent())).getPrimaryKey());
        }
    }

    @SuppressWarnings("unchecked")
    private void setVisibility(IncomeSource incomeSource) {
        get(proto().employer()).setVisible(false);
        get(proto().seasonallyEmployed()).setVisible(false);
        get(proto().selfEmployed()).setVisible(false);
        get(proto().studentIncome()).setVisible(false);
        get(proto().socialServices()).setVisible(false);
        get(proto().otherIncomeInfo()).setVisible(false);
        if (fileUpload != null) {
            fileUpload.setVisible(false);
        }

        if (incomeSource != null) {
            switch (incomeSource) {
            case fulltime:
            case parttime:
                get(proto().employer()).setVisible(true);
                if (fileUpload != null) {
                    fileUpload.setVisible(true);
                }
                break;
            case selfemployed:
                get(proto().selfEmployed()).setVisible(true);
                if (fileUpload != null) {
                    fileUpload.setVisible(true);
                }
                break;
            case seasonallyEmployed:
                get(proto().seasonallyEmployed()).setVisible(true);
                if (fileUpload != null) {
                    fileUpload.setVisible(true);
                }
                break;
            case socialServices:
                get(proto().socialServices()).setVisible(true);
                if (fileUpload != null) {
                    fileUpload.setVisible(true);
                }
                break;
            case student:
                get(proto().studentIncome()).setVisible(true);
                if (fileUpload != null) {
                    fileUpload.setVisible(true);
                }
                break;
            default:
                @SuppressWarnings("rawtypes")
                CEntityEditor comp = (CEntityEditor) get(proto().otherIncomeInfo());
                comp.setVisible(true);
                applyOtherLables(incomeSource, comp);
            }
        }
    }

    private void applyOtherLables(IncomeSource incomeSource, CEntityEditor<IncomeInfoOther> comp) {
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

    private static void injectIEmploymentInfo(VistaDecoratorsFlowPanel main, IEmploymentInfo proto, CEntityEditor<?> parent) {
        main.add(parent.inject(proto.supervisorName()), 20);
        main.add(parent.inject(proto.supervisorPhone()), 15);
        main.add(parent.inject(proto.monthlyAmount()), 8);
        main.add(parent.inject(proto.position()), 20);
    }

    private CEntityEditor<IncomeInfoEmployer> createEmployerEditor() {
        return new CEntityEditor<IncomeInfoEmployer>(IncomeInfoEmployer.class) {

            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

                main.add(inject(proto().name()), 12);
                main.add(inject(proto().employedForYears()), 4);

                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split);

                AddressUtils.injectIAddress(split, proto(), this);

                VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split2);

                injectIEmploymentInfo(split2.getLeftPanel(), proto(), this);

                split2.getRightPanel().add(inject(proto().starts()), 8.2);
                split2.getRightPanel().add(inject(proto().ends()), 8.2);

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityEditor<IncomeInfoSeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityEditor<IncomeInfoSeasonallyEmployed>(IncomeInfoSeasonallyEmployed.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

                main.add(inject(proto().name()), 10);

                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split);

                AddressUtils.injectIAddress(split, proto(), this);

                VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split2);

                injectIEmploymentInfo(split2.getLeftPanel(), proto(), this);

                split2.getRightPanel().add(inject(proto().starts()), 8.2);
                split2.getRightPanel().add(inject(proto().ends()), 8.2);

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityEditor<IncomeInfoStudentIncome> createStudentIncomeEditor() {
        return new CEntityEditor<IncomeInfoStudentIncome>(IncomeInfoStudentIncome.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

                main.add(inject(proto().name()), 10);

                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split);

                AddressUtils.injectIAddress(split, proto(), this);

                VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split2);

                split2.getLeftPanel().add(inject(proto().program()), 10);
                split2.getLeftPanel().add(inject(proto().fieldOfStudy()), 10);
                split2.getLeftPanel().add(inject(proto().fundingChoices()), 10);
                split2.getLeftPanel().add(inject(proto().monthlyAmount()), 10);

                split2.getRightPanel().add(inject(proto().starts()), 8.2);
                split2.getRightPanel().add(inject(proto().ends()), 8.2);

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityEditor<IncomeInfoSelfEmployed> createSelfEmployedEditor() {
        return new CEntityEditor<IncomeInfoSelfEmployed>(IncomeInfoSelfEmployed.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

                main.add(inject(proto().name()), 10);

                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split);

                AddressUtils.injectIAddress(split, proto(), this);

                VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split2);

                injectIEmploymentInfo(split2.getLeftPanel(), proto(), this);

                split2.getLeftPanel().add(inject(proto().fullyOwned()), 10);
                split2.getLeftPanel().add(inject(proto().monthlyRevenue()), 10);
                split2.getLeftPanel().add(inject(proto().numberOfEmployees()), 4);

                split2.getRightPanel().add(inject(proto().starts()), 8.2);
                split2.getRightPanel().add(inject(proto().ends()), 8.2);

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityEditor<IncomeInfoSocialServices> createSocialServicesEditor() {
        return new CEntityEditor<IncomeInfoSocialServices>(IncomeInfoSocialServices.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

                main.add(inject(proto().name()), 10);

                VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split);

                AddressUtils.injectIAddress(split, proto(), this);

                VistaDecoratorsSplitFlowPanel split2 = new VistaDecoratorsSplitFlowPanel(!TenantFinancialViewIncomeForm.this.isEditable());
                main.add(new VistaLineSeparator());
                main.add(split2);

                injectIEmploymentInfo(split2.getLeftPanel(), proto(), this);

                split2.getRightPanel().add(inject(proto().starts()), 8.2);
                split2.getRightPanel().add(inject(proto().ends()), 8.2);

                return main;
            }

            @Override
            public void addValidations() {
                super.addValidations();
                validationOfStartStopDates(this);
            }
        };
    }

    private CEntityEditor<IncomeInfoOther> createOtherIncomeInfoEditor() {
        return new CEntityEditor<IncomeInfoOther>(IncomeInfoOther.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);

                main.add(inject(proto().name()), 10);
                main.add(inject(proto().monthlyAmount()), 10);
                main.add(inject(proto().ends()), 8.2);

                return main;
            }
        };
    }

    void validationOfStartStopDates(final CEntityEditor<? extends IIncomeInfo> comp) {
        comp.get(comp.proto().starts()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = comp.getValue().ends();
                return (value != null) && (date.isNull() || value.before(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The start date cannot be equal or after end date.");
            }
        });

        comp.get(comp.proto().starts()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().ends())));

        comp.get(comp.proto().ends()).addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                IPrimitive<LogicalDate> date = comp.getValue().starts();
                return (value != null) && (date.isNull() || value.after(date.getValue()));
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("End Date must be greater than Start Date");
            }
        });

        comp.get(comp.proto().ends()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().starts())));
    }
}
