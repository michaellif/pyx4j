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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.lead.Lead;

public class LeadEditorForm extends CrmEntityForm<Lead> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public LeadEditorForm() {
        super(Lead.class, new CrmEditorsComponentFactory());
    }

    public LeadEditorForm(IEditableComponentFactory factory, IFormView<Lead> parent) {
        super(Lead.class, factory);
        setParentView(parent);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.addDisable(createAppointmentsTab(), i18n.tr("Appointments"));

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
        split.getLeftPanel().add(inject(proto().person().name().firstName()), 15);
        split.getLeftPanel().add(inject(proto().person().name().lastName()), 15);
        split.getLeftPanel().add(inject(proto().person().email()), 20);
        split.getLeftPanel().add(inject(proto().person().homePhone()), 15);
        split.getLeftPanel().add(inject(proto().person().mobilePhone()), 15);
        split.getLeftPanel().add(inject(proto().informedFrom()), 10);

        split.getRightPanel().add(inject(proto().moveInDate()), 8);

        split.getRightPanel().add(inject(proto().rent().min()), 5, i18n.tr("Min rent"));
        split.getRightPanel().add(inject(proto().rent().max()), 5, i18n.tr("Max rent"));

        split.getRightPanel().add(inject(proto().term()), 8);
        split.getRightPanel().add(inject(proto().beds()), 4);
        split.getRightPanel().add(inject(proto().baths()), 4);
        split.getRightPanel().add(inject(proto().floorplan()), 15);

        main.add(inject(proto().comments()), 50);

        main.add(new VistaLineSeparator());

        main.add(split = new VistaDecoratorsSplitFlowPanel(!isEditable()));
        split.getLeftPanel().add(inject(proto().source()), 15);
        split.getLeftPanel().add(inject(proto().assignedTo()), 15);

        split.getRightPanel().add(inject(proto().createDate()), 8.2);
        split.getRightPanel().add(inject(proto().status()), 10);

        return new CrmScrollPanel(main);
    }

    private Widget createAppointmentsTab() {
        if (!isEditable()) {
            return new ScrollPanel(((LeadViewerView) getParentView()).getAppointmentsListerView().asWidget());
        }
        return new HTML(); // just stub - not necessary for editing mode!.. 
    }
}