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
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

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

    private final Map<GadgetType, CComponent<?, ?>> panelMap = new HashMap<GadgetType, CComponent<?, ?>>();

    public HomePageGadgetForm(IForm<HomePageGadget> view) {
        super(HomePageGadget.class, view);
        mainPanel = new SimplePanel();
        mainPanel.setSize("100%", "100%");

        panelMap.put(GadgetType.custom, getCustomContentEditor());
        panelMap.put(GadgetType.news, getNewsContentEditor());
        panelMap.put(GadgetType.testimonials, getTestimContentEditor());
        panelMap.put(GadgetType.promo, getPromoContentEditor());

        TwoColumnFlexFormPanel generalPanel = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = 0;
        generalPanel.setH1(row++, 0, 2, i18n.tr("General"));
        generalPanel.setWidget(row++, 0, 2, injectAndDecorate(proto().name(), 10, true));
        generalPanel.setWidget(row++, 0, 2, injectAndDecorate(proto().area(), 10, true));
        generalPanel.setWidget(row++, 0, 2, injectAndDecorate(proto().status(), 10, true));
        generalPanel.setWidget(row++, 0, 2, mainPanel);

        selectTab(addTab(generalPanel));

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
            CComponent<?, ?> editor = panelMap.get(type);
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

    private CComponent<?, ?> getCustomContentEditor() {
        CEntityForm<CustomGadgetContent> editor = new CEntityForm<CustomGadgetContent>(CustomGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = 0;
                main.setH1(row++, 0, 2, proto().htmlContent().getMeta().getCaption());
                main.setWidget(row++, 0, 2, inject(proto().htmlContent(), new RichTextContentEditor(true)));
                return main;
            }
        };
        return editor;
    }

    private CComponent<?, ?> getNewsContentEditor() {
        CEntityForm<NewsGadgetContent> editor = new CEntityForm<NewsGadgetContent>(NewsGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = 0;
                main.setH1(row++, 0, 2, proto().news().getMeta().getCaption());
                main.setWidget(row++, 0, 2, inject(proto().news(), new NewsFolder(isEditable())));
                return main;
            }
        };
        return editor;
    }

    private CComponent<?, ?> getTestimContentEditor() {
        CEntityForm<TestimonialsGadgetContent> editor = new CEntityForm<TestimonialsGadgetContent>(TestimonialsGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = 0;
                main.setH1(row++, 0, 2, proto().testimonials().getMeta().getCaption());
                main.setWidget(row++, 0, 2, inject(proto().testimonials(), new TestimonialFolder(isEditable())));
                return main;
            }
        };
        return editor;
    }

    private CComponent<?, ?> getPromoContentEditor() {
        CEntityForm<PromoGadgetContent> editor = new CEntityForm<PromoGadgetContent>(PromoGadgetContent.class) {
            @Override
            protected IsWidget createContent() {
                TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
                int row = 0;
                main.setH1(row++, 0, 2, i18n.tr("Promotions"));
                main.setWidget(row++, 0, 2, new HTML(i18n.tr("No input required. Content will be generated automatically according to built-in rules")));
                return main;
            }
        };
        return editor;
    }
}