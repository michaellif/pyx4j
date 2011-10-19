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


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.client.ui.decorations.CrmSectionSeparator;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteEditorForm extends CrmEntityForm<SiteDescriptorDTO> {

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);

    public SiteEditorForm(IFormView<SiteDescriptorDTO> parentView) {
        this(parentView, new CrmEditorsComponentFactory());
    }

    public SiteEditorForm(IFormView<SiteDescriptorDTO> parentView, IEditableComponentFactory factory) {
        super(SiteDescriptorDTO.class, factory);
        setParentView(parentView);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createTestimonialsTab(), i18n.tr("Testimonials"));
        tabPanel.add(createNewsTab(), i18n.tr("News"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    public IsWidget createGeneralTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();

        main.add(inject(proto().skin()), 10);
        main.add(inject(proto().baseColor()), 10);
        main.add(inject(proto().copyright()), 25);

        main.add(new CrmSectionSeparator(proto().locales().getMeta().getCaption()));
        main.add(inject(proto().locales(), new AvailableLocaleFolder()));

// TODO: image lists uploaders:
//        main.add(inject(proto().logo(), new CFileUploader()), 60);
//        main.add(inject(proto().slogan(), new CFileUploader()), 60);
//        main.add(inject(proto().images(), new CFileUploader()), 60);

        main.add(new CrmSectionSeparator(proto().childPages().getMeta().getCaption()));
        main.add(inject(proto().childPages(), new SitePageDescriptorFolder(this, (SiteViewer) getParentView())));

        return new CrmScrollPanel(main);
    }

    private Widget createTestimonialsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().testimonials(), new TestimonialFolder()));
        return new ScrollPanel(main);
    }

    private Widget createNewsTab() {
        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel();
        main.add(inject(proto().news(), new NewsFolder()));
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