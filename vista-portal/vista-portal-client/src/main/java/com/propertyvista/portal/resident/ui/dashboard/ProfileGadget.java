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
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.shared.Dimension;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.ImageViewport;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.portal.resident.resources.PortalImages;
import com.propertyvista.portal.resident.themes.DashboardTheme;
import com.propertyvista.portal.resident.ui.AbstractGadget;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.ResidentSummaryDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class ProfileGadget extends AbstractGadget<MainDashboardViewImpl> {

    private final PersonInfoPanel personInfoPanel;

    private final AddressPanel addressPanel;

    ProfileGadget(MainDashboardViewImpl form) {
        super(form, ThemeColor.contrast2, 1);
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());

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

        setContent(contentPanel);

        setNavigationBar(new NavigationBar());

    }

    class NavigationBar extends FlowPanel {
        public NavigationBar() {
            if (!VistaFeatures.instance().yardiIntegration()) {

                Anchor anchor = new Anchor("View my Profile", new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Profile());
                    }
                });
                add(anchor);

                anchor = new Anchor("View my Account", new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Account());
                    }
                });
                add(anchor);

                anchor = new Anchor("Change my Password", new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(new PortalSiteMap.PasswordChange());
                    }
                });
                add(anchor);
            }
        }
    }

    class PersonInfoPanel extends SimplePanel {

        private final HTML nameLabel;

        private final ImageViewport picture;

        public PersonInfoPanel() {
            FlowPanel contentPanel = new FlowPanel();
            setWidget(contentPanel);

            picture = new ImageViewport(new Dimension(60, 80), ScaleMode.Cover);
            picture.addStyleName(DashboardTheme.StyleName.PersonPhoto.name());
            picture.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            picture.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            contentPanel.add(picture);

            nameLabel = new HTML();
            nameLabel.setStyleName(DashboardTheme.StyleName.PersonName.name());
            nameLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            nameLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

            contentPanel.add(nameLabel);
        }

        void setValue(ResidentSummaryDTO value) {
            nameLabel.setHTML(value.tenantName().getValue());
            Image img = value.picture().isNull() ? new Image(VistaImages.INSTANCE.profilePicture()) : new Image(MediaUtils.createCustomerPictureUrl(value
                    .picture()));
            picture.setImage(img);
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

            HTML separator = new HTML();
            separator.getElement().getStyle().setProperty("marginRight", "10px");
            contentPanel.setWidget(++row, ++col, separator);
            contentPanel.getFlexCellFormatter().setHeight(row, col, "100%");
            contentPanel.getFlexCellFormatter().setStyleName(row, col, DashboardTheme.StyleName.GadgetBlockSeparator.name());
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);

            Image marker = new Image(PortalImages.INSTANCE.marker());
            marker.getElement().getStyle().setProperty("marginRight", "10px");
            contentPanel.setWidget(row, ++col, marker);
            contentPanel.getFlexCellFormatter().setRowSpan(row, col, 2);
            contentPanel.getFlexCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);

            floorplanLabel = new HTML();
            floorplanLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            contentPanel.setWidget(row, ++col, floorplanLabel);

            col = 0;
            addressLabel = new HTML();
            contentPanel.setWidget(++row, col, addressLabel);

        }

        void setValue(ResidentSummaryDTO value) {
            floorplanLabel.setHTML(value.floorplanName().getValue());
            addressLabel.setHTML(value.tenantAddress().getValue());
        }
    }

    protected void populate(ResidentSummaryDTO value) {
        personInfoPanel.setValue(value);
        addressPanel.setValue(value);
    }

}
