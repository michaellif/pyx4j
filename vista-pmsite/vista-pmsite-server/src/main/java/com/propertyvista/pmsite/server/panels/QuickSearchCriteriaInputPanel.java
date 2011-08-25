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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.propertyvista.pmsite.server.model.Province;
import com.propertyvista.pmsite.server.panels.QuickSearchModel.BedroomChoice;
import com.propertyvista.pmsite.server.panels.QuickSearchModel.PriceChoice;

public class QuickSearchCriteriaInputPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public QuickSearchCriteriaInputPanel(String id, IModel<QuickSearchModel> model) {
        super(id, model);

        {
            List<Province> provinces = new ArrayList<Province>();
            provinces.add(new Province("ON", "Ontario"));
            provinces.add(new Province("QC", "Quebec"));
            provinces.add(new Province("BC", "British Columbia"));

            IChoiceRenderer<Province> renderer = new IChoiceRenderer<Province>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Object getDisplayValue(Province paramT) {
                    return paramT.getName();
                }

                @Override
                public String getIdValue(Province paramT, int paramInt) {
                    return paramT.getCode();
                }

            };
            DropDownChoice<Province> provincesChoice = new DropDownChoice<Province>("province", provinces, renderer);
            add(provincesChoice);
        }

        {
            List<String> cities = new ArrayList<String>();
            cities.add("Toronto");
            cities.add("Montreal");
            cities.add("Vancouver");
            DropDownChoice<String> citiesChoice = new DropDownChoice<String>("city", cities);
            add(citiesChoice);
        }

        {
            IChoiceRenderer<BedroomChoice> renderer = new IChoiceRenderer<BedroomChoice>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Object getDisplayValue(BedroomChoice paramT) {
                    return paramT.getDisplay();
                }

                @Override
                public String getIdValue(BedroomChoice paramT, int paramInt) {
                    return String.valueOf(paramInt);
                }

            };

            DropDownChoice<BedroomChoice> bedroomsChoice = new DropDownChoice<BedroomChoice>("bedrooms",
                    Arrays.asList(QuickSearchModel.BedroomChoice.values()), renderer);
            add(bedroomsChoice);
        }

        {

            IChoiceRenderer<PriceChoice> renderer = new IChoiceRenderer<PriceChoice>() {

                private static final long serialVersionUID = 1L;

                @Override
                public Object getDisplayValue(PriceChoice paramT) {
                    return paramT.getDisplay();
                }

                @Override
                public String getIdValue(PriceChoice paramT, int paramInt) {
                    return String.valueOf(paramInt);
                }

            };

            DropDownChoice<PriceChoice> priceChoice = new DropDownChoice<PriceChoice>("price", Arrays.asList(QuickSearchModel.PriceChoice.values()), renderer);
            add(priceChoice);
        }

    }
}
