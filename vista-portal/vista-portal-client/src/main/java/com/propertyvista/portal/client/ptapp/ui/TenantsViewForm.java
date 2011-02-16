/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author antonk
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.Tenant;
import com.propertyvista.portal.domain.pt.Tenants;

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
import com.pyx4j.forms.client.ui.CComponent;

@Singleton
public class TenantsViewForm extends CEntityForm<Tenants> {

    public TenantsViewForm() {
        super(Tenants.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();
        main.add(create(proto().tenants(), this));
        setWidget(main);
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.equals(proto().tenants())) {
            return createTenantsEditorColumns();
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

    private CEntityFolder<Tenant> createTenantsEditorColumns() {
        return new CEntityFolder<Tenant>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                Tenant proto = EntityFactory.getEntityPrototype(Tenant.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.middleName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.email(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.relationship(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.dependant(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.takeOwnership(), "100px"));
                //TODO: make stuff readonly
            }

            @Override
            protected FolderDecorator createFolderDecorator() {
                return new TableFolderDecorator(columns, SiteImages.INSTANCE.addRow(), "Add a tenant");
            }

            @Override
            protected CEntityFolderItem<Tenant> createItem() {
                return createTenantRowEditor(columns);
            }

            private CEntityFolderItem<Tenant> createTenantRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Tenant>(Tenant.class, columns, TenantsViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove tenant");
                    }

                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        Tenants val = TenantsViewForm.this.getValue();
                        comp.setEnabled(val.tenants().size() > 1);
                        if (column == proto().dependant() || column == proto().takeOwnership()) {
                            // no title for checkboxes
                            comp.setTitle("");
                        }
                        return comp;
                    }

                };
            }

        };

    }
}
