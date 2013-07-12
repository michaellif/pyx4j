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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.PopupWindow.PopupWindowHandle;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.domain.site.SocialLink;
import com.propertyvista.portal.rpc.portal.dto.PortalFooterContentDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class FooterViewImpl extends FlowPanel implements FooterView {

    private static final I18n i18n = I18n.get(FooterViewImpl.class);

    private final BrandPanel brandPanel;

    private final PmcInfoPanel pmcInfoPanel;

    private final FlowPanel actionsPanel;

    private final FollowUsPanel followUsPanel;

    public FooterViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.PageFooter.name());
        getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        pmcInfoPanel = new PmcInfoPanel();
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

        SimplePanel langSelectorPanel = new SimplePanel(new HTML("Eng(US) - Eng(Ca) - Fr(Ca)"));
        langSelectorPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        langSelectorPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
        langSelectorPanel.getElement().getStyle().setProperty("textAlign", "center");
        langSelectorPanel.setWidth("100%");

        followUsPanel = new FollowUsPanel();
        followUsPanel.setVisible(false);
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
        pmcInfoPanel.setPmcInfo(content.content().html().getValue());
        pmcInfoPanel.setPmcLogo(MediaUtils.createSiteImageResourceUrl(content.logo()));

        followUsPanel.setSocialLinks(content.socialLinks());

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

        private final Image deviceImage;

        public BrandPanel() {

            myCommunityImage = new Image(PortalImages.INSTANCE.myCommunityFooterLogo());
            myCommunityImage.getElement().getStyle().setProperty("margin", "-5px 5px 0 0");
            myCommunityImage.getElement().getStyle().setDisplay(Display.BLOCK);
            add(myCommunityImage);

            vistaImage = new Image(PortalImages.INSTANCE.vistaFooterLogo());
            vistaImage.getElement().getStyle().setDisplay(Display.BLOCK);
            add(vistaImage);

            deviceImage = new Image();
            add(deviceImage);

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

            switch (layoutType) {
            case phonePortrait:
                deviceImage.setResource(PortalImages.INSTANCE.phone());
                break;
            case phoneLandscape:
                deviceImage.setResource(PortalImages.INSTANCE.phoneL());
                break;
            case tabletPortrait:
                deviceImage.setResource(PortalImages.INSTANCE.tablet());
                break;
            case tabletLandscape:
                deviceImage.setResource(PortalImages.INSTANCE.tabletL());
                break;
            case monitor:
                deviceImage.setResource(PortalImages.INSTANCE.monitor());
                break;
            case huge:
                deviceImage.setResource(PortalImages.INSTANCE.huge());
                break;
            }
            deviceImage.getElement().getStyle().setDisplay(Display.BLOCK);
            deviceImage.getElement().getStyle().setProperty("margin", "5px 5px 5px auto");

        }
    }

    class FollowUsPanel extends SimplePanel {

        private final FlowPanel contentPanel;

        public FollowUsPanel() {
            setStyleName(PortalWebRootPaneTheme.StyleName.PageFooterFollowUs.name());

            contentPanel = new FlowPanel();
            contentPanel.setWidth("10em");
            contentPanel.getElement().getStyle().setProperty("margin", "0 auto");
            setWidget(contentPanel);

        }

        void setSocialLinks(IList<SocialLink> links) {
            if (links.size() > 0) {
                followUsPanel.setVisible(true);
                contentPanel.add(new HTML(i18n.tr("FOLLOW US")));
            }
            for (final SocialLink socialLink : links) {

                ImageResource resource = null;
                switch (socialLink.socialSite().getValue()) {
                case Facebook:
                    resource = PortalImages.INSTANCE.socialFacebook();
                    break;
                case Twitter:
                    resource = PortalImages.INSTANCE.socialTwitter();
                    break;
                case Youtube:
                    resource = PortalImages.INSTANCE.socialYouTube();
                    break;
                case Flickr:
                    resource = PortalImages.INSTANCE.socialFlickr();
                    break;

                }

                contentPanel.add(new Button(resource, socialLink.socialSite().getStringView(), new Command() {

                    @Override
                    public void execute() {
                        openLink(socialLink.siteUrl().getValue());
                    }
                }));
            }

        }

        public native PopupWindowHandle openLink(String url) /*-{
			return $wnd.open(url);
        }-*/;
    }

    class PmcInfoPanel extends SimplePanel {

        private final HTML pmcTextPanel;

        private final Image pmcLogoImage;

        public PmcInfoPanel() {
            setStyleName(PortalWebRootPaneTheme.StyleName.PageFooterPmcInfo.name());

            FlowPanel contentPanel = new FlowPanel();
            contentPanel.setStyleName(PortalWebRootPaneTheme.StyleName.PageFooterPmcInfoContent.name());
            contentPanel.getElement().getStyle().setProperty("margin", "0 auto");
            setWidget(contentPanel);

            pmcLogoImage = new Image();
            pmcLogoImage.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            pmcLogoImage.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            contentPanel.add(pmcLogoImage);

            pmcTextPanel = new HTML();
            pmcTextPanel.setStyleName(PortalWebRootPaneTheme.StyleName.PageFooterPmcInfoText.name());
            pmcTextPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            pmcTextPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            contentPanel.add(pmcTextPanel);
        }

        public void setPmcLogo(String url) {
            pmcLogoImage.setUrl(url);

        }

        void setPmcInfo(String content) {
            pmcTextPanel.setHTML(content);
        }
    }

}
