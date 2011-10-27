/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.CAddressStructured;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.PropertyPhone;
import com.propertyvista.dto.ComplexDTO;

public class ComplexViewerForm extends CrmEntityForm<ComplexDTO> {
    private static I18n i18n = I18n.get(ComplexViewerForm.class);

    private static final String TAB_CAPTION_DASHBOARD = "Dashboard";

    private static final String TAB_CAPTION_GENERAL = "General";

    private static final String TAB_CAPTION_CONTACTS = "Contacts";

    private static final String TAB_CAPTION_BUILDINGS = "Buildings";

    private final VistaTabLayoutPanel tabPanel;

    public ComplexViewerForm() {
        this(new CrmEditorsComponentFactory());
    }

    public ComplexViewerForm(IEditableComponentFactory factory) {
        super(ComplexDTO.class, factory);
        tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);
    }

    @Override
    public IsWidget createContent() {
        tabPanel.addDisable(isEditable() ? new HTML() : getParentComplexViewerView().getDashboardView(), i18n.tr(TAB_CAPTION_DASHBOARD));
        tabPanel.add(createGeneralPanel(), i18n.tr(TAB_CAPTION_GENERAL));
        tabPanel.add(createContactsPanel(), i18n.tr(TAB_CAPTION_CONTACTS));
        tabPanel.addDisable(isEditable() ? new HTML() : getParentComplexViewerView().getBuildingListerView(), i18n.tr(TAB_CAPTION_BUILDINGS));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createGeneralPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = 0;

        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().name()))).build());
        panel.setWidget(row++, 0, (new DecoratorBuilder(inject(proto().contactInfo().website()))).build());

        panel.setH1(row++, 0, 2, proto().contactInfo().phones().getMeta().getCaption());
        panel.setWidget(row, 0, inject(proto().contactInfo().phones(), new PropertyPhoneFolder()));
        panel.getFlexCellFormatter().setColSpan(row++, 0, 2);

        panel.setH1(row++, 0, 2, proto().address().getMeta().getCaption());
        panel.setWidget(row, 0, inject(proto().address(), new CAddressStructured(true, false)));
        panel.getFlexCellFormatter().setColSpan(row++, 0, 2);
        return new CrmScrollPanel(panel);
    }

    private Widget createContactsPanel() {
        FormFlexPanel panel = new FormFlexPanel();
        panel.setWidget(0, 0, inject(proto().contactInfo().contacts(), new OrganizationContactFolder()));
        return new CrmScrollPanel(panel);
    }

    private ComplexViewerView getParentComplexViewerView() {
        return (ComplexViewerView) getParentView();
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    private class PropertyPhoneFolder extends VistaTableFolder<PropertyPhone> {

        public PropertyPhoneFolder() {
            super(PropertyPhone.class, false);
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "7em"));
            columns.add(new EntityFolderColumnDescriptor(proto().number(), "11em"));
            columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
            columns.add(new EntityFolderColumnDescriptor(proto().designation(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().provider(), "10em"));
            columns.add(new EntityFolderColumnDescriptor(proto().visibility(), "7em"));
            return columns;
        }
    }

}
