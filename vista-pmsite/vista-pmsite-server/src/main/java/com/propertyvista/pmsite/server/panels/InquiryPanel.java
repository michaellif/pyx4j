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

import java.util.Arrays;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Inquiry;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleRadioGroup;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.InquiryPage;

public class InquiryPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaPanel.class);

    public InquiryPanel(String id, final Building building, final Floorplan fp) {
        super(id);

        final Building bld = (fp == null ? building : fp.building());
        Inquiry entity = EntityFactory.create(Inquiry.class);
        IPojo<Inquiry> pojo = ServerEntityFactory.getPojo(entity);
        final CompoundPropertyModel<IPojo<Inquiry>> model = new CompoundPropertyModel<IPojo<Inquiry>>(pojo);

        final StatelessForm<IPojo<Inquiry>> form = new StatelessForm<IPojo<Inquiry>>("inquiryForm", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                if (fp == null) {
                    setResponsePage(AptDetailsPage.class, new PageParameters().set("propId", bld.id().getValue().asLong()));
                } else {
                    setResponsePage(InquiryPage.class, new PageParameters().set("fpId", fp.id().getValue().asLong()));
                }
            }

        };

        // add Error Message panel
        form.add(new FeedbackPanel("form_messages"));

        // add form input fields
        // name
        form.add(new WicketUtils.DropDownList<Inquiry.Title>("name.namePrefix", Arrays.asList(Inquiry.Title.values()), false, false));
        form.add(new RequiredTextField<String>("name.firstName").setLabel(new Model<String>(i18n.tr("First Name"))));
        form.add(new RequiredTextField<String>("name.lastName").setLabel(new Model<String>(i18n.tr("Last Name"))));
        // phone
        TextField<String> workPhone = new TextField<String>("workPhone", new Model<String>());
        form.add(workPhone.setLabel(new Model<String>(i18n.tr("Work Phone"))));
        form.add(new TextField<String>("workPhoneExt", new Model<String>()));
        TextField<String> cellPhone = new TextField<String>("cellPhone", new Model<String>());
        form.add(cellPhone.setLabel(new Model<String>(i18n.tr("Cell Phone"))));
        // email
        EmailTextField email = new EmailTextField("email.address");
        form.add(email.setLabel(new Model<String>(i18n.tr("Email Address"))));
        // floorplan selector
        Long curFp = null;
        if (fp != null) {
            curFp = fp.id().getValue().asLong();
        }
        SimpleRadioGroup<Long> radioGroup = new SimpleRadioGroup<Long>("floorPlanList", new Model<Long>(curFp));
        for (Floorplan p : PMSiteContentManager.getBuildingFloorplans(bld).keySet()) {
            Double minPrice = null;
            for (AptUnit u : PMSiteContentManager.getBuildingAptUnits(p.building(), p)) {
                Double _prc = u.financial().marketRent().getValue();
                if (minPrice == null || minPrice > _prc) {
                    minPrice = _prc;
                }
            }
            String fpEntry = SimpleMessageFormat.format(i18n.tr("<b>{0}</b> - {1} Bed, {2} Bath, {3,choice,null#price not available|!null#from $ {3}}"), p
                    .name().getValue(), p.bedrooms().getValue(), p.bathrooms().getValue(), minPrice);
            radioGroup.addChoice(p.id().getValue().asLong(), fpEntry);
        }
        form.add(radioGroup.setEscapeModelStrings(false));
        // lease term
        form.add(new RadioChoice<Inquiry.LeaseTerm>("availLeaseTerms", new Model<Inquiry.LeaseTerm>(), Arrays.asList(Inquiry.LeaseTerm.values())));
        // moving date
        form.add(new TextField<String>("movingDate", new Model<String>()));
        // apmnt date / time
        String[] aptTimes = { i18n.tr("Morning"), i18n.tr("Afternoon"), i18n.tr("Evening") };
        form.add(new TextField<String>("apmntDate1", new Model<String>()));
        form.add(new RadioChoice<String>("apmntTime1", new Model<String>(), Arrays.asList(aptTimes)));
        form.add(new TextField<String>("apmntDate2", new Model<String>()));
        form.add(new RadioChoice<String>("apmntTime2", new Model<String>(), Arrays.asList(aptTimes)));
        // comments
        form.add(new TextArea<String>("comments"));
        // captcha
//        form.add(new Image("captchaImg", new CaptchaImageResource("1234", 40, 8)));
//        form.add(new TextField<String>("captchaText", new Model<String>()).add(AttributeModifier.replace("size", "6")));

        form.add(new Button("inquirySubmit").add(AttributeModifier.replace("value", i18n.tr("Submit"))));

        form.add(new WicketUtils.OneRequiredFormValidator(workPhone, cellPhone, email));

        add(form);
    }
}
