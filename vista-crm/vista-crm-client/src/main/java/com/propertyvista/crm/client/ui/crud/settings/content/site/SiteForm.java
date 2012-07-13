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

import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(SiteForm.class);

    public SiteForm() {
        this(false);
    }

    public SiteForm(boolean viewMode) {
        super(SiteDescriptorDTO.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = 0;

        content.setH1(row++, 0, 1, i18n.tr("Look And Feel"));
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().enabled()), 10).build());

        CComponent<?, ?> skinComp;
        content.setWidget(row++, 0, new DecoratorBuilder(skinComp = inject(proto().skin()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object1()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object2()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast1()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast2()), 10).build());

        if (skinComp instanceof CComboBox) {
            Collection<SiteDescriptor.Skin> skinOpt;
            if (ApplicationMode.isDevelopment()) {
                skinOpt = EnumSet.of(SiteDescriptor.Skin.skin1, SiteDescriptor.Skin.skin2, SiteDescriptor.Skin.skin3, SiteDescriptor.Skin.skin4);
            } else {
                skinOpt = EnumSet.of(SiteDescriptor.Skin.skin2, SiteDescriptor.Skin.skin3, SiteDescriptor.Skin.skin4);
            }
            ((CComboBox<SiteDescriptor.Skin>) skinComp).setOptions(skinOpt);
        }
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().disableMapView()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().disableBuildingDetails()), 10).build());

        content.setH1(row++, 0, 1, proto().locales().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().locales(), new AvailableLocaleFolder(isEditable())));

        content.setH1(row++, 0, 1, proto().siteTitles().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().siteTitles(), new SiteTitlesFolder(isEditable())));

        content.setH1(row++, 0, 1, proto().logo().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().logo(), new PortalImageResourceFolder(isEditable())));

        content.setH1(row++, 0, 1, proto().slogan().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().slogan(), new RichTextContentFolder(isEditable())));

        content.setH1(row++, 0, 1, proto().banner().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().banner(), new PortalImageResourceFolder(isEditable())));

        content.setH1(row++, 0, 1, proto().socialLinks().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().socialLinks(), new SocialLinkFolder(isEditable())));

        // home page gadgets
        content.setWidget(row++, 0, createGadgetPanel());

        content.setH1(row++, 0, 1, proto().childPages().getMeta().getCaption());
        content.setWidget(row++, 0, inject(proto().childPages(), new SitePageDescriptorFolder(this)));

        selectTab(addTab(content));

    }

    class GadgetSelectorDialog extends SelectEnumDialog<HomePageGadget.GadgetType> {
        public GadgetSelectorDialog() {
            super(i18n.tr("Select Gadget Type"), EnumSet.allOf(HomePageGadget.GadgetType.class));
        }

        @Override
        public boolean onClickOk() {
            HomePageGadget.GadgetType type = getSelectedType();
            if (type == null) {
                return false;
            }

            HomePageGadget newItem = EntityFactory.create(HomePageGadget.class);
            newItem.type().setValue(type);
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(HomePageGadget.class).formNewItemPlace(newItem));
            return true;
        }
    }

    private Widget createGadgetPanel() {
        FormFlexPanel gadgetPanel = new FormFlexPanel();
        int row = 0;

        Widget addNewItem = null;
        if (isEditable()) {
            addNewItem = new HTML();
        } else {
            Anchor addNewItemLink = new Anchor(i18n.tr("Add New Gadget"));
            addNewItemLink.getElement().getStyle().setProperty("lineHeight", "2em");
            addNewItemLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    new GadgetSelectorDialog().show();
                }
            });
            addNewItem = addNewItemLink;
        }
        gadgetPanel.setH1(row++, 0, 2, i18n.tr("Home Page Gadgets"), addNewItem);
        gadgetPanel.setWidget(row, 0, inject(proto().homePageGadgetsNarrow(), new HomePageGadgetFolder(isEditable())));
        gadgetPanel.setWidget(row, 1, inject(proto().homePageGadgetsWide(), new HomePageGadgetFolder(isEditable())));
        gadgetPanel.getRowFormatter().setVerticalAlign(row++, HasVerticalAlignment.ALIGN_TOP);
        return gadgetPanel;
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