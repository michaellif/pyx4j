/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
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
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.BedroomChoice;
import com.propertyvista.portal.server.portal.PropertyFinder;

//http://www.google.com/codesearch#o92Uy7_Jjpw/base/openqrm-3.5.2/src/base/java/main/code/com/qlusters/qrm/web/wicket/markup/&type=cs
public class QuickSearchCriteriaPanel extends Panel {

    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(QuickSearchCriteriaPanel.class);

    public QuickSearchCriteriaPanel(String id) {
        super(id);

        PropertySearchCriteria criteria = EntityFactory.create(PropertySearchCriteria.class);

        final CompoundIEntityModel<PropertySearchCriteria> model = new CompoundIEntityModel<PropertySearchCriteria>(criteria);

        final StatelessForm<IPojo<PropertySearchCriteria>> form = new StatelessForm<IPojo<PropertySearchCriteria>>("quickSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                model.getObject().getEntityValue().searchType().setValue(PropertySearchCriteria.SearchType.city);
                executeSearch(model.getObject());
            }
        };

        form.add(new FeedbackPanel("form_messages"));
        form.add(new QuickSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit").add(AttributeModifier.replace("value", i18n.tr("Search"))));

        add(form);
    }

    private void executeSearch(IPojo<PropertySearchCriteria> model) {
        PropertySearchCriteria criteria = model.getEntityValue();
        if (!criteria.priceRange().isNull()) {
            criteria.minPrice().setValue(criteria.priceRange().getValue().getMinPrice());
            criteria.maxPrice().setValue(criteria.priceRange().getValue().getMaxPrice());
            criteria.priceRange().setValue(null);
        }

        if (!criteria.bedsRange().isNull()) {
            criteria.minBeds().setValue(BedroomChoice.getChoice(criteria.bedsRange().getValue().getMinBeds()));
            criteria.maxBeds().setValue(BedroomChoice.getChoice(criteria.bedsRange().getValue().getMaxBeds()));
            criteria.bedsRange().setValue(null);
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
}