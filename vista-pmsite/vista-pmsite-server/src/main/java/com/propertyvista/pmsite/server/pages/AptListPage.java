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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.model.SearchCriteriaModel;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaInputPanel;

public class AptListPage extends BasePage {

    public AptListPage() {
        super();

        CompoundPropertyModel<SearchCriteriaModel> model = new CompoundPropertyModel<SearchCriteriaModel>(PMSiteApplication.get().getSearchModel());

        final Form<SearchCriteriaModel> form = new Form<SearchCriteriaModel>("advancedSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(AptListPage.class);
            }
        };

        form.add(new AdvancedSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit"));

        add(form);

        SearchCriteriaModel data = model.getObject();
        String model_dump = "Search Criteria:\n" + "searchType = " + data.getSearchType() + "; province = " + data.getProvince() + "; city = " + data.getCity()
                + "; location = " + data.getLocation() + "; distance = " + data.getDistance() + "; bedsMin = " + data.getBedsMin() + "; bedsMax = "
                + data.getBedsMax() + "; bathsMin = " + data.getBathsMin() + "; bathsMax = " + data.getBathsMax() + "; priceMin = " + data.getPriceMin()
                + "; priceMax = " + data.getPriceMax() + "; amenities = " + data.getAmenities();

        add(new Label("model_dump", model_dump));
    }

}
