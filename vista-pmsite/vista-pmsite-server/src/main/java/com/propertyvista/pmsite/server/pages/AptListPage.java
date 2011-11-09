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
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters.NamedPair;

import templates.TemplateResources;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.pmsite.server.PMSiteClientPreferences;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.PageParamsUtil;
import com.propertyvista.pmsite.server.model.StylesheetTemplateModel;
import com.propertyvista.pmsite.server.model.WicketUtils.AttributeClassModifier;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaInputPanel;
import com.propertyvista.pmsite.server.panels.AptListPanel;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.AmenityType;

public class AptListPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AptListPage.class);

    public enum ViewMode {
        map, list;

        @Override
        public String toString() {
            return i18n.tr(this == map ? "List View" : "Map View");
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
        List<String> amenities = argsE.get("amenities");
        if (amenities != null) {
            for (int i = 0; i < amenities.size(); i++) {
                criteria.amenities().add(AmenityType.valueOf(amenities.get(i)));
            }
        }
        if (criteria.searchType().getValue() == null) {
            criteria.searchType().setValue(PropertySearchCriteria.SearchType.city);
        }
        IPojo<PropertySearchCriteria> pojo = ServerEntityFactory.getPojo(criteria);
        final CompoundPropertyModel<IPojo<PropertySearchCriteria>> model = new CompoundPropertyModel<IPojo<PropertySearchCriteria>>(pojo);

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

        // check view mode - Map/List
        if (PMSiteClientPreferences.getClientPref("aptListMode") == null) {
            PMSiteClientPreferences.setClientPref("aptListMode", ViewMode.map.name());
        }
        ViewMode viewMode = ViewMode.map;
        try {
            viewMode = ViewMode.valueOf(PMSiteClientPreferences.getClientPref("aptListMode"));
        } catch (Exception ignore) {
            // do nothing
        }
        add(new Label("aptListModeSwitch", viewMode.toString()).add(new AttributeClassModifier(null, "aptListMode_" + viewMode.name())));
        add(new AptListPanel("aptListPanel", new CompoundPropertyModel<List<Building>>(PMSiteContentManager.getPropertyList(criteria)), viewMode));
        String jsAptListModeInfo = "\n" + "var aptListModeInfo = {";
        for (ViewMode mode : ViewMode.values()) {
            int nextId = (mode.ordinal() + 1) % ViewMode.values().length;
            ViewMode nextMode = ViewMode.values()[nextId];
            jsAptListModeInfo += "\n" + mode.name() + ": {text: '" + mode.toString() + "', cls: 'aptListMode_" + mode.name() + "', show: '.listing_"
                    + mode.name() + "view', next: '" + nextMode.name() + "'}";
            if (nextId > 0) {
                jsAptListModeInfo += ",";
            }
        }
        jsAptListModeInfo += "\n}\n";
        add(new Label("jsAptListModeInfo", jsAptListModeInfo).setEscapeModelStrings(false));

    }

    @Override
    public String getLocalizedPageTitle() {
        return i18n.tr("Property List");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "aptlist" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);

    }
}
