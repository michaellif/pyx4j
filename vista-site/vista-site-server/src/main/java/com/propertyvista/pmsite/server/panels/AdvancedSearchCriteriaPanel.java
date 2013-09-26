/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 31, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.PageParamsUtil;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.server.portal.PropertyFinder;

public class AdvancedSearchCriteriaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaPanel.class);

    public AdvancedSearchCriteriaPanel() {
        super("advancedSearchCriteriaPanel");

        PropertySearchCriteria criteria = EntityFactory.create(PropertySearchCriteria.class);
        if (criteria.searchType().isNull()) {
            criteria.searchType().setValue(PropertySearchCriteria.SearchType.city);
        }
        final CompoundIEntityModel<PropertySearchCriteria> model = new CompoundIEntityModel<PropertySearchCriteria>(criteria);

        final StatelessForm<IPojo<PropertySearchCriteria>> form = new StatelessForm<IPojo<PropertySearchCriteria>>("advancedSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                PropertySearchCriteria criteria = model.getObject().getEntityValue();
                // see if we get geolocation parameter
                PageParameters pp = getPage().getPageParameters();
                String gl = pp.get("geolocation").toString();
                if (gl != null && gl.length() > 0) {
                    String[] glArr = gl.split(":");
                    try {
                        criteria.geolocation().setValue(new GeoPoint(Double.parseDouble(glArr[0]), Double.parseDouble(glArr[1])));
                    } catch (NumberFormatException ignore) {
                        // ignore
                    }
                }
                PMSiteContentManager cm = ((PMSiteWebRequest) getRequest()).getContentManager();
                if (cm.isAptListEnabled()) {
                    setResponsePage(AptListPage.class, PageParamsUtil.convertToPageParameters(criteria));
                } else {
                    // This mode is intended for single building PMCs, so we only show the first available building
                    String propCode = "";
                    List<Building> searchResult = PropertyFinder.getPropertyList(criteria);
                    if (searchResult != null && searchResult.size() > 0) {
                        propCode = searchResult.get(0).propertyCode().getValue();
                    }
                    setResponsePage(AptDetailsPage.class, new PageParameters().set(PMSiteApplication.ParamNameBuilding, propCode));
                }
            }

        };

        form.add(new AdvancedSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit").add(AttributeModifier.replace("value", i18n.tr("Search"))));

        add(form);

    }
}
