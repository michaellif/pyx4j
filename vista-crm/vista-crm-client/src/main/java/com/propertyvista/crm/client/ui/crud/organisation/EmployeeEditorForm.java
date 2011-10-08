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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.company.AssignedPortfolio;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.ManagedEmployee;

public class EmployeeEditorForm extends CrmEntityForm<Employee> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public EmployeeEditorForm() {
        super(Employee.class, new CrmEditorsComponentFactory());
    }

    public EmployeeEditorForm(IEditableComponentFactory factory) {
        super(Employee.class, factory);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    public IsWidget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(inject(proto().title()), 20);

        main.add(new HTML("&nbsp"));

        main.add(inject(proto().name().namePrefix()), 6);
        main.add(inject(proto().name().firstName()), 15);
        main.add(inject(proto().name().middleName()), 15);
        main.add(inject(proto().name().lastName()), 15);
        main.add(inject(proto().name().maidenName()), 15);
        main.add(inject(proto().name().nameSuffix()), 6);
        main.add(inject(proto().birthDate()), 8.2);

        main.add(inject(proto().homePhone()), 10);
        main.add(inject(proto().mobilePhone()), 10);
        main.add(inject(proto().workPhone()), 10);
        main.add(inject(proto().email()), 25);

        main.add(new HTML("&nbsp"));

        main.add(inject(proto().description()), 50);

        return new CrmScrollPanel(main);
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    public IsWidget createDetailsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());

        main.add(new CrmSectionSeparator(i18n.tr("Assigned Portfolios:")));
        main.add(inject(proto().portfolios(), createPortfolioListView()));

        main.add(new CrmSectionSeparator(i18n.tr("Managed Employees:")));
        main.add(inject(proto().employees(), createEmpoloyeeListView()));

        return new CrmScrollPanel(main);
    }

    private CEditableComponent<?, ?> createPortfolioListView() {
        return new VistaEntityFolder<AssignedPortfolio>(AssignedPortfolio.class, i18n.tr("Portfolio"), isEditable()) {
            private final VistaEntityFolder<AssignedPortfolio> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().portfolio(), "40em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<AssignedPortfolio> createDecorator() {
                return new VistaTableFolderDecorator<AssignedPortfolio>(columns(), parent) {
                    {
                        setShowHeader(false);
                    }
                };
            }

        };
    }

    private CEditableComponent<?, ?> createEmpoloyeeListView() {
        return new VistaEntityFolder<ManagedEmployee>(ManagedEmployee.class, i18n.tr("Employee"), isEditable()) {
            private final VistaEntityFolder<ManagedEmployee> parent = this;

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().employee(), "40em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<ManagedEmployee> createDecorator() {
                return new VistaTableFolderDecorator<ManagedEmployee>(columns(), parent) {
                    {
                        setShowHeader(false);
                    }
                };
            }
        };
    }
}
