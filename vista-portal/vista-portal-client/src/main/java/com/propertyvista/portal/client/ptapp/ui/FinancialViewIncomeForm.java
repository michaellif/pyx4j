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
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.components.MoneyEditorForm;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.IEmploymentInfo;
import com.propertyvista.portal.domain.pt.IncomeInfoEmployer;
import com.propertyvista.portal.domain.pt.IncomeInfoOther;
import com.propertyvista.portal.domain.pt.IncomeInfoSeasonallyEmployed;
import com.propertyvista.portal.domain.pt.IncomeInfoSelfEmployed;
import com.propertyvista.portal.domain.pt.IncomeInfoSocialServices;
import com.propertyvista.portal.domain.pt.IncomeInfoStudentIncome;
import com.propertyvista.portal.domain.pt.IncomeSource;
import com.propertyvista.portal.domain.pt.TenantIncome;

import com.pyx4j.entity.client.ui.flex.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class FinancialViewIncomeForm extends CEntityFolderItem<TenantIncome> {

    private final boolean readOnlyMode;

    public FinancialViewIncomeForm(boolean readOnlyMode) {
        super(TenantIncome.class);
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
            main.add(incomeSource, 10, 20);
        } else {
            main.add(inject(proto().incomeSource()), 10, 20);
        }

        main.add(inject(proto().employer(), createEmployerEditor()));
        main.add(inject(proto().seasonallyEmployed(), createSeasonallyEmployedEditor()));
        main.add(inject(proto().selfEmployed(), createSelfEmployedEditor()));
        main.add(inject(proto().studentIncome(), createStudentIncomeEditor()));
        main.add(inject(proto().socialServices(), createSocialServicesEditor()));
        main.add(inject(proto().otherIncomeInfo(), createOtherIncomeInfoEditor()));

        return main;
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        if (readOnlyMode) {
            return new BoxReadOnlyFolderItemDecorator(false);
        } else {
            return new BoxFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover());
        }
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if ((!readOnlyMode) && member.getValueClass().equals(Money.class)) {
            return new MoneyEditorForm();
        } else {
            return super.create(member);
        }
    }

    @Override
    public void populate(TenantIncome value) {
        super.populate(value);
        setVisibility(value.incomeSource().getValue());
    }

    @SuppressWarnings("unchecked")
    private void setVisibility(IncomeSource incomeSource) {
        get(proto().employer()).setVisible(false);
        get(proto().seasonallyEmployed()).setVisible(false);
        get(proto().selfEmployed()).setVisible(false);
        get(proto().studentIncome()).setVisible(false);
        get(proto().socialServices()).setVisible(false);
        get(proto().otherIncomeInfo()).setVisible(false);

        if (incomeSource != null) {
            switch (incomeSource) {
            case fulltime:
            case parttime:
                get(proto().employer()).setVisible(true);
                break;
            case selfemployed:
                get(proto().selfEmployed()).setVisible(true);
                break;
            case seasonallyEmployed:
                get(proto().seasonallyEmployed()).setVisible(true);
                break;
            case socialServices:
                get(proto().socialServices()).setVisible(true);
                break;
            case student:
                get(proto().studentIncome()).setVisible(true);
                break;
            default:
                @SuppressWarnings("rawtypes")
                CEntityEditableComponent comp = (CEntityEditableComponent) get(proto().otherIncomeInfo());
                comp.setVisible(true);
                applyOtherLables(incomeSource, comp);
            }
        }
    }

    private void applyOtherLables(IncomeSource incomeSource, CEntityEditableComponent<IncomeInfoOther> comp) {
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

    private static void injectIEmploymentInfo(VistaDecoratorsFlowPanel main, IEmploymentInfo proto, CEntityEditableComponent<?> parent) {
        main.add(parent.inject(proto.supervisorName()), 20);
        main.add(parent.inject(proto.supervisorPhone()), 15);
        main.add(parent.inject(proto.monthlyAmount()), 8);
        main.add(parent.inject(proto.position()), 20);
    }

    private CEntityEditableComponent<IncomeInfoEmployer> createEmployerEditor() {
        return new CEntityEditableComponent<IncomeInfoEmployer>(IncomeInfoEmployer.class) {

            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 12);
                main.add(inject(proto().employedForYears()), 10, 2);
                BaseEntityForm.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);

                main.add(inject(proto().starts()), 10, 10);
                main.add(inject(proto().ends()), 10, 10);
                return main;
            }

        };
    }

    private CEntityEditableComponent<IncomeInfoSeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityEditableComponent<IncomeInfoSeasonallyEmployed>(IncomeInfoSeasonallyEmployed.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 10);
                BaseEntityForm.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);

                main.add(inject(proto().starts()), 10, 10);
                main.add(inject(proto().ends()), 10, 10);
                return main;
            }
        };
    }

    private CEntityEditableComponent<IncomeInfoStudentIncome> createStudentIncomeEditor() {
        return new CEntityEditableComponent<IncomeInfoStudentIncome>(IncomeInfoStudentIncome.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 10);
                BaseEntityForm.injectIAddress(main, proto(), this);

                main.add(inject(proto().program()), 10, 10);
                main.add(inject(proto().fieldOfStudy()), 10, 10);
                main.add(inject(proto().fundingChoices()), 10, 10);

                main.add(inject(proto().monthlyAmount()), 10, 10);

                main.add(inject(proto().starts()), 10, 10);
                main.add(inject(proto().ends()), 10, 10);
                return main;
            }
        };
    }

    private CEntityEditableComponent<IncomeInfoSelfEmployed> createSelfEmployedEditor() {
        return new CEntityEditableComponent<IncomeInfoSelfEmployed>(IncomeInfoSelfEmployed.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 10);
                BaseEntityForm.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);
                main.add(inject(proto().fullyOwned()), 10, 10);
                main.add(inject(proto().monthlyRevenue()), 10, 10);
                main.add(inject(proto().numberOfEmployees()), 10, 4);

                main.add(inject(proto().starts()), 10, 10);
                main.add(inject(proto().ends()), 10, 10);
                return main;
            }
        };
    }

    private CEntityEditableComponent<IncomeInfoSocialServices> createSocialServicesEditor() {
        return new CEntityEditableComponent<IncomeInfoSocialServices>(IncomeInfoSocialServices.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 10);
                BaseEntityForm.injectIAddress(main, proto(), this);
                injectIEmploymentInfo(main, proto(), this);

                main.add(inject(proto().starts()), 10, 10);
                main.add(inject(proto().ends()), 10, 10);
                return main;
            }
        };
    }

    private CEntityEditableComponent<IncomeInfoOther> createOtherIncomeInfoEditor() {
        return new CEntityEditableComponent<IncomeInfoOther>(IncomeInfoOther.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 10);
                main.add(inject(proto().monthlyAmount()), 10, 10);
                main.add(inject(proto().ends()), 10, 10);
                return main;
            }
        };
    }
}
