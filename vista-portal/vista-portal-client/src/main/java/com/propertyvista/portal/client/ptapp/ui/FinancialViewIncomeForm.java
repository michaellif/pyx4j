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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.domain.pt.Employer;
import com.propertyvista.portal.domain.pt.IncomeSource;
import com.propertyvista.portal.domain.pt.SeasonallyEmployed;
import com.propertyvista.portal.domain.pt.SelfEmployed;
import com.propertyvista.portal.domain.pt.SocialServices;
import com.propertyvista.portal.domain.pt.StudentIncome;
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

        main.add(inject(proto().employer()));
        main.add(inject(proto().seasonallyEmployed()));
        main.add(inject(proto().selfEmployed()));
        main.add(inject(proto().studentIncome()));
        main.add(inject(proto().socialServices()));

        return main;
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        if (readOnlyMode) {
            return new BoxReadOnlyFolderItemDecorator(false);
        } else {
            return new BoxFolderItemDecorator(SiteImages.INSTANCE.removeRow());
        }
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member.getValueClass().equals(Employer.class)) {
            return createEmployerEditor();
        } else if (member.getValueClass().equals(SelfEmployed.class)) {
            return createSelfEmployedEditor();
        } else if (member.getValueClass().equals(SeasonallyEmployed.class)) {
            return createSeasonallyEmployedEditor();
        } else if (member.getValueClass().equals(SocialServices.class)) {
            return createSocialServicesEditor();
        } else if (member.getValueClass().equals(StudentIncome.class)) {
            return createStudentIncomeEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    public void populate(TenantIncome value) {
        super.populate(value);
        setVisibility(value.incomeSource().getValue());
    }

    private void setVisibility(IncomeSource value) {
        get(proto().employer()).setVisible(false);
        get(proto().seasonallyEmployed()).setVisible(false);
        get(proto().selfEmployed()).setVisible(false);
        get(proto().studentIncome()).setVisible(false);
        get(proto().socialServices()).setVisible(false);

        if (value != null) {
            switch (value) {
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
            }
        }
    }

    private CEntityEditableComponent<Employer> createEmployerEditor() {
        return new CEntityEditableComponent<Employer>(Employer.class) {

            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().name()), 10, 12);
                main.add(inject(proto().employedForYears()), 10, 2);
                BaseEntityForm.injectIEmploymentInfo(main, proto(), this);
                return main;
            }

        };
    }

    private CEntityEditableComponent<SeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityEditableComponent<SeasonallyEmployed>(SeasonallyEmployed.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                BaseEntityForm.injectIEmploymentInfo(main, proto(), this);
                BaseEntityForm.injectIAddress(main, proto(), this);
                return main;
            }
        };
    }

    private CEntityEditableComponent<StudentIncome> createStudentIncomeEditor() {
        return new CEntityEditableComponent<StudentIncome>(StudentIncome.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(new HTML());
                BaseEntityForm.injectIEmploymentInfo(main, proto(), this);
                BaseEntityForm.injectIAddress(main, proto(), this);
                main.add(new HTML());
                return main;
            }
        };
    }

    private CEntityEditableComponent<SelfEmployed> createSelfEmployedEditor() {
        return new CEntityEditableComponent<SelfEmployed>(SelfEmployed.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().companyName()), 10, 15);
                main.add(inject(proto().yearsInBusiness()), 10, 2);
                main.add(inject(proto().fullyOwned()), 10, 10);
                main.add(inject(proto().monthlyRevenue()), 10, 10);
                main.add(inject(proto().monthlySalary()), 10, 8);
                main.add(inject(proto().numberOfEmployees()), 10, 4);
                BaseEntityForm.injectIAddress(main, proto(), this);
                main.add(new HTML());
                return main;
            }
        };
    }

    private CEntityEditableComponent<SocialServices> createSocialServicesEditor() {
        return new CEntityEditableComponent<SocialServices>(SocialServices.class) {
            @Override
            public IsWidget createContent() {
                VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(readOnlyMode);
                main.add(inject(proto().agency()), 10, 15);
                main.add(inject(proto().yearsReceiving()), 10, 15);
                main.add(inject(proto().worker()), 10, 30);
                main.add(inject(proto().workerPhone()), 10, 15);
                main.add(inject(proto().monthlyAmount()), 10, 8);
                BaseEntityForm.injectIAddress(main, proto(), this);
                main.add(new HTML());
                return main;
            }
        };
    }
}
