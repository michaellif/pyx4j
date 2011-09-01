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
 * @author job_vista
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.propertyvista.pmsite.server.model.SearchCriteriaModel;

public class AdvancedSearchCriteriaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaPanel() {
        super("advancedSearchCriteriaPanel");

        CompoundPropertyModel<SearchCriteriaModel> model = new CompoundPropertyModel<SearchCriteriaModel>(new SearchCriteriaModel());

        final Form<SearchCriteriaModel> form = new Form<SearchCriteriaModel>("advancedSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                info("Form Submitted:");
                info("searchType = " + getModelObject().getSearchType());
                info("province = " + getModelObject().getProvince());
                info("city = " + getModelObject().getCity());
                info("location = " + getModelObject().getLocation());
                info("distance = " + getModelObject().getDistance());
                info("bedsMin = " + getModelObject().getBedsMin());
                info("bedsMax = " + getModelObject().getBedsMax());
                info("bathsMin = " + getModelObject().getBathsMin());
                info("bathsMax = " + getModelObject().getBathsMax());
                info("priceMin = " + getModelObject().getPriceMin());
                info("priceMax = " + getModelObject().getPriceMax());
                info("amenities = " + getModelObject().getAmenities());
                //setResponsePage(FindAptPage.class);
            }
        };

        form.add(new AdvancedSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit"));

        add(form);

    }
}
