/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 6, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.tenant.Inquiry;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.pages.InquiryPage;

public class InquiryPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaPanel.class);

    public InquiryPanel(String id, final Floorplan fp) {
        super(id);

        Inquiry entity = EntityFactory.create(Inquiry.class);
        IPojo<Inquiry> pojo = ServerEntityFactory.getPojo(entity);
        final CompoundPropertyModel<IPojo<Inquiry>> model = new CompoundPropertyModel<IPojo<Inquiry>>(pojo);

        final StatelessForm<IPojo<Inquiry>> form = new StatelessForm<IPojo<Inquiry>>("inquiryForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                setResponsePage(InquiryPage.class, new PageParameters().set("fpId", fp.id().getValue().asLong()));
            }

        };

        // add Error Message panel
        form.add(new FeedbackPanel("form_messages"));

        // add form input fields
        // name
//        IModel<Inquiry.title> title = new Model<Inquiry.title>();
//        form.add(new WicketUtils.DropDownList<Inquiry.title>("title", title, Arrays.asList(Inquiry.title.values()), false, false));
        form.add(new WicketUtils.DropDownList<Inquiry.Title>("name.namePrefix", Arrays.asList(Inquiry.Title.values()), false, false));
        form.add(new TextField<String>("name.firstName"));
        form.add(new TextField<String>("name.lastName"));
        // phone
        form.add(new TextField<String>("workPhone", new Model<String>()));
        form.add(new TextField<String>("workPhoneExt", new Model<String>()));
        form.add(new TextField<String>("cellPhone", new Model<String>()));
        // email
        form.add(new TextField<String>("email.address"));
        // floorplan selector
        final List<String> fpList = new ArrayList<String>();
        for (Floorplan p : PMSiteContentManager.getBuildingFloorplans(fp.building()).keySet()) {
            fpList.add(p.name().getValue());
        }
        form.add(new RadioChoice<String>("floorPlanList", new Model<String>(), fpList));
        // lease term
        form.add(new RadioChoice<Inquiry.LeaseTerm>("availLeaseTerms", new Model<Inquiry.LeaseTerm>(), Arrays.asList(Inquiry.LeaseTerm.values())));
        // comments
        form.add(new TextArea<String>("comments"));

        form.add(new Button("inquirySubmit").add(AttributeModifier.replace("value", i18n.tr("Submit"))));

        add(form);
    }
}
