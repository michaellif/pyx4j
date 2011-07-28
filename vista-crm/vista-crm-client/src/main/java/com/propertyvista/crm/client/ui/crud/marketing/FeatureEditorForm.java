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
package com.propertyvista.crm.client.ui.crud.marketing;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.offering.Feature;

public abstract class FeatureEditorForm<T extends Feature> extends CrmEntityForm<T> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public FeatureEditorForm(Class<T> rootClass) {
        super(rootClass);
    }

    public FeatureEditorForm(Class<T> rootClass, IEditableComponentFactory factory) {
        super(rootClass, factory);
    }

    protected abstract void addMoreTabs(VistaTabLayoutPanel tabPanel);

    @Override
    public IsWidget createContent() {
        tabPanel.add(createGeneralTab(), i18n.tr("General"));

        tabPanel.addDisable(new ScrollPanel(((FeatureView) getParentView()).getConcessionsListerView().asWidget()), i18n.tr("Concessions"));

        addMoreTabs(tabPanel);

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        main.add(inject(proto().description()), 43);

        main.add(split);
        split.getLeftPanel().add(inject(proto().start()), 8.2);
        split.getRightPanel().add(inject(proto().end()), 8.2);

        return new CrmScrollPanel(main);
    }
}