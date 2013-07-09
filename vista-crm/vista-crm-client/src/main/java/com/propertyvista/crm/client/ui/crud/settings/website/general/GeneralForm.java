/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.general;

import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.site.ResidentPortalSettings;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.dto.SiteDescriptorDTO;

public class GeneralForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(GeneralForm.class);

    private final CCheckBox publicPortalSwitch = new CCheckBox();

    private final CCheckBox residentSkinSwitch = new CCheckBox();

    private final ResidentCustomContentFolder contentFolder = new ResidentCustomContentFolder(isEditable());

    public GeneralForm(IForm<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = 0;

        content.setH1(row++, 0, 2, i18n.tr("Web Skin"));

        CComponent<?> skinComp;
        content.setWidget(row++, 0, new DecoratorBuilder(skinComp = inject(proto().skin()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object1()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object2()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast1()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast2()), 10).build());
        if (skinComp instanceof CComboBox) {
            ((CComboBox<Skin>) skinComp).setOptions(EnumSet.of(Skin.skin2, Skin.skin3, Skin.skin4, Skin.skin5, Skin.skin6));
        }

        // ---------------------------------------------------------------------------------------------------------------

        content.setH1(row++, 0, 2, i18n.tr("Website"));
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().enabled(), publicPortalSwitch), 10).build());
        publicPortalSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Boolean enable = event.getValue();
                // enable skin switch when public portal is disabled
                residentSkinSwitch.setEnabled(!enable);
                // if public portal is turning on while resident portal is on and custom skin is enabled, display warning
                if (enable != null && enable) {
                    ResidentPortalSettings residentSettings = GeneralForm.this.getValue().isEmpty() ? null : GeneralForm.this.getValue()
                            .residentPortalSettings();
                    if (residentSettings != null && residentSettings.enabled().isBooleanTrue() && residentSettings.useCustomHtml().isBooleanTrue()) {
                        OkCancelDialog confirm = new OkCancelDialog("Please Confirm") {
                            @Override
                            public boolean onClickOk() {
                                residentSkinSwitch.setValue(false);
                                return true;
                            }

                            @Override
                            public boolean onClickCancel() {
                                publicPortalSwitch.setValue(false);
                                return true;
                            }

                        };
                        confirm.setBody(new HTML(i18n.tr("This will turn off Resident Portal Custom Skin!")));
                        confirm.show();
                    }
                }
            }
        });
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().disableMapView()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().disableBuildingDetails()), 10).build());

        // --------------------------------------------------------------------------------------------------------------------

        content.setH1(row++, 0, 2, i18n.tr("Resident Portal"));
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().residentPortalSettings().enabled()), 10).build());
        residentSkinSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Boolean customSkin = event.getValue();
                contentFolder.setEnabled(customSkin != null && customSkin);
            }
        });
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().residentPortalSettings().useCustomHtml(), residentSkinSwitch), 10).build());

        content.setH3(row++, 0, 2, i18n.tr("Resident Portal Custom Content"));
        content.setWidget(row++, 0, inject(proto().residentPortalSettings().customHtml(), contentFolder));
        selectTab(addTab(content));

        // =====================================================================================================================

        content = new FormFlexPanel(proto().locales().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().locales(), new AvailableLocaleFolder(isEditable())));
        addTab(content);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // resident portal skin dependencies:
        // 1. if public portal enabled, resident skin selection is disabled
        residentSkinSwitch.setEnabled(!getValue().enabled().isBooleanTrue());
        // 2. enable content folder on useCustomSkin
        contentFolder.setEnabled(getValue().residentPortalSettings().useCustomHtml().isBooleanTrue());
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