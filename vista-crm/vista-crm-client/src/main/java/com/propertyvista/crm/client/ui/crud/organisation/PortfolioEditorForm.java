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
package com.propertyvista.crm.client.ui.crud.organisation;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
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
            new SelectBuildingBox(getValue()) {
                @Override
                public boolean onClickOk() {
                    for (Building building : getSelectedItems()) {
                        addItem(building);
                    }
                    return true;
                }
            }.show();
        }

    }

    private abstract class SelectBuildingBox extends EntitySelectorDialog<Building> {

        public SelectBuildingBox(List<Building> alreadySelected) {
            super(Building.class, true, alreadySelected, i18n.tr("Select Buildings"));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ColumnDescriptor<Building>> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off                    
                    new MemberColumnDescriptor.Builder(proto().propertyCode()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().complex()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().propertyManager()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().marketing().name()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().info().name()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().info().type()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().city()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().province()).<Building>build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().country()).<Building>build()
            ); //@formatter:on
        }

        @Override
        protected AbstractListService<Building> getSelectService() {
            return GWT.<AbstractListService<Building>> create(SelectBuildingCrudService.class);
        }

        @Override
        protected String width() {
            return "700px";
        }

        @Override
        protected String height() {
            return "400px";
        }

    }
}
