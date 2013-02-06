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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.CRadioGroupBoolean;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup.Layout;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.settings.content.page.CityIntroPageFolder;
import com.propertyvista.crm.client.ui.crud.settings.content.site.PortalImageResourceFolder.SiteImageThumbnail;
import com.propertyvista.domain.File;
import com.propertyvista.domain.site.ResidentPortalSettings;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.domain.site.SiteImageResource;
import com.propertyvista.domain.site.gadgets.HomePageGadget;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteForm extends CrmEntityForm<SiteDescriptorDTO> {

    private static final I18n i18n = I18n.get(SiteForm.class);

    private final CCheckBox publicPortalSwitch = new CCheckBox();

    private final CRadioGroupBoolean residentSkinSwitch = new CRadioGroupBoolean(Layout.VERTICAL);

    private final SiteImageThumbnail thumb = new SiteImageThumbnail();

    public SiteForm(IFormView<SiteDescriptorDTO> view) {
        super(SiteDescriptorDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = 0;
        content.setH1(row++, 0, 2, i18n.tr("Website"));
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().enabled(), publicPortalSwitch), 10).build());
        publicPortalSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Boolean enable = event.getValue();
                // if public portal is turning on while resident portal is on and custom skin is enabled, display warning
                if (enable != null && enable) {
                    ResidentPortalSettings residentSettings = SiteForm.this.getValue().isEmpty() ? null : SiteForm.this.getValue().residentPortalSettings();
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
        CComponent<?, ?> skinComp;
        content.setWidget(row++, 0, new DecoratorBuilder(skinComp = inject(proto().skin()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object1()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().object2()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast1()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().sitePalette().contrast2()), 10).build());

        if (skinComp instanceof CComboBox) {
            ((CComboBox<Skin>) skinComp).setOptions(EnumSet.of(Skin.skin2, Skin.skin3, Skin.skin4, Skin.skin5, Skin.skin6));
        }
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().disableMapView()), 10).build());
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().disableBuildingDetails()), 10).build());

        content.setH1(row++, 0, 2, i18n.tr("Resident Portal"));
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().residentPortalSettings().enabled()), 10).build());
        residentSkinSwitch.setFormat(new IFormat<Boolean>() {
            @Override
            public String format(Boolean value) {
                if (value == null) {
                    return format(false);
                } else if (value) {
                    return i18n.tr("Custom");
                } else {
                    return i18n.tr("Same as Website");
                }
            }

            @Override
            public Boolean parse(String string) {
                return null;
            }
        });
        residentSkinSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Boolean customSkin = event.getValue();
                // if custom resident skin is turning on while public portal is enabled, display warning
                if (customSkin != null && customSkin) {
                    SiteDescriptorDTO site = SiteForm.this.getValue() == null ? null : SiteForm.this.getValue();
                    if (site != null && site.enabled().isBooleanTrue() && site.residentPortalSettings().enabled().isBooleanTrue()) {
                        OkCancelDialog confirm = new OkCancelDialog("Please Confirm") {
                            @Override
                            public boolean onClickOk() {
                                publicPortalSwitch.setValue(false);
                                return true;
                            }

                            @Override
                            public boolean onClickCancel() {
                                residentSkinSwitch.setValue(false);
                                return true;
                            }

                        };
                        confirm.setBody(new HTML(i18n.tr("This will turn off Public Website!")));
                        confirm.show();
                    }
                }
            }
        });
        content.setWidget(row++, 0, new DecoratorBuilder(inject(proto().residentPortalSettings().useCustomHtml(), residentSkinSwitch), 10).build());
        content.setH3(row++, 0, 2, i18n.tr("Resident Portal Custom Content"));
        content.setWidget(row++, 0, inject(proto().residentPortalSettings().customHtml(), new ResidentCustomContentFolder(isEditable())));
        selectTab(addTab(content));

        content = new FormFlexPanel(proto().locales().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().locales(), new AvailableLocaleFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().siteTitles().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().siteTitles(), new SiteTitlesFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(i18n.tr("Site Logos"));
        content.setWidget(0, 0, inject(proto().logo(), new PortalImageResourceFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().slogan().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().slogan(), new RichTextContentFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().banner().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().banner(), new PortalImageSetFolder(isEditable())));
        addTab(content);

        // home page gadgets
        content = new FormFlexPanel(i18n.tr("Home Page Gadgets"));
        content.setWidget(0, 0, createGadgetPanel());
        addTab(content);

        content = new FormFlexPanel(proto().socialLinks().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().socialLinks(), new SocialLinkFolder(isEditable())));
        addTab(content);

        content = new FormFlexPanel(proto().childPages().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().childPages(), new SitePageDescriptorFolder(this)));
        addTab(content);

        content = new FormFlexPanel(proto().cityIntroPages().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().cityIntroPages(), new CityIntroPageFolder(this)));
        addTab(content);

        content = new FormFlexPanel(proto().crmLogo().getMeta().getCaption());
        HorizontalPanel imageLinkContainer = new HorizontalPanel();
        imageLinkContainer.setWidth("100%");
        content.setWidget(row++, 0, imageLinkContainer);
        imageLinkContainer.add(inject(proto().crmLogo(), new CFile<File>(new Command() {
            @Override
            public void execute() {
                OkDialog dialog = new OkDialog(getValue().crmLogo().fileName().getValue()) {
                    @Override
                    public boolean onClickOk() {
                        return true;
                    }
                };
                dialog.setBody(new Image(MediaUtils.createSiteImageResourceUrl(getValue().crmLogo())));
                dialog.center();
            }
        }) {
            @Override
            public void showFileSelectionDialog() {
                SiteImageResourceProvider provider = new SiteImageResourceProvider();
                provider.selectResource(new AsyncCallback<SiteImageResource>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MessageDialog.error(i18n.tr("Action Failed"), caught.getMessage());
                    }

                    @Override
                    public void onSuccess(SiteImageResource rc) {
                        setValue(rc);
                        thumb.setUrl(MediaUtils.createSiteImageResourceUrl(rc));
                    }
                });
            }
        }));
        imageLinkContainer.add(thumb);
        imageLinkContainer.setCellVerticalAlignment(imageLinkContainer.getWidget(0), HasVerticalAlignment.ALIGN_MIDDLE);
        imageLinkContainer.getWidget(0).setWidth("400px");
        imageLinkContainer.setCellWidth(thumb, "200px");
        addTab(content);

        content = new FormFlexPanel(proto().metaTags().getMeta().getCaption());
        content.setWidget(0, 0, inject(proto().metaTags(), new MetaTagsFolder(isEditable())));
        addTab(content);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // resident portal skin dependencies:
        // 1. if public portal enabled, resident skin selection is disabled
        residentSkinSwitch.setEnabled(!getValue().enabled().isBooleanTrue());

        thumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().crmLogo()));
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