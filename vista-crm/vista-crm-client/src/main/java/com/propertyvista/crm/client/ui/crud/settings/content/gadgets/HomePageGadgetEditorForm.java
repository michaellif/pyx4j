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
package com.propertyvista.crm.client.ui.crud.settings.content.gadgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.settings.content.site.NewsFolder;
import com.propertyvista.crm.client.ui.crud.settings.content.site.RichTextContentEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.site.TestimonialFolder;
import com.propertyvista.domain.site.gadgets.CustomGadgetContent;
import com.propertyvista.domain.site.gadgets.GadgetContent;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.domain.site.gadgets.HomePageGadget.GadgetType;
import com.propertyvista.domain.site.gadgets.NewsGadgetContent;
import com.propertyvista.domain.site.gadgets.PromoGadgetContent;
import com.propertyvista.domain.site.gadgets.TestimonialsGadgetContent;

public class HomePageGadgetEditorForm extends CrmEntityForm<HomePageGadget> {
    private static final I18n i18n = I18n.get(HomePageGadgetEditorForm.class);

    private final SimplePanel mainPanel = new SimplePanel();

    private final Map<GadgetType, CComponent<?, ?>> panelMap = new HashMap<GadgetType, CComponent<?, ?>>();

    public HomePageGadgetEditorForm() {
        this(false);
    }

    public HomePageGadgetEditorForm(boolean viewMode) {
        super(HomePageGadget.class, viewMode);
        panelMap.put(GadgetType.custom, getCustomContentEditor());
        panelMap.put(GadgetType.news, getNewsContentEditor());
        panelMap.put(GadgetType.testimonials, getTestimContentEditor());
        panelMap.put(GadgetType.promo, getPromoContentEditor());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setH1(row++, 0, 1, i18n.tr("General"));
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().name()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().area()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(row++, 0, mainPanel);

        mainPanel.setSize("100%", "100%");

        return new ScrollPanel(main);
    }

    @Override
    protected void propagateValue(HomePageGadget value, boolean fireEvent, boolean populate) {
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
        super.propagateValue(value, fireEvent, populate);
    }

    private CComponent<?, ?> getCustomContentEditor() {
        CEntityDecoratableForm<CustomGadgetContent> editor = new CEntityDecoratableForm<CustomGadgetContent>(CustomGadgetContent.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();
                int row = 0;
                main.setH1(row++, 0, 1, proto().htmlContent().getMeta().getCaption());
                main.setWidget(row++, 0, inject(proto().htmlContent(), new RichTextContentEditor(true)));
                return main;
            }
        };
        return editor;
    }

    private CComponent<?, ?> getNewsContentEditor() {
        CEntityDecoratableForm<NewsGadgetContent> editor = new CEntityDecoratableForm<NewsGadgetContent>(NewsGadgetContent.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();
                int row = 0;
                main.setH1(row++, 0, 1, proto().news().getMeta().getCaption());
                main.setWidget(row++, 0, inject(proto().news(), new NewsFolder(isEditable())));
                return main;
            }
        };
        return editor;
    }

    private CComponent<?, ?> getTestimContentEditor() {
        CEntityDecoratableForm<TestimonialsGadgetContent> editor = new CEntityDecoratableForm<TestimonialsGadgetContent>(TestimonialsGadgetContent.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();
                int row = 0;
                main.setH1(row++, 0, 1, proto().testimonials().getMeta().getCaption());
                main.setWidget(row++, 0, inject(proto().testimonials(), new TestimonialFolder(isEditable())));
                return main;
            }
        };
        return editor;
    }

    private CComponent<?, ?> getPromoContentEditor() {
        CEntityDecoratableForm<PromoGadgetContent> editor = new CEntityDecoratableForm<PromoGadgetContent>(PromoGadgetContent.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel main = new FormFlexPanel();
                int row = 0;
                main.setH1(row++, 0, 1, i18n.tr("Promotions"));
                main.setWidget(row++, 0, new HTML(i18n.tr("No input required. Content will be generated automatically according to built-in rules")));
                return main;
            }
        };
        return editor;
    }
}