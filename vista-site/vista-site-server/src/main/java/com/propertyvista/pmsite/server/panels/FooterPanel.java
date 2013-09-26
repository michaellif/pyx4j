/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.SocialLink.SocialSite;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.pages.CityPage;

public class FooterPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(FooterPanel.class);

    public FooterPanel(boolean residentsOnly) {
        super("footer");

        final AvailableLocale siteLocale = ((PMSiteWebRequest) getRequest()).getSiteLocale();
        final PMSiteContentManager cm = ((PMSiteWebRequest) getRequest()).getContentManager();
        WebMarkupContainer footerLocations = new WebMarkupContainer("footer_locations");
        if (residentsOnly) {
            footerLocations.setVisible(false);
        } else {
            List<City> cities = cm.getCities();
            if (cities != null && cities.size() > 0) {
                footerLocations.add(new ListView<City>("footer_locations_city", cities) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<City> item) {
                        City city = item.getModelObject();
                        String _city = city.name().getValue();
                        String _prov = city.province().name().getValue();
                        String _prov2 = city.province().code().getValue();
                        if (_city != null && _prov != null && _prov2 != null) {
                            PageParameters params = new PageParameters();
                            params.set(PMSiteApplication.ParamNameCityProv, preprocess(_city) + "-" + _prov);
                            BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", CityPage.class, params);
                            link.add(new Label("city", _city + " (" + _prov2 + ")"));
                            item.add(link);
                        }
                    }
                });
            } else {
                footerLocations.setVisible(false);
            }
        }
        add(footerLocations);

        WebMarkupContainer footerSocial = new WebMarkupContainer("footer_social");
        final Map<SocialSite, String> socialLinks = cm.getSocialLinks();
        if (socialLinks != null && socialLinks.size() > 0) {
            footerSocial.add(new ListView<SocialSite>("social_link", new ArrayList<SocialSite>(socialLinks.keySet())) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(ListItem<SocialSite> item) {
                    SocialSite site = item.getModelObject();
                    item.add(new ExternalLink("link_url", socialLinks.get(site)).add(new AttributeAppender("class", " " + site.name())));
                }
            });
        } else {
            footerSocial.setVisible(false);
        }
        add(footerSocial);

        add(new ListView<NavigationItem>("footer_link", cm.getFooterNavigItems()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<NavigationItem> item) {
                NavigationItem navItem = item.getModelObject();
                BookmarkablePageLink<?> link = new BookmarkablePageLink<Void>("link", navItem.getDestination(), navItem.getPageParameters());
                link.add(new Label("caption", cm.getCaption(navItem.getPageDescriptor(), siteLocale)));
                item.add(link);
            }
        });

        add(new LocalePanel("locale"));

        Label copy = new Label("footer_copyright", cm.getCopyrightInfo(siteLocale));
        add(copy);

        Label poweredProd = new Label("powered_product", i18n.tr("Property Management Software"));
        add(poweredProd);
        Label poweredBy = new Label("powered_by", i18n.tr("by"));
        add(poweredBy);
        ExternalLink poweredLogo = new ExternalLink("powered_logo", cm.poveredByUrl());
        String title = "Property Management Software by PropertyVista";
        String alt = "PropertyVista Logo";
        poweredLogo.add(AttributeModifier.replace("title", title), AttributeModifier.replace("alt", alt));
        add(poweredLogo);

        Label devTs = new Label("dev_ts");
        if (ApplicationMode.isDevelopment()) {
            devTs.setDefaultModel(Model.of(new Date().toString()));
        } else {
            devTs.setVisible(false);
        }
        add(devTs);
    }

    private String preprocess(String cityName) {
        return cityName.replaceAll("[\\W_]", "");
    }
}