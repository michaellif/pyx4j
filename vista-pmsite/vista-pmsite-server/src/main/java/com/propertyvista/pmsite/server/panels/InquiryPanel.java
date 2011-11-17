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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import templates.TemplateResources;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.Phone;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Inquiry;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteContentManager;
import com.propertyvista.pmsite.server.PMSiteWebRequest;
import com.propertyvista.pmsite.server.model.WicketUtils;
import com.propertyvista.pmsite.server.model.WicketUtils.SimpleRadioGroup;
import com.propertyvista.pmsite.server.model.WicketUtils.VolatileTemplateResourceReference;
import com.propertyvista.pmsite.server.pages.InquirySuccessPage;

public class InquiryPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.get(AdvancedSearchCriteriaPanel.class);

    public InquiryPanel(String id, final Building building, final Floorplan fp) {
        super(id);

        final Building bld = (fp == null ? building : fp.building());
        Inquiry inquiry = EntityFactory.create(Inquiry.class);
        inquiry.phone1().type().setValue(Phone.Type.home);
        inquiry.phone2().type().setValue(Phone.Type.mobile);
        IPojo<Inquiry> pojo = ServerEntityFactory.getPojo(inquiry);
        final CompoundPropertyModel<IPojo<Inquiry>> model = new CompoundPropertyModel<IPojo<Inquiry>>(pojo);

        final StatelessForm<IPojo<Inquiry>> form = new StatelessForm<IPojo<Inquiry>>("inquiryForm", model) {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public void onSubmit() {
                // update model
                Inquiry inquiry = model.getObject().getEntityValue();
/*
 * // phones
 * String workPhone = ((FormComponent<String>) get("workPhone")).getModelObject();
 * if (workPhone != null && workPhone.length() > 0) {
 * inquiry.phone1().type().setValue(Phone.Type.home);
 * inquiry.phone1().number().setValue(workPhone);
 * String ext = ((FormComponent<String>) get("workPhoneExt")).getModelObject();
 * try {
 * inquiry.phone1().extension().setValue(Integer.valueOf(ext));
 * } catch (NumberFormatException ignore) {
 * // do nothing
 * }
 * }
 * String cellPhone = ((FormComponent<String>) get("cellPhone")).getModelObject();
 * if (cellPhone != null && cellPhone.length() > 0) {
 * inquiry.phone2().type().setValue(Phone.Type.mobile);
 * inquiry.phone2().number().setValue(cellPhone);
 * }
 */
                // add current building
                inquiry.building().id().setValue(bld.id().getValue());
                // floorplan
                Long fpId = ((FormComponent<Long>) get("floorPlanList")).getModelObject();
                if (fpId != null) {
                    inquiry.floorplan().id().setValue(new Key(fpId));
                }
                inquiry.createDate().setValue(new LogicalDate());
                Persistence.service().persist(inquiry);
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
        form.add(new WicketUtils.DropDownList<Inquiry.Title>("name.namePrefix", Arrays.asList(Inquiry.Title.values()), false, false));
        form.add(new RequiredTextField<String>("name.firstName").setLabel(new Model<String>(i18n.tr("First Name"))));
        form.add(new RequiredTextField<String>("name.lastName").setLabel(new Model<String>(i18n.tr("Last Name"))));
        // phone
        TextField<String> workPhone = new TextField<String>("phone1.number");
        form.add(workPhone.setLabel(new Model<String>(i18n.tr("Work Phone"))));
        form.add(new TextField<String>("phone1.extension"));
        TextField<String> cellPhone = new TextField<String>("phone2.number");
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
                Double _prc = u.financial()._marketRent().getValue();
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
        form.add(new RadioChoice<Inquiry.LeaseTerm>("leaseTerm", Arrays.asList(Inquiry.LeaseTerm.values())));
        // moving date
        form.add(new WicketUtils.DateInput("movingDate"));
        // apmnt date / time
        form.add(new WicketUtils.DateInput("appointmentDate1"));
        form.add(new RadioChoice<Inquiry.DayPart>("appointmentTime1", Arrays.asList(Inquiry.DayPart.values())));
        form.add(new WicketUtils.DateInput("appointmentDate2"));
        form.add(new RadioChoice<Inquiry.DayPart>("appointmentTime2", Arrays.asList(Inquiry.DayPart.values())));
        // ref source
        form.add(new WicketUtils.DropDownList<Inquiry.RefSource>("refSource", Arrays.asList(Inquiry.RefSource.values()), false, false));
        // comments
        form.add(new TextArea<String>("comments"));
        // captcha
//        form.add(new Image("captchaImg", new CaptchaImageResource("1234", 40, 8)));
//        form.add(new TextField<String>("captchaText", new Model<String>()).add(AttributeModifier.replace("size", "6")));

        form.add(new Button("inquirySubmit").add(AttributeModifier.replace("value", i18n.tr("Submit"))));

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
