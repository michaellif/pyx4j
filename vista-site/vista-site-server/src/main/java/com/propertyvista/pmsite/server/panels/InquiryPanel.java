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

import java.math.BigDecimal;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.MinMaxPair;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.tenant.LeadFacade;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.PropertyFinder;
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
        final CompoundIEntityModel<Lead> leadModel = new CompoundIEntityModel<Lead>(lead);
        Guest guest = EntityFactory.create(Guest.class);
        final CompoundIEntityModel<Guest> guestModel = new CompoundIEntityModel<Guest>(guest);

        final StatelessForm<IPojo<Lead>> form = new StatelessForm<IPojo<Lead>>("inquiryForm", leadModel) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public void onSubmit() {
                // update model
                Lead lead = leadModel.getObject().getEntityValue();
                lead.status().setValue(Lead.Status.active);
                // add guest
                lead.guests().add(guestModel.getObject().getEntityValue());
                // floorplan
                FormComponent<Long> fpList = (FormComponent<Long>) get("floorPlanList");
                Long fpId = fpList.getModelObject();
                if (fpId != null) {
                    lead.floorplan().id().setValue(new Key(fpId));
                }
                lead.createDate().setValue(new LogicalDate(SystemDateManager.getDate()));
                ServerSideFactory.create(LeadFacade.class).init(lead);
                ServerSideFactory.create(LeadFacade.class).persist(lead);
                Persistence.service().commit();
                PageParameters params = new PageParameters();
                if (fp != null) {
                    params.set(PMSiteApplication.ParamNameFloorplan, fp.id().getValue().asLong());
                } else {
                    params.set(PMSiteApplication.ParamNameBuilding, bld.propertyCode().getValue());
                }
                setResponsePage(InquirySuccessPage.class, params);
            }
        };

        // add form input fields
        // name
        form.add(new WicketUtils.DropDownList<Name.Prefix>("namePrefix", guestModel.bind(guest.person().name().namePrefix()), Arrays.asList(Name.Prefix
                .values()), true, false));
        String label;
        label = i18n.tr("First Name");
        form.add(new Label("firstNameLabel", label));
        form.add(new RequiredTextField<String>("firstName", guestModel.bind(guest.person().name().firstName())).setLabel(new Model<String>(label)));
        form.add(new FormErrorPanel("firstNameError", "firstName"));
        label = i18n.tr("Last Name");
        form.add(new Label("lastNameLabel", label));
        form.add(new RequiredTextField<String>("lastName", guestModel.bind(guest.person().name().lastName())).setLabel(new Model<String>(label)));
        form.add(new FormErrorPanel("lastNameError", "lastName"));
        // phone
        label = i18n.tr("Work Phone");
        form.add(new Label("workPhoneLabel", label));
        TextField<String> workPhone = new TextField<String>("workPhone", guestModel.bind(guest.person().workPhone()));
        form.add(workPhone.setLabel(new Model<String>(label)));
        form.add(new FormErrorPanel("workPhoneError", "workPhone"));
        label = i18n.tr("Cell Phone");
        form.add(new Label("cellPhoneLabel", label));
        TextField<String> cellPhone = new TextField<String>("cellPhone", guestModel.bind(guest.person().mobilePhone()));
        form.add(cellPhone.setLabel(new Model<String>(guest.person().mobilePhone().getMeta().getCaption())));
        form.add(new FormErrorPanel("cellPhoneError", "cellPhone"));
        // email
        label = i18n.tr("Email Address");
        form.add(new Label("emailLabel", label));
        EmailTextField email = new EmailTextField("email", guestModel.bind(guest.person().email()));
        form.add(email.setLabel(new Model<String>(label)));
        form.add(new FormErrorPanel("emailError", "email", "inquiryForm"));

        // floorplan selector
        Long curFp = null;
        if (fp != null) {
            curFp = fp.id().getValue().asLong();
        }
        SimpleRadioGroup<Long> radioGroup = new SimpleRadioGroup<Long>("floorPlanList", new Model<Long>(curFp));
        for (Floorplan p : PropertyFinder.getBuildingFloorplans(bld).keySet()) {

            MinMaxPair<BigDecimal> minMaxMarketRent = PropertyFinder.getMinMaxMarketRent(PropertyFinder.getBuildingAptUnits(p.building(), p));
            String fpEntry = SimpleMessageFormat.format(i18n.tr("<b>{0}</b> - {1} Bed, {2} Bath, {3,choice,null#price not available|!null#from ${3}}"), p
                    .marketingName().getValue(), p.bedrooms().getValue(), p.bathrooms().getValue(), minMaxMarketRent.getMin());
            radioGroup.addChoice(p.id().getValue().asLong(), fpEntry);
        }
        form.add(radioGroup.setEscapeModelStrings(false));
        // lease term
        form.add(new RadioChoice<Lead.LeaseTerm>("leaseTerm", leadModel.bind(lead.leaseTerm()), Arrays.asList(Lead.LeaseTerm.values())));
        // moving date
        form.add(new DateInput("movingDate", leadModel.bind(lead.moveInDate())));
        // apmnt date / time
        form.add(new DateInput("appointmentDate1", leadModel.bind(lead.appointmentDate1())));
        form.add(new RadioChoice<Lead.DayPart>("appointmentTime1", leadModel.bind(lead.appointmentTime1()), Arrays.asList(Lead.DayPart.values())));
        form.add(new DateInput("appointmentDate2", leadModel.bind(lead.appointmentDate2())));
        form.add(new RadioChoice<Lead.DayPart>("appointmentTime2", leadModel.bind(lead.appointmentTime2()), Arrays.asList(Lead.DayPart.values())));
        // ref source
        form.add(new WicketUtils.DropDownList<Lead.RefSource>("refSource", leadModel.bind(lead.refSource()), Arrays.asList(Lead.RefSource.values()), true,
                false));
        // comments
        form.add(new TextArea<String>("comments", leadModel.bind(lead.comments())));
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
        String skin = ((PMSiteWebRequest) getRequest()).getContentManager().getSiteSkin();
        String fileCSS = skin + "/" + "inquiryPanel.css";
        VolatileTemplateResourceReference refCSS = new VolatileTemplateResourceReference(TemplateResources.class, fileCSS, "text/css",
                ((PMSiteWebRequest) getRequest()).getStylesheetTemplateModel());
        response.renderCSSReference(refCSS);
        super.renderHead(response);
    }
}
