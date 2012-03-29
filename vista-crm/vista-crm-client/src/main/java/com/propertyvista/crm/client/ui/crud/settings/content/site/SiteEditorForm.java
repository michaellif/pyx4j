/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.EnumSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteEditorForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(SiteEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public SiteEditorForm() {
        this(false);
    }

    public SiteEditorForm(boolean viewMode) {
        super(SiteDescriptorDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createTestimonialsTab(), i18n.tr("Testimonials"));
        tabPanel.add(createNewsTab(), i18n.tr("News"));

        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    public IsWidget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;

        main.setH1(row++, 0, 1, i18n.tr("Look And Feel"));
        CComponent<?, ?> skinComp;
        main.setWidget(row++, 0, new DecoratorBuilder(skinComp = inject(proto().skin()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object1()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object2()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast1()), 10).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast2()), 10).build());

        if (skinComp instanceof CComboBox) {
            ((CComboBox<SiteDescriptor.Skin>) skinComp).setOptions(EnumSet.of(SiteDescriptor.Skin.skin1, SiteDescriptor.Skin.skin2, SiteDescriptor.Skin.skin3));
        }

        main.setH1(row++, 0, 1, proto().locales().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().locales(), new AvailableLocaleFolder(isEditable())));

        main.setH1(row++, 0, 1, proto().siteTitles().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().siteTitles(), new SiteTitlesFolder(isEditable())));

        main.setH1(row++, 0, 1, proto().logo().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().logo(), new PortalImageResourceFolder(isEditable())));

        main.setH1(row++, 0, 1, proto().slogan().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().slogan(), new RichTextContentFolder(isEditable())));

        main.setH1(row++, 0, 1, proto().banner().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().banner(), new PortalImageResourceFolder(isEditable())));

        main.setH1(row++, 0, 1, proto().socialLinks().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().socialLinks(), new SocialLinkFolder(isEditable())));

        main.setH1(row++, 0, 1, proto().childPages().getMeta().getCaption());
        main.setWidget(row++, 0, inject(proto().childPages(), new SitePageDescriptorFolder(this)));

        return new CrmScrollPanel(main);
    }

    private Widget createTestimonialsTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().testimonials(), new TestimonialFolder(isEditable())));

        return new ScrollPanel(main);
    }

    private Widget createNewsTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().news(), new NewsFolder(isEditable())));

        return new ScrollPanel(main);
    }

// TODO
//    private class ColorPickerDialog extends DialogBox {
//
//        private final ColorPicker picker;
//
//        private Integer colorselected;
//
//        public ColorPickerDialog() {
//            setText("Choose a color");
//
//            // Define the panels
//            VerticalPanel panel = new VerticalPanel();
//            FlowPanel okcancel = new FlowPanel();
//            picker = new ColorPicker();
//
//            // Define the buttons
//            Button preview = new Button("Preview");
//            preview.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent sender) {
//                    colorPreview(picker.getHexColor());
//                }
//            });
//
//            Button ok = new Button("Ok"); // ok button
//            ok.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent sender) {
//                    colorSelected(picker.getHexColor());
//                    ColorPickerDialog.this.hide();
//                }
//            });
//
//            Button cancel = new Button("Cancel"); // cancel button
//            cancel.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent sender) {
//                    cancel();
//                    ColorPickerDialog.this.hide();
//                }
//            });
//            okcancel.add(preview);
//            okcancel.add(ok);
//            okcancel.add(cancel);
//
//            // Put it together
//            panel.add(picker);
//            panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//            panel.add(okcancel);
//
//            setWidget(panel);
//        }
//
//        public void showNear(Widget sender) {
//            int left = sender.getAbsoluteLeft() + 30;
//            int top = sender.getAbsoluteTop() + 10;
//            this.setPopupPosition(left, top);
//            this.show();
//        }
//
//        public Integer colorSelected() {
//            return colorselected;
//        }
//
//        private void colorSelected(Integer color) {
//            colorselected = color;
//        }
//
//        private void cancel() {
//            colorselected = -1;
//        }
//    }
}