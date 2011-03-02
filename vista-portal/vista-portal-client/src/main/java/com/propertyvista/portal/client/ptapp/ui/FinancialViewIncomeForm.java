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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
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

    private final FinancialViewForm parentForm;

    public FinancialViewIncomeForm(final FinancialViewForm parentForm) {
        super(TenantIncome.class);
        this.parentForm = parentForm;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        if (!parentForm.isSummaryViewMode()) {
            @SuppressWarnings("unchecked")
            CComboBox<IncomeSource> incomeSource = (CComboBox<IncomeSource>) inject(proto().incomeSource());
            incomeSource.addValueChangeHandler(new ValueChangeHandler<IncomeSource>() {
                @Override
                public void onValueChange(ValueChangeEvent<IncomeSource> event) {
                    setVisibility(event.getValue());
                }
            });
            main.add(new VistaWidgetDecorator(incomeSource));
        } else {
            main.add(new VistaWidgetDecorator(inject(proto().incomeSource())));
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
        if (parentForm.isSummaryViewMode()) {
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
                FlowPanel main = new FlowPanel();
                main.add(new VistaWidgetDecorator(inject(proto().name()), 10d, 12));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().employedForYears()), 10d, 2));
                main.add(new HTML());
                BaseEntityForm.injectIEmploymentInfo(main, proto(), this);
                return main;
            }

        };
    }

    private CEntityEditableComponent<SeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityEditableComponent<SeasonallyEmployed>(SeasonallyEmployed.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new HTML());
                BaseEntityForm.injectIEmploymentInfo(main, proto(), this);
                main.add(new HTML());
                BaseEntityForm.injectIAddress(main, proto(), this);
                main.add(new HTML());
                return main;
            }
        };
    }

    private CEntityEditableComponent<StudentIncome> createStudentIncomeEditor() {
        return new CEntityEditableComponent<StudentIncome>(StudentIncome.class) {
            @Override
            public IsWidget createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new HTML());
                BaseEntityForm.injectIEmploymentInfo(main, proto(), this);
                main.add(new HTML());
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
                FlowPanel main = new FlowPanel();
                main.add(new VistaWidgetDecorator(inject(proto().companyName()), 10d, 15));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().yearsInBusiness()), 10d, 2));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().fullyOwned()), 10d, 10));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().monthlyRevenue()), 10d, 10));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().monthlySalary()), 10d, 8));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().numberOfEmployees()), 10d, 4));
                main.add(new HTML());
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
                FlowPanel main = new FlowPanel();
                main.add(new VistaWidgetDecorator(inject(proto().agency()), 10d, 15));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().yearsReceiving()), 10d, 15));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().worker()), 10d, 30));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().workerPhone()), 10d, 15));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(inject(proto().monthlyAmount()), 10d, 8));
                main.add(new HTML());
                BaseEntityForm.injectIAddress(main, proto(), this);
                main.add(new HTML());
                return main;
            }
        };
    }
}
