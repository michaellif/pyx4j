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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.domain.company.AssignedPortfolio;
import com.propertyvista.domain.company.ManagedEmployee;

public class EmployeeEditorForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public EmployeeEditorForm() {
        super(EmployeeDTO.class, new CrmEditorsComponentFactory());
    }

    public EmployeeEditorForm(IEditableComponentFactory factory) {
        super(EmployeeDTO.class, factory);
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
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().title()), 20).build());

        main.setBR(++row, 0, 1);

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().lastName()), 20).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().maidenName()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name(), new CEntityLabel()), 25).customLabel(i18n.tr("Employee")).build());
            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            get(proto().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().birthDate()), 9).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().workPhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 25).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 50).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().enabled()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().behaviors()), 20).build());

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
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Assigned Portfolios"));
        main.setWidget(++row, 0, inject(proto().portfolios(), createPortfolioListView()));

        main.setH1(++row, 0, 2, i18n.tr("Managed Employees"));
        main.setWidget(++row, 0, inject(proto().employees(), createEmpoloyeeListView()));

        return new CrmScrollPanel(main);
    }

    private CComponent<?, ?> createPortfolioListView() {
        return new VistaTableFolder<AssignedPortfolio>(AssignedPortfolio.class, isEditable()) {

            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().portfolio(), "40em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<AssignedPortfolio> createDecorator() {
                return new VistaTableFolderDecorator<AssignedPortfolio>(this, this.isEditable()) {
                    {
                        setShowHeader(false);
                    }
                };
            }

        };
    }

    private CComponent<?, ?> createEmpoloyeeListView() {
        return new VistaTableFolder<ManagedEmployee>(ManagedEmployee.class, isEditable()) {

            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().employee(), "40em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<ManagedEmployee> createDecorator() {
                return new VistaTableFolderDecorator<ManagedEmployee>(this, this.isEditable()) {
                    {
                        setShowHeader(false);
                    }
                };
            }
        };
    }
}
