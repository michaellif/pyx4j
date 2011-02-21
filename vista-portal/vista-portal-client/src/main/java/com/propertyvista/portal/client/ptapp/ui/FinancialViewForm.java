/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.portal.domain.pt.Employer;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial.EmploymentTypes;
import com.propertyvista.portal.domain.pt.SeasonallyEmployed;
import com.propertyvista.portal.domain.pt.SelfEmployed;
import com.propertyvista.portal.domain.pt.SocialServices;
import com.propertyvista.portal.domain.pt.StudentIncome;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;

@Singleton
public class FinancialViewForm extends BaseEntityForm<PotentialTenantFinancial> {

    private FlowPanel previousEmployerPanel;

    private FlowPanel fulltime;

    private FlowPanel parttime;

    private FlowPanel selfemployed;

    private FlowPanel socialservices;

    private FlowPanel student;

    protected CEditableComponent<?, ?> currentEmployedForYears;

    public FinancialViewForm() {
        super(PotentialTenantFinancial.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new ViewHeaderDecorator(new HTML("<h4>Income Details</h4>")));
        @SuppressWarnings("unchecked")
        CComboBox<EmploymentTypes> occupation = (CComboBox<EmploymentTypes>) create(proto().occupation(), this);
        occupation.addValueChangeHandler(new ValueChangeHandler<EmploymentTypes>() {
            @Override
            public void onValueChange(ValueChangeEvent<EmploymentTypes> event) {
                setVisibility(event.getValue());
            }
        });
        main.add(new VistaWidgetDecorator(occupation));
        main.add(new HTML());

        main.add(fulltime = createFulltimePanel());
        main.add(parttime = createParttimePanel());
        main.add(selfemployed = createSelfemployedPanel());
        main.add(student = createStudentPanel());
        main.add(socialservices = createSocialPanel());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Assets</h4>")));
        main.add(create(proto().assets(), this));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Income sources</h4>")));
        main.add(create(proto().incomes(), this));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Guarantors</h4>")));
        main.add(create(proto().guarantors(), this));
        main.add(new HTML());

        setWidget(main);
    }

    protected void setVisibility(EmploymentTypes value) {
        fulltime.setVisible(false);
        parttime.setVisible(false);
        selfemployed.setVisible(false);
        student.setVisible(false);
        socialservices.setVisible(false);
        switch (value) {
        case fulltime:
            fulltime.setVisible(true);
            break;

        case parttime:
        case seasonallyEmployed:
            parttime.setVisible(true);
            break;

        case student:
            student.setVisible(true);
            break;

        case selfemployed:
            selfemployed.setVisible(true);
            break;

        case socialServices:
            socialservices.setVisible(true);
            break;
        }
    }

    protected FlowPanel createFulltimePanel() {
        FlowPanel fulltime = new FlowPanel();
        fulltime.add(new HTML("<h6>Current Employer</h6>"));
        fulltime.add(create(proto().currentEmployer(), this));
        fulltime.add(new HTML());

        previousEmployerPanel = new FlowPanel();
        previousEmployerPanel.setVisible(false);
        fulltime.add(previousEmployerPanel);
        previousEmployerPanel.add(new HTML("<h6>Previous Employer</h6>"));
        previousEmployerPanel.add(create(proto().previousEmployer(), this));
        previousEmployerPanel.add(new HTML());
        return fulltime;
    }

    protected FlowPanel createSelfemployedPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Self employed</h6>"));
        panel.add(create(proto().selfEmployed(), this));
        panel.add(new HTML());
        return panel;
    }

    protected FlowPanel createParttimePanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Current Employer</h6>"));
        panel.add(create(proto().seasonallyEmployed(), this));
        panel.add(new HTML());
        return panel;
    }

    protected FlowPanel createSocialPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Social Income</h6>"));
        panel.add(create(proto().socialServices(), this));
        panel.add(new HTML());
        return panel;
    }

    protected FlowPanel createStudentPanel() {
        FlowPanel panel = new FlowPanel();
        panel.add(new HTML("<h6>Student Information</h6>"));
        panel.add(create(proto().studentIncome(), this));
        panel.add(new HTML());
        return panel;
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.equals(proto().assets())) {
            return createAssetFolderEditorColumns();
        } else if (member.equals(proto().incomes())) {
            return createIncomeFolderEditorColumns();
        } else if (member.equals(proto().guarantors())) {
            return createGuarantorFolderEditorColumns();
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        if (member.getValueClass().equals(Employer.class)) {
            return createEmployerEditor();
        }
        if (member.getValueClass().equals(SelfEmployed.class)) {
            return createSelfEmployedEditor(proto().selfEmployed());
        }
        if (member.getValueClass().equals(SeasonallyEmployed.class)) {
            return createSeasonallyEmployedEditor();
        }
        if (member.getValueClass().equals(SocialServices.class)) {
            return createSocialServicesEditor();
        }
        if (member.getValueClass().equals(StudentIncome.class)) {
            return createStudentIncomeEditor();
        }
        return super.createMemberEditor(member);
    }

    private CEntityEditableComponent<Employer> createEmployerEditor() {
        return new CEntityEditableComponent<Employer>(Employer.class) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new VistaWidgetDecorator(create(proto().name(), this)));
                main.add(new HTML());
                currentEmployedForYears = (CEditableComponent<?, ?>) create(proto().employedForYears(), this);
                currentEmployedForYears.addValueChangeHandler(new ValueChangeHandler() {
                    @Override
                    public void onValueChange(ValueChangeEvent event) {
                        previousEmployerPanel.setVisible(((Integer) event.getValue()) < 2);
                    }
                });
                main.add(new VistaWidgetDecorator(currentEmployedForYears));
                main.add(new HTML());
                createIEmploymentInfo(main, proto());
                setWidget(main);
            }

        };
    }

    private CEntityEditableComponent<SeasonallyEmployed> createSeasonallyEmployedEditor() {
        return new CEntityEditableComponent<SeasonallyEmployed>(SeasonallyEmployed.class) {
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new HTML());
                createIEmploymentInfo(main, proto());
                main.add(new HTML());
                createIAddress(main, proto());
                main.add(new HTML());
                setWidget(main);
            }
        };
    }

    private CEntityEditableComponent<StudentIncome> createStudentIncomeEditor() {
        return new CEntityEditableComponent<StudentIncome>(StudentIncome.class) {
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new HTML());
                createIEmploymentInfo(main, proto());
                main.add(new HTML());
                createIAddress(main, proto());
                main.add(new HTML());
                setWidget(main);
            }
        };
    }

    private CEntityEditableComponent<SelfEmployed> createSelfEmployedEditor(final SelfEmployed proto) {
        return new CEntityEditableComponent<SelfEmployed>(SelfEmployed.class) {
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new VistaWidgetDecorator(create(proto.companyName(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto.yearsInBusiness(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto.fullyOwned(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto.monthlyRevenue(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto.monthlySalary(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto.numberOfEmployees(), this)));
                main.add(new HTML());
                createIAddress(main, proto());
                main.add(new HTML());
                setWidget(main);
            }
        };
    }

    private CEntityEditableComponent<SocialServices> createSocialServicesEditor() {
        return new CEntityEditableComponent<SocialServices>(SocialServices.class) {
            @Override
            public void createContent() {
                FlowPanel main = new FlowPanel();
                main.add(new VistaWidgetDecorator(create(proto().agency(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().yearsReceiving(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().worker(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().workerPhone(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().monthlyAmount(), this)));
                main.add(new HTML());
                createIAddress(main, proto());
                main.add(new HTML());
                setWidget(main);
            }
        };
    }

    private CEntityFolder<TenantAsset> createAssetFolderEditorColumns() {
        return new CEntityFolder<TenantAsset>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                TenantAsset proto = EntityFactory.getEntityPrototype(TenantAsset.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.assetType(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.percent(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.assetValue(), "120px"));
            }

            @Override
            protected FolderDecorator<TenantAsset> createFolderDecorator() {
                return new TableFolderDecorator<TenantAsset>(columns, SiteImages.INSTANCE.addRow(), "Add an asset");
            }

            @Override
            protected CEntityFolderItem<TenantAsset> createItem() {
                return createAssetRowEditor(columns);
            }

            private CEntityFolderItem<TenantAsset> createAssetRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantAsset>(TenantAsset.class, columns, FinancialViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove asset");
                    }

                };
            }

        };

    }

    private CEntityFolder<TenantIncome> createIncomeFolderEditorColumns() {
        return new CEntityFolder<TenantIncome>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                TenantIncome proto = EntityFactory.getEntityPrototype(TenantIncome.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.type(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.monthlyAmount(), "120px"));
            }

            @Override
            protected FolderDecorator<TenantIncome> createFolderDecorator() {
                return new TableFolderDecorator<TenantIncome>(columns, SiteImages.INSTANCE.addRow(), "Add an income source");
            }

            @Override
            protected CEntityFolderItem<TenantIncome> createItem() {
                return createIncomeRowEditor(columns);
            }

            private CEntityFolderItem<TenantIncome> createIncomeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantIncome>(TenantIncome.class, columns, FinancialViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove income source");
                    }

                };
            }

        };

    }

    private CEntityFolder<TenantGuarantor> createGuarantorFolderEditorColumns() {
        return new CEntityFolder<TenantGuarantor>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                TenantGuarantor proto = EntityFactory.getEntityPrototype(TenantGuarantor.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.relationship(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.middleName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.email(), "100px"));
            }

            @Override
            protected FolderDecorator<TenantGuarantor> createFolderDecorator() {
                return new TableFolderDecorator<TenantGuarantor>(columns, SiteImages.INSTANCE.addRow(), "Add guarantor");
            }

            @Override
            protected CEntityFolderItem<TenantGuarantor> createItem() {
                return createGuarantorRowEditor(columns);
            }

            private CEntityFolderItem<TenantGuarantor> createGuarantorRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantGuarantor>(TenantGuarantor.class, columns, FinancialViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove guarantor");
                    }

                };
            }

        };

    }

    @Override
    public void populate(PotentialTenantFinancial value) {
        super.populate(value);
        setVisibility(value.occupation().getValue());
    }
}
