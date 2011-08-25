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

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.propertyvista.pmsite.server.pages.FindAptPage;

//http://www.google.com/codesearch#o92Uy7_Jjpw/base/openqrm-3.5.2/src/base/java/main/code/com/qlusters/qrm/web/wicket/markup/&type=cs
public class QuickSearchCriteriaPanel extends Panel {

    private static final long serialVersionUID = 1L;

    public QuickSearchCriteriaPanel() {
        super("quickSearchCriteriaPanel");

        CompoundPropertyModel<QuickSearchModel> model = new CompoundPropertyModel<QuickSearchModel>(new QuickSearchModel());

        final Form<QuickSearchModel> form = new Form<QuickSearchModel>("quickSearchCriteriaForm", model);

        form.add(new QuickSearchCriteriaInputPanel("searchCriteriaInput", model));

        form.add(new Button("searchSubmit") {

            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                super.onSubmit();
                executeSearch(form.getModelObject());
            }

        });

        add(form);

    }

    private void executeSearch(QuickSearchModel model) {
        PageParameters parameters = new PageParameters();
        parameters.put("param", "value");
        setResponsePage(FindAptPage.class, parameters);
    }

}