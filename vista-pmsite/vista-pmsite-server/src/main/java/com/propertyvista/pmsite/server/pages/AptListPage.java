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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;

import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.PageParamsUtil;
import com.propertyvista.pmsite.server.panels.AdvancedSearchCriteriaInputPanel;
import com.propertyvista.pmsite.server.panels.AptListPanel;
import com.propertyvista.pmsite.server.panels.GwtInclude;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.AmenityType;

public class AptListPage extends BasePage {

    public AptListPage(PageParameters params) {
        super(params);
        setVersioned(false);

        Map<String, String[]> argsW = params.toRequestParameters();

        Map<String, List<String>> argsE = new HashMap<String, List<String>>();
        for (String key : argsW.keySet()) {
            argsE.put(key, Arrays.asList(argsW.get(key)[0]));
        }

        PropertySearchCriteria criteria = EntityArgsConverter.createFromArgs(PropertySearchCriteria.class, argsE);
        String[] amenities = argsW.get("amenities");
        if (amenities != null) {
            for (int i = 0; i < amenities.length; i++) {
                criteria.amenities().add(AmenityType.valueOf(amenities[i]));
            }
        }
        IPojo<PropertySearchCriteria> pojo = ServerEntityFactory.getPojo(criteria);
        final CompoundPropertyModel<IPojo<PropertySearchCriteria>> model = new CompoundPropertyModel<IPojo<PropertySearchCriteria>>(pojo);

        final Form<IPojo<PropertySearchCriteria>> form = new Form<IPojo<PropertySearchCriteria>>("advancedSearchCriteriaForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(AptListPage.class, PageParamsUtil.convertToPageParameters(model.getObject().getEntityValue()));
            }

        };

        //TODO use PropertySearchCriteria instead
        form.add(new AdvancedSearchCriteriaInputPanel("searchCriteriaInput", model));
        form.add(new Button("searchSubmit"));

        add(form);

        add(new AptListPanel("aptListPanel", new CompoundPropertyModel<IList<PropertyDTO>>(PMSiteContentManager.getPropertyList(criteria).properties())));

        add(new GwtInclude("gwtInclude"));
    }
}
