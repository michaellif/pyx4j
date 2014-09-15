/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-26
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.website.content.gadgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.administration.website.RichTextContentEditor;
import com.propertyvista.domain.site.gadgets.CustomGadgetContent;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;
import com.propertyvista.domain.site.gadgets.NewsGadgetContent;
import com.propertyvista.domain.site.gadgets.PromoGadgetContent;
import com.propertyvista.domain.site.gadgets.TestimonialsGadgetContent;

public class HomePageGadgetForm extends CrmEntityForm<HomePageGadget> {
    private static final I18n i18n = I18n.get(HomePageGadgetForm.class);

    private final SimplePanel mainPanel;

    private final Map<GadgetType, CComponent<?, ?, ?>> panelMap = new HashMap<GadgetType, CComponent<?, ?, ?>>();

    public HomePageGadgetForm(IForm<HomePageGadget> view) {
        super(HomePageGadget.class, view);
        mainPanel = new SimplePanel();
        mainPanel.setSize("100%", "100%");

        panelMap.put(GadgetType.custom, getCustomContentEditor());
        panelMap.put(GadgetType.news, getNewsContentEditor());
        panelMap.put(GadgetType.testimonials, getTestimContentEditor());
        panelMap.put(GadgetType.promo, getPromoContentEditor());

        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("General"));
        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().area()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().status()).decorate().componentWidth(120);
        formPanel.append(Location.Left, mainPanel);

        selectTab(addTab(formPanel, i18n.tr("General")));

    }

    @Override
    protected void onValuePropagation(HomePageGadget value, boolean fireEvent, boolean populate) {
        // set content panel here
        HomePageGadget gadget = value;
        if (gadget != null && !gadget.type().isNull()) {
            GadgetType type = gadget.type().getValue();
            Class<? extends GadgetContent> contentClass = type.getContentClass();
            if (!contentClass.equals(gadget.content().getInstanceValueClass())) {
                // this should never happen...
                gadget.content().set(EntityFactory.create(contentClass));
            }
            CComponent<?, ?, ?> editor = panelMap.get(type);
            if (editor != null) {
                if (contains(proto().content())) {
                    if (!editor.equals(get(proto().content()))) {
                        unbind(proto().content());
                        mainPanel.setWidget(inject(proto().content(), editor));
                    }
                } else {
                    mainPanel.setWidget(inject(proto().content(), editor));
                }
            }
        }
        super.onValuePropagation(value, fireEvent, populate);
    }

    private CComponent<?, ?, ?> getCustomContentEditor() {
        CForm<CustomGadgetContent> editor = new CForm<CustomGadgetContent>(CustomGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.h1(proto().htmlContent().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().htmlContent(), new RichTextContentEditor(true));
                return formPanel;
            }
        };
        return editor;
    }

    private CComponent<?, ?, ?> getNewsContentEditor() {
        CForm<NewsGadgetContent> editor = new CForm<NewsGadgetContent>(NewsGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.h1(proto().news().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().news(), new NewsFolder(isEditable()));
                return formPanel;
            }
        };
        return editor;
    }

    private CComponent<?, ?, ?> getTestimContentEditor() {
        CForm<TestimonialsGadgetContent> editor = new CForm<TestimonialsGadgetContent>(TestimonialsGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.h1(proto().testimonials().getMeta().getCaption());
                formPanel.append(Location.Dual, proto().testimonials(), new TestimonialFolder(isEditable()));
                return formPanel;
            }
        };
        return editor;
    }

    private CComponent<?, ?, ?> getPromoContentEditor() {
        CForm<PromoGadgetContent> editor = new CForm<PromoGadgetContent>(PromoGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.h1(i18n.tr("Promotions"));
                formPanel.append(Location.Dual, new HTML(i18n.tr("No input required. Content will be generated automatically according to built-in rules")));
                return formPanel;
            }
        };
        return editor;
    }
}