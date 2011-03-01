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
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator.DecorationData;

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
import com.pyx4j.forms.client.ui.CComponent;

public class FinancialViewIncomeForm extends CEntityFolderItem<TenantIncome> {

    private final BaseEntityForm<?> parentForm;

    private FlowPanel employer;

    private FlowPanel selfEmployed;

    private FlowPanel seasonallyEmployed;

    private FlowPanel socialservices;

    private FlowPanel student;

    public FinancialViewIncomeForm(final BaseEntityForm<?> parentForm) {
        super(TenantIncome.class);
        this.parentForm = parentForm;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();

        @SuppressWarnings("unchecked")
        CComboBox<IncomeSource> incomeSource = (CComboBox<IncomeSource>) create(proto().incomeSource(), this);
        incomeSource.addValueChangeHandler(new ValueChangeHandler<IncomeSource>() {
            @Override
            public void onValueChange(ValueChangeEvent<IncomeSource> event) {
                setVisibility(event.getValue());
            }
        });

        main.add(new VistaWidgetDecorator(incomeSource));

        main.add(employer = createEmployerPanel());
        main.add(seasonallyEmployed = createSeasonallyEmployedPanel());
        main.add(selfEmployed = createSelfemployedPanel());
        main.add(student = createStudentPanel());
        main.add(socialservices = createSocialPanel());

        return main;
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        return new BoxFolderItemDecorator(SiteImages.INSTANCE.removeRow());
    }

    private CComponent<?> create(IObject<?> member, CEntityEditableComponent<?> parent) {
        CEntityEditableComponent<?> comp = null;

        if (member.getValueClass().equals(Employer.class)) {
            comp = createEmployerEditor();
        } else if (member.getValueClass().equals(SelfEmployed.class)) {
            comp = createSelfEmployedEditor();
        } else if (member.getValueClass().equals(SeasonallyEmployed.class)) {
            comp = createSeasonallyEmployedEditor();
        } else if (member.getValueClass().equals(SocialServices.class)) {
            comp = createSocialServicesEditor();
        } else if (member.getValueClass().equals(StudentIncome.class)) {
            comp = createStudentIncomeEditor();
        }

        if (comp != null) {
            parent.bind(comp, member);
            return comp;
        } else {
            return parentForm.create(member, parent);
        }
    }

    @Override
    public void populate(TenantIncome value) {
        super.populate(value);
        setVisibility(value.incomeSource().getValue());
    }

    private void setVisibility(IncomeSource value) {
        employer.setVisible(false);
        seasonallyEmployed.setVisible(false);
        selfEmployed.setVisible(false);
        student.setVisible(false);
        socialservices.setVisible(false);
        if (value != null) {
            switch (value) {
            case fulltime:
            case parttime:
                employer.setVisible(true);
                break;
            case selfemployed:
                selfEmployed.setVisible(true);
                break;
            case seasonallyEmployed:
                seasonallyEmployed.setVisible(true);
                break;
            case socialServices:
                socialservices.setVisible(true);
                break;
            case student:
                student.setVisible(true);
                break;

            }
        }
    }

    private FlowPanel createEmployerPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Employer</h6>"));
        panel.add(create(proto().employer(), this));
        panel.add(new HTML());
        return panel;
    }

    private FlowPanel createSelfemployedPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Self employed</h6>"));
        panel.add(create(proto().selfEmployed(), this));
        panel.add(new HTML());
        return panel;
    }

    private FlowPanel createSeasonallyEmployedPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Seasonally Employed</h6>"));
        panel.add(create(proto().seasonallyEmployed(), this));
        panel.add(new HTML());
        return panel;
    }

    private FlowPanel createSocialPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Social Income</h6>"));
        panel.add(create(proto().socialServices(), this));
        panel.add(new HTML());
        return panel;
    }

    private FlowPanel createStudentPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Student Information</h6>"));
        panel.add(create(proto().studentIncome(), this));
        panel.add(new HTML());
        return panel;
    }

    private CEntityEditableComponent<Employer> createEmployerEditor() {
        return new CEntityEditableComponent<Employer>(Employer.class) {

            @Override
            public IsWidget createContent() {
                FlowPanel main = new FlowPanel();
                DecorationData decorData = new DecorationData();
                decorData.componentWidth = 12;
                main.add(new VistaWidgetDecorator(create(proto().name(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 2;
                main.add(new VistaWidgetDecorator(create(proto().employedForYears(), this), decorData));
                main.add(new HTML());
                parentForm.createIEmploymentInfo(main, proto(), this);
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
                parentForm.createIEmploymentInfo(main, proto(), this);
                main.add(new HTML());
                parentForm.createIAddress(main, proto(), this);
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
                parentForm.createIEmploymentInfo(main, proto(), this);
                main.add(new HTML());
                parentForm.createIAddress(main, proto(), this);
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
                DecorationData decorData = new DecorationData();
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().companyName(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 2;
                main.add(new VistaWidgetDecorator(create(proto().yearsInBusiness(), this), decorData));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().fullyOwned(), this)));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 10;
                main.add(new VistaWidgetDecorator(create(proto().monthlyRevenue(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 8;
                main.add(new VistaWidgetDecorator(create(proto().monthlySalary(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 4;
                main.add(new VistaWidgetDecorator(create(proto().numberOfEmployees(), this), decorData));
                main.add(new HTML());
                parentForm.createIAddress(main, proto(), this);
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
                DecorationData decorData = new DecorationData();
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().agency(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().yearsReceiving(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 30;
                main.add(new VistaWidgetDecorator(create(proto().worker(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 15;
                main.add(new VistaWidgetDecorator(create(proto().workerPhone(), this), decorData));
                main.add(new HTML());
                decorData = new DecorationData();
                decorData.componentWidth = 8;
                main.add(new VistaWidgetDecorator(create(proto().monthlyAmount(), this), decorData));
                main.add(new HTML());
                parentForm.createIAddress(main, proto(), this);
                main.add(new HTML());
                return main;
            }
        };
    }
}
