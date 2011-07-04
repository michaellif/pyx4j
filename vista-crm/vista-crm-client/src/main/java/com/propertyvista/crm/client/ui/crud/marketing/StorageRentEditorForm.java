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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsSplitFlowPanel;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.CrmEntityForm;
import com.propertyvista.domain.financial.offering.StorageRent;

public class StorageRentEditorForm extends CrmEntityForm<StorageRent> {

    public StorageRentEditorForm(IView<StorageRent> parentView) {
        this(new CrmEditorsComponentFactory(), parentView);
    }

    public StorageRentEditorForm(IEditableComponentFactory factory, IView<StorageRent> parentView) {
        super(StorageRent.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        TabLayoutPanel tabPanel = new TabLayoutPanel(2.7, Unit.EM);
        tabPanel.add(new ScrollPanel(createFinancialTab()), i18n.tr("Financial"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    private Widget createFinancialTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        VistaDecoratorsSplitFlowPanel split = new VistaDecoratorsSplitFlowPanel();

        split.getLeftPanel().add(inject(proto().largeRent()), 7);
        split.getLeftPanel().add(inject(proto().regularRent()), 7);
        split.getRightPanel().add(inject(proto().smallRent()), 7);

        main.add(split);
        return main;
    }
}