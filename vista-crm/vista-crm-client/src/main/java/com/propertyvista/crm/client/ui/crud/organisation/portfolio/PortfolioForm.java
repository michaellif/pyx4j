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

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;

public class PortfolioForm extends CrmEntityForm<Portfolio> {

    private static final I18n i18n = I18n.get(PortfolioForm.class);

    public PortfolioForm(IForm<Portfolio> view) {
        super(Portfolio.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 40).build());

        content.setH1(++row, 0, 1, i18n.tr("Assigned Buildings"));
        content.setWidget(++row, 0, inject(proto().buildings(), new BuildingFolder()));

        selectTab(addTab(content));

    }

    private class BuildingFolder extends VistaTableFolder<Building> {

        public BuildingFolder() {
            super(Building.class, PortfolioForm.this.isEditable());
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

                    @Override
                    protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                        if (proto().propertyCode() == column.getObject()) {
                            return inject(proto().propertyCode(), new CHyperlink<String>(null, new Command() {
                                @Override
                                public void execute() {
                                    AppSite.getPlaceController().goTo(
                                            AppPlaceEntityMapper.resolvePlace(Building.class).formViewerPlace(getValue().getPrimaryKey()));
                                }
                            }));
                        }
                        return super.createCell(column);
                    }
                };
            } else {
                return super.create(member);
            }
        }

        @Override
        protected IFolderDecorator<Building> createFolderDecorator() {
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
