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
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.CompoundIEntityModel;
import com.propertyvista.pmsite.server.model.WicketUtils.DateInput;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleRadioGroup;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.pages.InquirySuccessPage;

public class InquiryPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaPanel.class);

    public InquiryPanel(String id, final Building building, final Floorplan fp) {
        super(id);

        final Building bld = (fp == null ? building : fp.building());
        Lead lead = EntityFactory.create(Lead.class);
        final CompoundIEntityModel<Lead> model = new CompoundIEntityModel<Lead>(lead);

        final StatelessForm<IPojo<Lead>> form = new StatelessForm<IPojo<Lead>>("inquiryForm", model) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public void onSubmit() {
                // update model
                Lead lead = model.getObject().getEntityValue();
                // floorplan
                Long fpId = ((FormComponent<Long>) get("floorPlanList")).getModelObject();
                if (fpId != null) {
                    lead.floorplan().id().setValue(new Key(fpId));
                }
                lead.createDate().setValue(new LogicalDate());
                Persistence.service().persist(lead);
                PageParameters params = new PageParameters();
                if (fp != null) {
                    params.set(PMSiteApplication.ParamNameFloorplan, fp.id().getValue().asLong());
                } else {
                    params.set(PMSiteApplication.ParamNameBuilding, bld.id().getValue().asLong());
                }
                setResponsePage(InquirySuccessPage.class, params);
            }
        };

        // add Error Message panel
        form.add(new FeedbackPanel("form_messages"));

        // add form input fields
        // name
        form.add(new WicketUtils.DropDownList<Name.Prefix>("namePrefix", model.bind(lead.person().name().namePrefix()), Arrays.asList(Name.Prefix.values()),
                true, false));

        String label;
//        label = lead.person().name().firstName().getMeta().getCaption();
        label = i18n.tr("First Name");
        form.add(new Label("firstNameLabel", label));
        form.add(new RequiredTextField<String>("firstName", model.bind(lead.person().name().firstName())).setLabel(new Model<String>(label)));
        label = i18n.tr("Last Name");
        form.add(new Label("lastNameLabel", label));
        form.add(new RequiredTextField<String>("lastName", model.bind(lead.person().name().lastName())).setLabel(new Model<String>(label)));
        // phone
        label = i18n.tr("Work Phone");
        form.add(new Label("workPhoneLabel", label));
        TextField<String> workPhone = new TextField<String>("workPhone", model.bind(lead.person().workPhone().number()));
        form.add(workPhone.setLabel(new Model<String>(label)));
        form.add(new TextField<Integer>("workPhoneExt", model.bind(lead.person().workPhone().extension())));
        label = i18n.tr("Cell Phone");
        form.add(new Label("cellPhoneLabel", label));
        TextField<String> cellPhone = new TextField<String>("cellPhone", model.bind(lead.person().mobilePhone().number()));
        form.add(cellPhone.setLabel(new Model<String>(lead.person().mobilePhone().getMeta().getCaption())));
        // email
        label = i18n.tr("Email Address");
        form.add(new Label("emailLabel", label));
        EmailTextField email = new EmailTextField("email", model.bind(lead.person().email().address()));
        form.add(email.setLabel(new Model<String>(label)));
        // floorplan selector
        Long curFp = null;
        if (fp != null) {
            curFp = fp.id().getValue().asLong();
        }
        SimpleRadioGroup<Long> radioGroup = new SimpleRadioGroup<Long>("floorPlanList", new Model<Long>(curFp));
        for (Floorplan p : PMSiteContentManager.getBuildingFloorplans(bld).keySet()) {
            Double minPrice = null;
            for (AptUnit u : PMSiteContentManager.getBuildingAptUnits(p.building(), p)) {
                Double _prc = u.financial()._marketRent().getValue();
                if (minPrice == null || minPrice > _prc) {
                    minPrice = _prc;
                }
            }
            String fpEntry = SimpleMessageFormat.format(i18n.tr("<b>{0}</b> - {1} Bed, {2} Bath, {3,choice,null#price not available|!null#from ${3}}"), p
                    .name().getValue(), p.bedrooms().getValue(), p.bathrooms().getValue(), minPrice);
            radioGroup.addChoice(p.id().getValue().asLong(), fpEntry);
        }
        form.add(radioGroup.setEscapeModelStrings(false));
        // lease term
        form.add(new RadioChoice<Lead.LeaseTerm>("leaseTerm", model.bind(lead.leaseTerm()), Arrays.asList(Lead.LeaseTerm.values())));
        // moving date
        form.add(new DateInput("movingDate", model.bind(lead.moveInDate())));
        // apmnt date / time
        form.add(new DateInput("appointmentDate1", model.bind(lead.appointmentDate1())));
        form.add(new RadioChoice<Lead.DayPart>("appointmentTime1", model.bind(lead.appointmentTime1()), Arrays.asList(Lead.DayPart.values())));
        form.add(new DateInput("appointmentDate2", model.bind(lead.appointmentDate2())));
        form.add(new RadioChoice<Lead.DayPart>("appointmentTime2", model.bind(lead.appointmentTime2()), Arrays.asList(Lead.DayPart.values())));
        // ref source
        form.add(new WicketUtils.DropDownList<Lead.RefSource>("refSource", model.bind(lead.refSource()), Arrays.asList(Lead.RefSource.values()), true, false));
        // comments
        form.add(new TextArea<String>("comments", model.bind(lead.comments())));
        // captcha
//        form.add(new Image("captchaImg", new CaptchaImageResource("1234", 40, 8)));
//        form.add(new TextField<String>("captchaText", new Model<String>()).add(AttributeModifier.replace("size", "6")));

        form.add(new Button("inquirySubmit").add(AttributeModifier.replace("value", i18n.tr("Submit"))));
        // make sure that either workPhone, cellPhone or email is provided
        form.add(new WicketUtils.OneRequiredFormValidator(workPhone, cellPhone, email));

        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        int styleId = ((PMSiteWebRequest) getRequest()).getContentManager().getStyleId();
        String fileCSS = "inquiryPanel" + styleId + ".css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);
    }
}
