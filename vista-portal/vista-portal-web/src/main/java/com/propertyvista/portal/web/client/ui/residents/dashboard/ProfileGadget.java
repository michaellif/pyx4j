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
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.dto.TenantProfileDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class ProfileGadget extends AbstractGadget<TenantProfileDTO> {

    private PersonInfoPanel personInfoPanel;

    private AddressPanel addressPanel;

    ProfileGadget() {
        super(ThemeColor.contrast2);
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
    }

    @Override
    public IsWidget createContent() {

        FlowPanel contentPanel = new FlowPanel();

        personInfoPanel = new PersonInfoPanel();
        personInfoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        personInfoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        personInfoPanel.setWidth("50%");

        addressPanel = new AddressPanel();
        addressPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        addressPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        addressPanel.setWidth("50%");

        contentPanel.add(personInfoPanel);
        contentPanel.add(addressPanel);

        return contentPanel;
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

        private final HTML nameLabel;

        public PersonInfoPanel() {
            FlowPanel contentPanel = new FlowPanel();
            setWidget(contentPanel);

            Image image = new Image(PortalImages.INSTANCE.avatar3());
            image.setStyleName(DashboardTheme.StyleName.PersonPhoto.name());
            image.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            contentPanel.add(image);

            nameLabel = new HTML();
            nameLabel.setStyleName(DashboardTheme.StyleName.PersonName.name());
            nameLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            nameLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

            contentPanel.add(nameLabel);
        }

        void setValue(TenantProfileDTO value) {
            nameLabel.setHTML(value.tenantName().getValue());
        }
    }

    class AddressPanel extends SimplePanel {

        private final HTML floorplanLabel;

        private final HTML addressLabel;

        public AddressPanel() {
            FlexTable contentPanel = new FlexTable();
            setWidget(contentPanel);

            int row = -1;
            int col = -1;

            FlowPanel actionsPanel = new FlowPanel();
            actionsPanel.getElement().getStyle().setProperty("marginRight", "10px");
            actionsPanel.add(new Anchor("Edit my Profile"));
            actionsPanel.add(new Anchor("Edit Settings"));
            actionsPanel.add(new Anchor("Change Password"));

            contentPanel.setWidget(++row, ++col, actionsPanel);
            contentPanel.getFlexCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);

            HTML separator = new HTML();
            separator.getElement().getStyle().setProperty("marginRight", "10px");
            contentPanel.setWidget(row, ++col, separator);
            contentPanel.getFlexCellFormatter().setHeight(row, col, "100%");
            contentPanel.getFlexCellFormatter().setStyleName(row, col, DashboardTheme.StyleName.GadgetBlockSeparator.name());
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);

            contentPanel.setWidget(row, ++col, new Image(PortalImages.INSTANCE.marker()));
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);
            contentPanel.getFlexCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);

            floorplanLabel = new HTML();
            floorplanLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            contentPanel.setWidget(row, ++col, floorplanLabel);

            col = 0;
            addressLabel = new HTML();
            contentPanel.setWidget(++row, col, addressLabel);

        }

        void setValue(TenantProfileDTO value) {
            floorplanLabel.setHTML(value.floorplanName().getValue());
            addressLabel.setHTML(value.tenantAddress().getValue());
        }
    }

    @Override
    protected void setComponentsValue(TenantProfileDTO value, boolean fireEvent, boolean populate) {
        personInfoPanel.setValue(value);
        addressPanel.setValue(value);
    }

}
