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
package com.propertyvista.portal.ptapp.client.ui.steps.financial;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.flex.CEntityComponent;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.editor.CPolymorphicEntityEditor;
import com.pyx4j.entity.client.ui.flex.editor.IDiscriminator;
import com.pyx4j.entity.client.ui.flex.editor.IPolymorphicEditorDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderBoxEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.AddressUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoOther;
import com.propertyvista.domain.tenant.income.IncomeInfoSeasonallyEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeInfoSocialServices;
import com.propertyvista.domain.tenant.income.IncomeInfoStudentIncome;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;

public class FinancialViewIncomeForm2 extends CEntityFolderBoxEditor<IIncomeInfo> {

    private static I18n i18n = I18nFactory.getI18n(FinancialViewIncomeForm2.class);

    private final boolean summaryViewMode;

    private CPolymorphicEntityEditor<IIncomeInfo> editor;

    public FinancialViewIncomeForm2(boolean summaryViewMode) {
        super(IIncomeInfo.class);
        this.summaryViewMode = summaryViewMode;
    }

    @Override
    public IsWidget createContent() {
        editor = new CPolymorphicEntityEditor<IIncomeInfo>(IIncomeInfo.class, Arrays.asList(IncomeSource.values())) {

            @Override
            protected CEntityEditor<? extends IIncomeInfo> createItem(IDiscriminator<IIncomeInfo> discriminator) {
                switch ((IncomeSource) discriminator) {
                case fulltime:
                    return createEmployerEditor();
                case parttime:
                    return createEmployerEditor();
                case selfemployed:
                    return createSelfEmployedEditor();
                default:
                    return createSelfEmployedEditor();
                }
            }

            @Override
            protected IPolymorphicEditorDecorator<IIncomeInfo> createDecorator() {
                // TODO Auto-generated method stub
                return new IPolymorphicEditorDecorator<IIncomeInfo>() {

                    private final SimplePanel panel = new SimplePanel();

                    @Override
                    public Widget asWidget() {
                        return panel;
                    }

                    @Override
                    public HandlerRegistration addItemSwitchClickHandler(ClickHandler handler) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public void setComponent(CPolymorphicEntityEditor<IIncomeInfo> content) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onValueChange(ValueChangeEvent<IIncomeInfo> event) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void setEditor(CPolymorphicEntityEditor<IIncomeInfo> w) {
                        panel.setWidget(w.getContainer());

                    }
                };
            }

        };

        return editor;
    }

    @Override
    public void onBound(CEntityComponent<?, ?> parent) {
        super.onBound(parent);
        editor.onBound(this);
    }

    @Override
    public IFolderItemDecorator<IIncomeInfo> createDecorator() {
        if (summaryViewMode) {
            return new BoxReadOnlyFolderItemDecorator<IIncomeInfo>(!isFirst());
        } else {
            return new BoxFolderItemDecorator<IIncomeInfo>(PortalImages.INSTANCE);
        }
    }

    @Override
    public void populate(IIncomeInfo value) {
        editor.populate(value);
    }

    @Override
    public void setValue(IIncomeInfo value) {
        editor.setValue(value);
    }

    @Override
    public IIncomeInfo getValue() {
        return editor.getValue();
    }

    @Override
    public Collection<? extends CEditableComponent<?, ?>> getComponents() {
        return Arrays.asList(new CPolymorphicEntityEditor<?>[] { editor });
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
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                main.add(inject(proto().name()), 12);
                main.add(inject(proto().employedForYears()), 4);
                AddressUtils.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);

                main.add(inject(proto().starts()), 8.2);
                main.add(inject(proto().ends()), 8.2);
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
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                main.add(inject(proto().name()), 10);
                AddressUtils.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);

                main.add(inject(proto().starts()), 8.2);
                main.add(inject(proto().ends()), 8.2);
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
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                main.add(inject(proto().name()), 10);
                AddressUtils.injectIAddress(main, proto(), this);

                main.add(inject(proto().program()), 10);
                main.add(inject(proto().fieldOfStudy()), 10);
                main.add(inject(proto().fundingChoices()), 10);

                main.add(inject(proto().monthlyAmount()), 10);

                main.add(inject(proto().starts()), 8.2);
                main.add(inject(proto().ends()), 8.2);
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
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                main.add(inject(proto().name()), 10);
                AddressUtils.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);
                main.add(inject(proto().fullyOwned()), 10);
                main.add(inject(proto().monthlyRevenue()), 10);
                main.add(inject(proto().numberOfEmployees()), 4);

                main.add(inject(proto().starts()), 8.2);
                main.add(inject(proto().ends()), 8.2);
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
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                main.add(inject(proto().name()), 10);
                AddressUtils.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);

                main.add(inject(proto().starts()), 8.2);
                main.add(inject(proto().ends()), 8.2);
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
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
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
                return i18n.tr("The start date can not be equal or after end date.");
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
                return i18n.tr("The end date can not be before of equal to start date.");
            }
        });

        comp.get(comp.proto().ends()).addValueChangeHandler(new RevalidationTrigger<LogicalDate>(comp.get(comp.proto().starts())));
    }
}
