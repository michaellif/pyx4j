/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.dashboard;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.dto.TenantProfileDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class ProfileGadget extends CEntityViewer<TenantProfileDTO> {

    private PersonInfoPanel personInfoPanel;

    private AddressPanel addressPanel;

    ProfileGadget() {
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
    }

    @Override
    public IsWidget createContent(TenantProfileDTO value) {

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.setStyleName(DashboardTheme.StyleName.GadgetContent.name());

        personInfoPanel = new PersonInfoPanel(value);
        personInfoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        personInfoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        personInfoPanel.setWidth("50%");

        addressPanel = new AddressPanel(value);
        addressPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        addressPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        addressPanel.setWidth("50%");

        contentPanel.add(personInfoPanel);
        contentPanel.add(addressPanel);

        SimplePanel container = new SimplePanel(contentPanel);
        container.setStyleName(DashboardTheme.StyleName.GadgetContainer.name());
        return container;
    }

    public void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            personInfoPanel.setWidth("100%");
            addressPanel.setWidth("100%");
            break;

        default:
            personInfoPanel.setWidth("50%");
            addressPanel.setWidth("50%");
            break;
        }

    }

    class PersonInfoPanel extends SimplePanel {
        public PersonInfoPanel(TenantProfileDTO value) {
            FlowPanel contentPanel = new FlowPanel();
            setWidget(contentPanel);

            contentPanel.setStyleName(DashboardTheme.StyleName.GadgetBlock.name());

            Image image = new Image(PortalImages.INSTANCE.avatar3());
            image.setStyleName(DashboardTheme.StyleName.PersonPhoto.name());
            image.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            contentPanel.add(image);

            HTML nameLabel = new HTML(value.tenantName().getValue());
            nameLabel.setStyleName(DashboardTheme.StyleName.PersonName.name());
            nameLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            nameLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

            contentPanel.add(nameLabel);
        }
    }

    class AddressPanel extends SimplePanel {
        public AddressPanel(TenantProfileDTO value) {
            FlexTable contentPanel = new FlexTable();
            contentPanel.setStyleName(DashboardTheme.StyleName.GadgetBlock.name());
            setWidget(contentPanel);

            int row = -1;
            int col = -1;

            contentPanel.setWidget(++row, ++col, new Image(PortalImages.INSTANCE.marker()));
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);
            contentPanel.getFlexCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);

            contentPanel.setWidget(row, ++col, new HTML(value.floorplanName().getValue()));

            FlowPanel actionsPanel = new FlowPanel();
            actionsPanel.add(new Anchor("Edit my Profile"));
            actionsPanel.add(new Anchor("Edit Settings"));
            actionsPanel.add(new Anchor("Change Password"));

            contentPanel.setWidget(row, ++col, actionsPanel);
            contentPanel.getFlexCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);

            col = 0;
            contentPanel.setWidget(++row, col, new HTML(value.tenantAddress().getValue()));

        }
    }

}
