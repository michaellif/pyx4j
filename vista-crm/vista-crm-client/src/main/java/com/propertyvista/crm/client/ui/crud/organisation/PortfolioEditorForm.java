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

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
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
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().name()), 20);
        main.add(inject(proto().description()), 35);

        main.add(new CrmSectionSeparator(i18n.tr("Assigned Buildings:")));
        main.add(inject(proto().buildings(), createBuildingListView()));

        return new CrmScrollPanel(main);
    }

    private CEditableComponent<?, ?> createBuildingListView() {
        return new VistaEntityFolder<AssignedBuilding>(AssignedBuilding.class, i18n.tr("Portfolio"), isEditable()) {
            private final VistaEntityFolder<AssignedBuilding> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().building(), "40em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<AssignedBuilding> createDecorator() {
                return new VistaTableFolderDecorator<AssignedBuilding>(columns(), parent) {
                    {
                        setShowHeader(false);
                    }
                };
            }
        };
    }
}
