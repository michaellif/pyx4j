/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeForm;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingFolder extends VistaTableFolder<Building> {

    private EmployeeForm employeeForm;

    public BuildingFolder(boolean modifiable) {
        super(Building.class, modifiable);
    }

    public BuildingFolder(EmployeeForm employeeForm) {
        this(employeeForm.isEditable());
        this.employeeForm = employeeForm;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().propertyCode(), "10em"),
                new EntityFolderColumnDescriptor(proto().info().name(), "20em"),
                new EntityFolderColumnDescriptor(proto().info().type(), "20em")
        );//@formatter:on
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Building) {
            return new CEntityFolderRowEditor<Building>(Building.class, columns()) {
                @Override
                protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                    CLabel<?> comp = inject(column.getObject(), new CLabel<String>());

                    if (proto().propertyCode() == column.getObject()) {
                        comp.setNavigationCommand(new Command() {
                            @Override
                            public void execute() {
                                AppSite.getPlaceController()
                                        .goTo(AppPlaceEntityMapper.resolvePlace(Building.class).formViewerPlace(getValue().getPrimaryKey()));
                            }
                        });
                    }

                    return comp;
                }
            };
        }
        return super.create(member);
    }

    @Override
    protected IFolderDecorator<Building> createFolderDecorator() {
        return new VistaTableFolderDecorator<Building>(this, this.isEditable());
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(employeeForm.getParentView(), getValue()) {
            @Override
            public void onClickOk() {
                for (Building selected : getSelectedItems()) {
                    addItem(selected);
                }
            }

            @Override
            protected void setFilters(List<Criterion> filters) {
                super.setFilters(filters);

                if (employeeForm != null) {
                    if (employeeForm.isRestrictAccessSet()) {
                        List<Key> buildingAccessKeys = new ArrayList<Key>();

                        List<Building> buildingAccess = employeeForm.getBuildingAccess();
                        if (buildingAccess != null && !buildingAccess.isEmpty()) {
                            for (Building entity : buildingAccess) {
                                buildingAccessKeys.add(entity.getPrimaryKey());
                            }
                        }

                        List<Portfolio> portfolioAccess = employeeForm.getPortfolioAccess();
                        if (portfolioAccess != null && !portfolioAccess.isEmpty()) {
                            for (Portfolio portfolio : portfolioAccess) {
                                for (Building building : portfolio.buildings()) {
                                    buildingAccessKeys.add(building.getPrimaryKey());
                                }
                            }
                        }

                        if (!buildingAccessKeys.isEmpty()) {
                            addFilter(PropertyCriterion.in(EntityFactory.getEntityPrototype(Building.class).id(), buildingAccessKeys));
                        } else {
                            addFilter(PropertyCriterion.isNull(EntityFactory.getEntityPrototype(Building.class).id()));
                        }
                    }
                }
            }
        }.show();
    }
}