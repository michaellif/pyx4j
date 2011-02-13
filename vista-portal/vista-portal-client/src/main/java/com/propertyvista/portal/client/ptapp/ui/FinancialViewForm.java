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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
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
import com.pyx4j.forms.client.ui.decorators.BasicWidgetDecorator;

@Singleton
public class FinancialViewForm extends CEntityForm<PotentialTenantFinancial> {

    public FinancialViewForm() {
        super(PotentialTenantFinancial.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new HTML("<h4>Income Details</h4>"));
        main.add(new BasicWidgetDecorator(create(proto().occupation(), this)));
        main.add(new HTML());

        addEmployerSection("Current Employer", proto().currentEmployer(), this, main);

        addEmployerSection("Previous Employer", proto().currentEmployer(), this, main);

        main.add(new HTML("<p/><h4>Assets</h4>"));
        main.add(create(proto().assets(), this));

        main.add(new HTML("<p/><h4>Income sources</h4>"));
        main.add(create(proto().incomes(), this));

        setWidget(main);
    }

    private void addEmployerSection(String label, Employer employer, FinancialViewForm form, FlowPanel main) {
        main.add(new HTML("<p/><h4>" + label + "</h4>"));
        //TODO: checkbox enabling/disabling the whole section? or list with +/-?
        main.add(new BasicWidgetDecorator(create(employer.name(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(employer.employedForYears(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(employer.address().street1(), this)));
        main.add(new HTML());
        main.add(new BasicWidgetDecorator(create(employer.address().street2(), this)));
        main.add(new HTML());
    }

    @Override
    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        return super.createMemberEditor(member);
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
            protected FolderDecorator createFolderDecorator() {
                return new TableFolderDecorator(columns, SiteImages.INSTANCE.addRow());
            }

            @Override
            protected CEntityFolderItem<TenantAsset> createItem() {
                return createAssetRowEditor(columns);
            }

            private CEntityFolderItem<TenantAsset> createAssetRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantAsset>(TenantAsset.class, columns, FinancialViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow());
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
            protected FolderDecorator createFolderDecorator() {
                return new TableFolderDecorator(columns, SiteImages.INSTANCE.addRow());
            }

            @Override
            protected CEntityFolderItem<TenantIncome> createItem() {
                return createIncomeRowEditor(columns);
            }

            private CEntityFolderItem<TenantIncome> createIncomeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantIncome>(TenantIncome.class, columns, FinancialViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow());
                    }

                };
            }

        };

    }
}
