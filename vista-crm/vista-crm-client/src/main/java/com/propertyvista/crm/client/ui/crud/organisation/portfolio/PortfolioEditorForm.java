/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation.portfolio;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public class PortfolioEditorForm extends CrmEntityForm<Portfolio> {

    private static final I18n i18n = I18n.get(PortfolioEditorForm.class);

    public PortfolioEditorForm() {
        this(false);
    }

    public PortfolioEditorForm(boolean viewMode) {
        super(Portfolio.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 40).build());

        main.setH1(++row, 0, 1, i18n.tr("Assigned Buildings"));
        main.setWidget(++row, 0, inject(proto().buildings(), new BuildingFolder()));

        return new CrmScrollPanel(main);
    }

    private class BuildingFolder extends VistaTableFolder<Building> {

        public BuildingFolder() {
            super(Building.class, PortfolioEditorForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().propertyCode(), "5em"),
                    new EntityFolderColumnDescriptor(proto().info().name(), "10em"),                    
                    new EntityFolderColumnDescriptor(proto().info().type(), "20em")
            );//@formatter:on
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Building) {
                return new CEntityFolderRowEditor<Building>(Building.class, columns()) {
                    {
                        setViewable(true);
                    }
                };
            } else {
                return super.create(member);
            }
        }

        @Override
        protected IFolderDecorator<Building> createDecorator() {
            return new VistaTableFolderDecorator<Building>(this, this.isEditable()) {
                {
                    setShowHeader(false);
                }
            };
        }

        @Override
        protected void addItem() {
            new BuildingSelectorDialog(true, getValue()) {
                @Override
                public boolean onClickOk() {
                    for (Building selected : getSelectedItems()) {
                        addItem(selected);
                    }
                    return true;
                }
            }.show();
        }

    }
}
