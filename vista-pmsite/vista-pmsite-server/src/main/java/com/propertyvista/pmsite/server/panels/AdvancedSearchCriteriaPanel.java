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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.pmsite.server.model.PageParamsUtil;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.BasePage;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class AdvancedSearchCriteriaPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public AdvancedSearchCriteriaPanel() {
        super("advancedSearchCriteriaPanel");

        PropertySearchCriteria entity = EntityFactory.create(PropertySearchCriteria.class);
        if (entity.searchType().isNull()) {
            entity.searchType().setValue(PropertySearchCriteria.SearchType.city);
        }
        IPojo<PropertySearchCriteria> pojo = ServerEntityFactory.getPojo(entity);
        final CompoundPropertyModel<IPojo<PropertySearchCriteria>> model = new CompoundPropertyModel<IPojo<PropertySearchCriteria>>(pojo);

        final StatelessForm<IPojo<PropertySearchCriteria>> form = new StatelessForm<IPojo<PropertySearchCriteria>>("advancedSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(AptListPage.class, PageParamsUtil.convertToPageParameters(model.getObject().getEntityValue()));
            }

        };

        // add Error Message panel
        form.add(new FeedbackPanel("form_messages"));
        form.add(new AdvancedSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit").add(AttributeModifier.replace("value", I18n.get(BasePage.class).tr("Search"))));

        add(form);

    }
}
