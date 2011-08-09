/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lead;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentEditorForm extends CrmEntityForm<Appointment> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public AppointmentEditorForm() {
        super(Appointment.class, new CrmEditorsComponentFactory());
    }

    public AppointmentEditorForm(IEditableComponentFactory factory, IFormView<Appointment> parentView) {
        super(Appointment.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.addDisable(createShowingsTab(), i18n.tr("ShowingsTab"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split;

        main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable()));
        split.getLeftPanel().add(inject(proto().date()), 8);
        split.getLeftPanel().add(inject(proto().time()), 6);
        split.getLeftPanel().add(inject(proto().address()), 20);
        split.getLeftPanel().add(inject(proto().status()), 8);

        split.getRightPanel().add(inject(proto().agent()), 15);
        split.getRightPanel().add(inject(proto().phone()), 15, i18n.tr("Agent Phone"));
        split.getRightPanel().add(inject(proto().email()), 15, i18n.tr("Agent Email"));

        return new CrmScrollPanel(main);
    }

    private Widget createShowingsTab() {
        if (!isEditable()) {
            return new CrmScrollPanel(((AppointmentViewerView) getParentView()).getShowingsListerView().asWidget());
        }
        return new HTML(); // just stub - not necessary for editing mode!.. 
    }
}