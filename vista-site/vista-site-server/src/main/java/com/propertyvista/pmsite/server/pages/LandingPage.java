/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import templates.TemplateResources;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.domain.site.PageMetaTags;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;
import com.propertyvista.pmsite.server.PMSiteClientPreferences;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.CustomGadgetPanel;
import com.propertyvista.pmsite.server.panels.NewsGadgetPanel;
import com.propertyvista.pmsite.server.panels.PromoGadgetPanel;
import com.propertyvista.pmsite.server.panels.QuickSearchGadgetPanel;
import com.propertyvista.pmsite.server.panels.TestimGadgetPanel;
import com.propertyvista.pmsite.server.skins.base.PMSiteTheme;

public class LandingPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public LandingPage() {
        super();
        setStatelessHint(true);
        setVersioned(false);

        // set aptlist view mode preference to Map
        PMSiteClientPreferences.setClientPref("aptListMode", AptListPage.ViewMode.map.name());

        // meta tags
        PageMetaTags meta = getCM().getMetaTags(getAvailableLocale());
        add(new Label(META_TITLE, meta.title().getValue()));
        add(new Label(META_DESCRIPTION).add(AttributeModifier.replace("content", meta.description().getValue())));
        add(new Label(META_KEYWORDS).add(AttributeModifier.replace("content", meta.keywords().getValue())));

        // see if banner image is available
        WebMarkupContainer bannerImg = new WebMarkupContainer("bannerImg");
        SiteImageResource banner = getCM().getSiteBanner(getAvailableLocale());
        if (banner != null) {
            bannerImg.add(AttributeModifier.replace("style", "background-image:url(" + PMSiteContentManager.getSiteImageResourceUrl(banner) + ")"));
        }
        add(bannerImg);

        // gadgets
        ListView<HomePageGadget> narrowPanel = new ListView<HomePageGadget>("narrowBox", getCM().getNarrowAreaGadgets()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<HomePageGadget> item) {
                HomePageGadget gadget = item.getModelObject();
                @SuppressWarnings("unchecked")
                GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
                switch (type) {
                case quickSearch:
                    item.add(new QuickSearchGadgetPanel("narrowBoxContent", gadget));
                    break;
                case news:
                    item.add(new NewsGadgetPanel("narrowBoxContent", gadget));
                    break;
                case custom:
                    item.add(new CustomGadgetPanel("narrowBoxContent", gadget));
                    break;
                }
            }
        };
        add(narrowPanel);

        ListView<HomePageGadget> widePanel = new ListView<HomePageGadget>("wideBox", getCM().getWideAreaGadgets()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<HomePageGadget> item) {
                HomePageGadget gadget = item.getModelObject();
                @SuppressWarnings("unchecked")
                GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
                if (type != null) {
                    switch (type) {
                    case quickSearch:
                        item.add(new QuickSearchGadgetPanel("wideBoxContent", gadget));
                        break;
                    case promo:
                        item.add(new PromoGadgetPanel("wideBoxContent"));
                        break;
                    case testimonials:
                        item.add(new TestimGadgetPanel("wideBoxContent", gadget));
                        break;
                    case custom:
                        item.add(new CustomGadgetPanel("wideBoxContent", gadget));
                        break;
                    }
                } else {
                    item.setVisible(false);
                }
            }
        };
        add(widePanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        if (ApplicationMode.isDevelopment()) {
            try {
                response.renderCSSReference(getCM().getCssManager().getCssReference(PMSiteTheme.Stylesheet.Landing));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "landing.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());

        response.renderCSSReference(refCSS);
        super.renderHead(response);
    }
}
