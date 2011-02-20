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
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantIncome;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;

@Singleton
public class FinancialViewForm extends CEntityForm<PotentialTenantFinancial> {

    private FlowPanel previousPanel;

    protected CEditableComponent<?, ?> currentEmployedForYears;

    public FinancialViewForm() {
        super(PotentialTenantFinancial.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new ViewHeaderDecorator(new HTML("<h4>Income Details</h4>")));
        main.add(new VistaWidgetDecorator(create(proto().occupation(), this)));
        main.add(new HTML());

        main.add(new HTML("<h6>Current Employer</h6>"));
        main.add(create(proto().currentEmployer(), this));
        main.add(new HTML());

        previousPanel = new FlowPanel();
        previousPanel.setVisible(false);
        main.add(previousPanel);
        previousPanel.add(new HTML("<h6>Previous Employer</h6>"));
        previousPanel.add(create(proto().previousEmployer(), this));
        previousPanel.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Assets</h4>")));
        main.add(create(proto().assets(), this));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(new HTML("<h4>Income sources</h4>")));
        main.add(create(proto().incomes(), this));
        main.add(new HTML());

        setWidget(main);
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.equals(proto().assets())) {
            return createAssetFolderEditorColumns();
        } else if (member.equals(proto().incomes())) {
            return createIncomeFolderEditorColumns();
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        if (member.getValueClass().equals(Employer.class)) {
            return createEmployerEditor();
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
                        previousPanel.setVisible(((Integer) event.getValue()) < 2);
                    }
                });
                main.add(new VistaWidgetDecorator(currentEmployedForYears));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().address().street1(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().address().street2(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().address().city(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().address().province(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().supervisorName(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().supervisorPhone(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().monthlySalary(), this)));
                main.add(new HTML());
                main.add(new VistaWidgetDecorator(create(proto().position(), this)));
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
                columns.add(new EntityFolderColumnDescriptor(proto.name(), "120px"));
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
                columns.add(new EntityFolderColumnDescriptor(proto.description(), "120px"));
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
}
