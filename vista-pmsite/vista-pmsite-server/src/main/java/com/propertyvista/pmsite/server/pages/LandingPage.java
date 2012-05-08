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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import templates.TemplateResources;

import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;
import com.propertyvista.pmsite.server.PMSiteClientPreferences;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.CustomModulePanel;
import com.propertyvista.pmsite.server.panels.NewsPanel;
import com.propertyvista.pmsite.server.panels.PromoPanel;
import com.propertyvista.pmsite.server.panels.QuickSearchCriteriaPanel;
import com.propertyvista.pmsite.server.panels.TestimPanel;

public class LandingPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public LandingPage() {
        super();
        setStatelessHint(true);
        setVersioned(false);

        // set aptlist view mode preference to Map
        PMSiteClientPreferences.setClientPref("aptListMode", AptListPage.ViewMode.map.name());

        WebMarkupContainer bannerImg = new WebMarkupContainer("bannerImg");
        bannerImg.add(new QuickSearchCriteriaPanel());
        // see if banner image is available
        PMSiteContentManager cm = ((PMSiteWebRequest) getRequest()).getContentManager();
        AvailableLocale locale = ((PMSiteWebRequest) getRequest()).getSiteLocale();
        SiteImageResource banner = cm.getSiteBanner(locale);
        if (banner != null) {
            bannerImg.add(AttributeModifier.replace("style", "background-image:url(" + PMSiteContentManager.getSiteImageResourceUrl(banner) + ")"));
        }
        add(bannerImg);
        ListView<HomePageGadget> narrowPanel = new ListView<HomePageGadget>("narrowBox", cm.getNarrowAreaGadgets()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<HomePageGadget> item) {
                HomePageGadget gadget = item.getModelObject();
                @SuppressWarnings("unchecked")
                GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
                switch (type) {
                case news:
                    item.add(new NewsPanel("narrowBoxContent"));
                    break;
                case custom:
                    item.add(new CustomModulePanel("narrowBoxContent", gadget));
                    break;
                }
            }
        };
        add(narrowPanel);

        ListView<HomePageGadget> widePanel = new ListView<HomePageGadget>("wideBox", cm.getWideAreaGadgets()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<HomePageGadget> item) {
                HomePageGadget gadget = item.getModelObject();
                @SuppressWarnings("unchecked")
                GadgetType type = GadgetType.getGadgetType((Class<? extends GadgetContent>) gadget.content().getInstanceValueClass());
                if (type != null) {
                    switch (type) {
                    case promo:
                        item.add(new PromoPanel("wideBoxContent"));
                        break;
                    case testimonials:
                        item.add(new TestimPanel("wideBoxContent"));
                        break;
                    case custom:
                        item.add(new CustomModulePanel("wideBoxContent", gadget));
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
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "landing.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());

        response.renderCSSReference(refCSS);

        super.renderHead(response);
    }
}
