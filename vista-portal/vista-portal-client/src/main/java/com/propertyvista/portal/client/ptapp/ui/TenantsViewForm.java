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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;

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
public class TenantsViewForm extends CEntityForm<PotentialTenantList> {

    public TenantsViewForm() {
        super(PotentialTenantList.class);
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

    private CEntityFolder<PotentialTenantInfo> createTenantsEditorColumns() {
        return new CEntityFolder<PotentialTenantInfo>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                PotentialTenantInfo proto = EntityFactory.getEntityPrototype(PotentialTenantInfo.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto.middleName(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto.email(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto.relationship(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto.dependant(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto.takeOwnership(), "7em"));
            }

            @Override
            protected FolderDecorator<PotentialTenantInfo> createFolderDecorator() {
                return new TableFolderDecorator<PotentialTenantInfo>(columns, SiteImages.INSTANCE.addRow(), "Add a person");
            }

            @Override
            protected CEntityFolderItem<PotentialTenantInfo> createItem() {
                return createTenantRowEditor(columns);
            }

            private CEntityFolderItem<PotentialTenantInfo> createTenantRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<PotentialTenantInfo>(PotentialTenantInfo.class, columns, TenantsViewForm.this) {
                    PotentialTenantInfo proto = EntityFactory.getEntityPrototype(PotentialTenantInfo.class);

                    @Override
                    public void createContent() {
                        if (!isFirst()) {
                            super.createContent();
                        } else {
                            FlowPanel main = new FlowPanel();
                            main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                            main.setWidth("100%");
                            for (EntityFolderColumnDescriptor column : columns) {
                                // Don't show dependant and takeOwnership 
                                if (column.getObject() == proto.dependant() || column.getObject() == proto.takeOwnership()) {
                                    continue;
                                }

                                CComponent<?> component = createCell(column);
                                component.setWidth("100%");
                                if (column.getObject() == proto.relationship() || column.getObject() == proto.email()) {
                                    component.setEnabled(false);
                                }
                                main.add(createDecorator(component, column.getWidth()));
                            }
                            setWidget(main);
                        }
                    }

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        if (isFirst()) {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.hideRemoveRow(), null, false);
                        } else {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove person", true);
                        }
                    }

                };
            }

        };

    }
}
