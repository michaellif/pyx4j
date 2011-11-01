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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.company.AssignedBuilding;
import com.propertyvista.domain.company.Portfolio;

public class PortfolioEditorForm extends CrmEntityForm<Portfolio> {

    public PortfolioEditorForm() {
        super(Portfolio.class, new CrmEditorsComponentFactory());
    }

    public PortfolioEditorForm(IEditableComponentFactory factory) {
        super(Portfolio.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 20).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 40).build());

        main.setH1(++row, 0, 1, i18n.tr("Assigned Buildings"));
        main.setWidget(++row, 0, inject(proto().buildings(), createBuildingListView()));

        return new CrmScrollPanel(main);
    }

    private CEditableComponent<?, ?> createBuildingListView() {
        return new VistaTableFolder<AssignedBuilding>(AssignedBuilding.class, isEditable()) {
            private final VistaTableFolder<AssignedBuilding> parent = this;

            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().building(), "60em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<AssignedBuilding> createDecorator() {
                return new VistaTableFolderDecorator<AssignedBuilding>(parent) {
                    {
                        setShowHeader(false);
                    }
                };
            }
        };
    }
}
