/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.Unit;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.Vehicle;

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
import com.pyx4j.widgets.client.Button;

public class ApartmentViewForm extends CEntityForm<UnitSelection> {

    public ApartmentViewForm() {
        super(UnitSelection.class);
    }

    @Override
    public void createContent() {
        FlowPanel main = new FlowPanel();

        main.add(new HTML("<h4>Available Units</h4>"));
        main.add(new BasicWidgetDecorator(create(proto().availableFrom(), this), 40, 100));
        main.add(new BasicWidgetDecorator(create(proto().availableTo(), this), 40, 100));
        main.add(new Button("Change"));
        main.add(new HTML());
        main.add(create(proto().availableUnits().units(), this));

        setWidget(main);
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        if (member.equals(proto().availableUnits().units())) {
            return createUnitsFolder();
        } else {
            return super.createMemberFolderEditor(member);
        }
    }

    private CEntityFolder<Unit> createUnitsFolder() {
        return new CEntityFolder<Unit>() {

            private List<EntityFolderColumnDescriptor> columns;

            {
                Unit proto = EntityFactory.getEntityPrototype(Unit.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.marketingName(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.requiredDeposit(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.bedrooms(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.bathrooms(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.area(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.avalableForRent(), "100px"));
            }

            @Override
            protected FolderDecorator<Unit> createFolderDecorator() {
                return new TableFolderDecorator<Unit>(columns, SiteImages.INSTANCE.addRow());
            }

            @Override
            protected CEntityFolderItem<Unit> createItem() {
                return createEmployeeRowEditor(columns);
            }

            private CEntityFolderItem<Unit> createEmployeeRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<Unit>(Unit.class, columns, ApartmentViewForm.this) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove vehicle");
                    }

                };
            }

        };

    }
}
