/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.dto.PortalFooterContentDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class FooterViewImpl extends FlowPanel implements FooterView {

    private static final I18n i18n = I18n.get(FooterViewImpl.class);

    private final BrandPanel brandPanel;

    private final SimplePanel pmcInfoPanel;

    private final FlowPanel actionsPanel;

    public FooterViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.PageFooter.name());
        getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        pmcInfoPanel = new SimplePanel(new HTML("PMC Info"));
        pmcInfoPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        pmcInfoPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        pmcInfoPanel.setWidth("50%");

        actionsPanel = new FlowPanel();
        actionsPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        actionsPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        actionsPanel.setWidth("50%");

        SimplePanel linksPanel = new SimplePanel(new HTML("Terms of Use - Privacy - FAQ"));
        linksPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        linksPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        linksPanel.getElement().getStyle().setProperty("textAlign", "center");
        linksPanel.setWidth("100%");

        SimplePanel langSelectorPanel = new SimplePanel(new HTML("Languages"));
        langSelectorPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        langSelectorPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        langSelectorPanel.getElement().getStyle().setProperty("textAlign", "center");
        langSelectorPanel.setWidth("100%");

        FollowUsPanel followUsPanel = new FollowUsPanel();
        followUsPanel.getElement().getStyle().setFloat(Float.LEFT);
        followUsPanel.setWidth("60%");

        brandPanel = new BrandPanel();
        brandPanel.getElement().getStyle().setFloat(Float.RIGHT);

        actionsPanel.add(followUsPanel);
        actionsPanel.add(brandPanel);

        add(pmcInfoPanel);
        add(actionsPanel);
        add(langSelectorPanel);
        add(linksPanel);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    @Override
    public void setContent(PortalFooterContentDTO content) {
        // TODO Auto-generated method stub       
    }

    private void doLayout(LayoutType layoutType) {
        brandPanel.doLayout(layoutType);
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            pmcInfoPanel.setWidth("100%");
            actionsPanel.setWidth("100%");
            break;
        case tabletLandscape:
        case monitor:
        case huge:
            pmcInfoPanel.setWidth("50%");
            actionsPanel.setWidth("50%");
            break;
        }
    }

    class BrandPanel extends FlowPanel {
        private final Image myCommunityImage;

        private final Image vistaImage;

        public BrandPanel() {

            myCommunityImage = new Image(PortalImages.INSTANCE.myCommunityFooterLogo());
            myCommunityImage.getElement().getStyle().setProperty("margin", "-5px 5px 0 0");
            myCommunityImage.getElement().getStyle().setDisplay(Display.BLOCK);
            add(myCommunityImage);

            vistaImage = new Image(PortalImages.INSTANCE.vistaFooterLogo());
            vistaImage.getElement().getStyle().setDisplay(Display.BLOCK);
            add(vistaImage);

        }

        private void doLayout(LayoutType layoutType) {
            switch (layoutType) {
            case phonePortrait:
            case phoneLandscape:
            case tabletPortrait:
                myCommunityImage.getElement().getStyle().setProperty("margin", "5px auto 5px auto");
                myCommunityImage.getElement().getStyle().setProperty("borderRadius", "4px");
                break;
            case tabletLandscape:
            case monitor:
            case huge:
                myCommunityImage.getElement().getStyle().setProperty("margin", "-5px 5px 0 0");
                myCommunityImage.getElement().getStyle().setProperty("borderRadius", "0");
                break;
            }
        }
    }

    class FollowUsPanel extends SimplePanel {
        public FollowUsPanel() {
            setStyleName(PortalWebRootPaneTheme.StyleName.PageFooterFollowUs.name());

            FlowPanel contentPanel = new FlowPanel();
            contentPanel.setWidth("10em");
            contentPanel.getElement().getStyle().setProperty("margin", "0 auto");
            setWidget(contentPanel);

            contentPanel.add(new HTML(i18n.tr("FOLLOW US")));
            contentPanel.add(new Button(PortalImages.INSTANCE.socialFacebook(), i18n.tr("Facebook"), new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            }));
            contentPanel.add(new Button(PortalImages.INSTANCE.socialTwitter(), i18n.tr("Twitter"), new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            }));
            contentPanel.add(new Button(PortalImages.INSTANCE.socialYouTube(), i18n.tr("YouTube"), new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            }));
            contentPanel.add(new Button(PortalImages.INSTANCE.socialFlickr(), i18n.tr("Flickr"), new Command() {

                @Override
                public void execute() {
                    // TODO Auto-generated method stub

                }
            }));
        }
    }

}
