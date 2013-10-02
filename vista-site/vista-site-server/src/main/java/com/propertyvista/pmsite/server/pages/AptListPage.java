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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters.NamedPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import templates.TemplateResources;

import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.essentials.rpc.SystemState;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteClientPreferences;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
import com.propertyvista.pmsite.server.model.PageParamsUtil;
import com.propertyvista.pmsite.server.model.WicketUtils.AttributeClassModifier;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaInputPanel;
import com.propertyvista.pmsite.server.panels.AptListPanel;
import com.propertyvista.site.rpc.dto.PropertySearchCriteria;

public class AptListPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(AptListPage.class);

    private static final I18n i18n = I18n.get(AptListPage.class);

    public static final String pageTitle = "Property List";

    @com.pyx4j.i18n.annotations.I18n
    public enum ViewMode {

        @Translate("Map View")
        map,

        @Translate("List View")
        list;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public AptListPage(PageParameters params) {
        super(params);

        List<NamedPair> namedPairs = params.getAllNamed();

        Map<String, List<String>> argsE = new HashMap<String, List<String>>();
        for (NamedPair namedPair : namedPairs) {
            if (!argsE.containsKey(namedPair.getKey())) {
                argsE.put(namedPair.getKey(), new ArrayList<String>());
            }
            argsE.get(namedPair.getKey()).add(namedPair.getValue());
        }

        PropertySearchCriteria criteria = EntityArgsConverter.createFromArgs(PropertySearchCriteria.class, argsE);
        // GeoPoint will not convert from string, so we will do it explicitly
        // TODO Nedds to be handled on the IEntity level
        if (argsE.get("geolocation") != null) {
            criteria.geolocation().setValue(GeoPoint.valueOf(argsE.get("geolocation").get(0)));
        }
        List<String> amenities = argsE.get("amenities");
        if (amenities != null) {
            for (int i = 0; i < amenities.size(); i++) {
                criteria.amenities().add(BuildingAmenity.Type.valueOf(amenities.get(i)));
            }
        }
        if (criteria.searchType().getValue() == null) {
            criteria.searchType().setValue(PropertySearchCriteria.SearchType.city);
        }
        List<Building> searchResult = PropertyFinder.getPropertyList(criteria);
        PMSiteContentManager cm = ((PMSiteWebRequest) getRequest()).getContentManager();

        // if AptList is not enabled redirect to AptDetails
        if (!cm.isAptListEnabled()) {
            // This mode is intended for single building PMCs, so we only show the first available building
            String propCode = "";
            if (searchResult != null && searchResult.size() > 0) {
                propCode = searchResult.get(0).propertyCode().getValue();
            }
            log.debug("redirect to page: {}", AptDetailsPage.class);
            throw new RestartResponseException(AptDetailsPage.class, new PageParameters().set(PMSiteApplication.ParamNameBuilding, propCode));
        }

        final CompoundIEntityModel<PropertySearchCriteria> model = new CompoundIEntityModel<PropertySearchCriteria>(criteria);

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

        // if asked for the form - no need to render any data
        if (getRequestCycle().getActiveRequestHandler() instanceof ListenerInterfaceRequestHandler) {
            return;
        }

        ViewMode viewMode = ViewMode.map;
        if (searchResult != null && searchResult.size() > 0) {
            if (cm.isMapEnabled() && SystemMaintenance.getExternalConnectionsState().equals(SystemState.Online)) {
                // check view mode - Map/List
                if (PMSiteClientPreferences.getClientPref("aptListMode") == null) {
                    PMSiteClientPreferences.setClientPref("aptListMode", ViewMode.map.name());
                }
                try {
                    viewMode = ViewMode.valueOf(PMSiteClientPreferences.getClientPref("aptListMode"));
                } catch (Exception ignore) {
                    // do nothing
                }
                // set switch label
                int nextId = (viewMode.ordinal() + 1) % ViewMode.values().length;
                ViewMode nextMode = ViewMode.values()[nextId];
                add(new Label("aptListModeSwitch", nextMode.toString()).add(new AttributeClassModifier(null, "aptListMode_" + nextMode.name())));
                add(new AptListPanel("aptListPanel", new CompoundPropertyModel<List<Building>>(searchResult), viewMode));
                String jsAptListModeInfo = "\n" + "var aptListModeInfo = {";
                for (ViewMode mode : ViewMode.values()) {
                    nextId = (mode.ordinal() + 1) % ViewMode.values().length;
                    nextMode = ViewMode.values()[nextId];
                    // switch icon must display the next mode it switches to, but must activate the current mode view
                    jsAptListModeInfo += "\n" + mode.name() + ": {text: '" + nextMode.toString() + "', cls: 'aptListMode_" + nextMode.name()
                            + "', show: '.listing_" + mode.name() + "view', next: '" + nextMode.name() + "'}";
                    if (nextId > 0) {
                        jsAptListModeInfo += ",";
                    }
                }
                jsAptListModeInfo += "\n}\n";
                add(new Label("jsAptListModeInfo", jsAptListModeInfo).setEscapeModelStrings(false));
            } else {
                viewMode = ViewMode.list;
                add(new Label("aptListModeSwitch").setVisible(false));
                add(new AptListPanel("aptListPanel", new CompoundPropertyModel<List<Building>>(searchResult), viewMode));
                add(new Label("jsAptListModeInfo").setVisible(false));
            }
        } else {
            add(new Label("aptListModeSwitch").setVisible(false));
            add(new Label("aptListPanel", i18n.tr("Sorry, no matches found. Please refine your search criteria and try again.")).add(AttributeModifier.replace(
                    "class", "notFoundNote")));
            add(new Label("jsAptListModeInfo").setVisible(false));
        }
        // js method to return aptDetails url
        CharSequence url = getRequestCycle().urlFor(AptDetailsPage.class, new PageParameters().add(PMSiteApplication.ParamNameBuilding, ""));
        String jsAptDetailsUrl = "\n" + "function getAptDetailsUrl() { return '" + url.toString() + "'; }\n";
        add(new Label("jsAptDetailsUrl", jsAptDetailsUrl).setEscapeModelStrings(false));
    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr(pageTitle);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "aptlist.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
