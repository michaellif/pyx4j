/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.CityIntroPage;
import com.propertyvista.domain.site.HtmlContent;
import com.propertyvista.domain.site.PageMetaTags;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
import com.propertyvista.pmsite.server.model.PageParamsUtil;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.model.WicketUtils.PageLink;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaInputPanel;
import com.propertyvista.site.rpc.dto.PropertySearchCriteria;
import com.propertyvista.site.rpc.dto.PropertySearchCriteria.SearchType;

public class CityPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(CityPage.class);

    private City city;

    public CityPage(PageParameters params) {

        String cityProvName = null;
        try {
            cityProvName = params.get(PMSiteApplication.ParamNameCityProv).toString();
            if ((city = findCity(cityProvName)) == null) {
                throw new Exception(i18n.tr("Invalid City-Province code: {0}", cityProvName));
            }
        } catch (Exception e) {
            // redirect to findapt page
            redirectOrFail(FindAptPage.class, e.getMessage());
        }

        PropertySearchCriteria searchCrit = EntityFactory.create(PropertySearchCriteria.class);
        searchCrit.searchType().setValue(SearchType.city);
        searchCrit.city().set(city.name());
        searchCrit.province().set(city.province().name());
        final CompoundIEntityModel<PropertySearchCriteria> model = new CompoundIEntityModel<PropertySearchCriteria>(searchCrit);

        final StatelessForm<IPojo<PropertySearchCriteria>> form = new StatelessForm<IPojo<PropertySearchCriteria>>("advancedSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(AptListPage.class, PageParamsUtil.convertToPageParameters(model.getObject().getEntityValue()));
            }

        };

        form.add(new AdvancedSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit").add(AttributeModifier.replace("value", i18n.tr("Search"))));

        add(form);

        // if asked for the form, no need to render any data
        if (getRequestCycle().getActiveRequestHandler() instanceof ListenerInterfaceRequestHandler) {
            return;
        }

        AvailableLocale curLocale = ((PMSiteWebRequest) getRequest()).getSiteLocale();
        // meta tags
        PageMetaTags meta = ((PMSiteWebRequest) getRequest()).getContentManager().getCityPageMetaTags(curLocale, city);
        add(new Label(META_TITLE, meta.title().getValue()));
        add(new Label(META_DESCRIPTION).add(AttributeModifier.replace("content", meta.description().getValue())));
        add(new Label(META_KEYWORDS).add(AttributeModifier.replace("content", meta.keywords().getValue())));

        add(new Label("caption2", getLocalizedPageTitle()));

        // if page content found, show it
        String html = "";
        EntityQueryCriteria<CityIntroPage> contentCrit = EntityQueryCriteria.create(CityIntroPage.class);
        contentCrit.add(PropertyCriterion.eq(contentCrit.proto().cityName(), preprocess(city.name().getValue())));
        CityIntroPage page = Persistence.service().retrieve(contentCrit);
        if (page != null) {
            for (HtmlContent cont : page.content()) {
                if (curLocale.equals(cont.locale())) {
                    html = cont.html().getValue();
                }
            }
        }
        add(new Label("cityPageContent", html).setEscapeModelStrings(false));

        // add property links title
        add(new Label("cityPropertiesTitle", i18n.tr("Properties in {0}, {1}", city.name().getValue(), city.province().name().getValue())));
        // link panel
        RepeatingView linkPanel = new RepeatingView("propertyEntry");
        add(linkPanel);
        List<Building> searchResult = PropertyFinder.getPropertyList(searchCrit);
        if (searchResult != null) {
            for (Building building : PropertyFinder.getPropertyList(searchCrit)) {
                AddressStructured addr = building.info().address();
                String title = addr.getStringView();
                // No-Street-City-Province-Code
                String ref = SimpleMessageFormat.format("{0} {1} {2} {3} {4}", addr.streetNumber().getValue(), addr.streetName().getValue(),
                        addr.city().getValue(), addr.province().name().getValue(), building.propertyCode().getValue()).replaceAll(" ", "-");
                WebMarkupContainer propEntry = new WebMarkupContainer(linkPanel.newChildId());
                propEntry.add(new PageLink("propertyLink", AptDetailsPage.class, new PageParameters().set(0, ref)).setText(title));
                linkPanel.add(propEntry);
            }
        }
    }

    private City findCity(String cityProvName) {
        String[] cityProvPair = cityProvName.split("-");
        if (cityProvPair.length != 2) {
            return null;
        }
        String cityName = cityProvPair[0];
        String provName = cityProvPair[1];
        EntityQueryCriteria<City> criteria = EntityQueryCriteria.create(City.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().hasProperties(), Boolean.TRUE));
        criteria.add(PropertyCriterion.eq(criteria.proto().province().name(), provName));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().name()));
        City foundCity = null;
        for (City city : Persistence.secureQuery(criteria)) {
            if (cityName.equalsIgnoreCase(preprocess(city.name().getValue()))) {
                foundCity = city;
                break;
            }
        }
        return foundCity;
    }

    private String preprocess(String city) {
        return city.replaceAll("[\\W_]", "");
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Rent Apartments in {0}, {1}", city.name().getValue(), city.province().name().getValue());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "citypage.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
