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
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceEditorForm extends CrmEntityForm<Service> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public ServiceEditorForm(IFormView<Service> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public ServiceEditorForm(IEditableComponentFactory factory, IFormView<Service> parentView) {
        super(Service.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createEligibilityTab(), i18n.tr("Eligibility"));

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

    public IsWidget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel(!isEditable());

        main.add(split);
        split.getLeftPanel().add(inject(proto().type(), new CLabel()), 10);
        split.getLeftPanel().add(inject(proto().name()), 10);

        split.getRightPanel().add(inject(proto().depositType()), 15);

        main.add(inject(proto().description()), 50);

        main.add(new CrmSectionSeparator(i18n.tr("Items:")));
        main.add(inject(proto().items(), new ServiceItemFolder(this)));

        return new CrmScrollPanel(main);
    }

    public IsWidget createEligibilityTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(new CrmSectionSeparator(i18n.tr("Features:")));
        main.add(inject(proto().features(), new ServiceFeatureFolder(isEditable() ? ((ServiceEditorView) getParentView()).getFeatureListerView() : null)));

        main.add(new CrmSectionSeparator(i18n.tr("Concessions:")));
        main.add(inject(proto().concessions(), new ServiceConcessionFolder(isEditable() ? ((ServiceEditorView) getParentView()).getConcessionListerView()
                : null)));

        return new CrmScrollPanel(main);
    }
}