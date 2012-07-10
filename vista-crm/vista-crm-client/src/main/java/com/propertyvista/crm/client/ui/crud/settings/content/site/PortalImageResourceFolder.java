/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author dev_vista
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.domain.File;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalImageResource;
import com.propertyvista.domain.site.SiteImageResource;

public class PortalImageResourceFolder extends VistaBoxFolder<PortalImageResource> {
    private static final I18n i18n = I18n.get(PortalImageResourceFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public PortalImageResourceFolder(boolean editable) {
        super(PortalImageResource.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PortalImageResource>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PortalImageResource>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PortalImageResource items : getValue()) {
            usedLocales.add(items.locale());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        updateUsedLocales();
    }

    @Override
    protected void addItem() {
        new AvailableLocaleSelectorDialog(usedLocales, new ValueChangeHandler<AvailableLocale>() {
            @Override
            public void onValueChange(ValueChangeEvent<AvailableLocale> event) {
                PortalImageResource item = EntityFactory.create(PortalImageResource.class);
                item.locale().set(event.getValue());
                PortalImageResourceFolder.super.addItem(item);
            }
        }).show();
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PortalImageResource) {
            return new PortalImageResourceEditor();
        }
        return super.create(member);
    }

    class PortalImageResourceEditor extends CEntityDecoratableForm<PortalImageResource> {

        private final SiteImageThumbnail thumb = new SiteImageThumbnail();

        public PortalImageResourceEditor() {
            super(PortalImageResource.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale(), locale), 10).build());
            if (PortalImageResourceFolder.this.isEditable()) {

                CEntityHyperlink<File> link = new CEntityHyperlink<File>(new Command() {
                    @Override
                    public void execute() {
                        SiteImageResourceProvider provider = new SiteImageResourceProvider();
                        provider.selectResource(new AsyncCallback<SiteImageResource>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                MessageDialog.error(i18n.tr("Action Failed"), caught.getMessage());
                            }

                            @Override
                            public void onSuccess(SiteImageResource rc) {
                                getValue().imageResource().set(rc);
                                thumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().imageResource()));
                            }
                        });
                    }
                });
                link.setFormat(new IFormat<File>() {
                    @Override
                    public String format(File value) {
                        if (value.blobKey().isNull()) {
                            return i18n.tr("Add Image");
                        } else {
                            return i18n.tr("Change Image");
                        }
                    }

                    @Override
                    public File parse(String string) {
                        return null;
                    }
                });
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().imageResource(), link), 10).build());
            } else {
                CEntityHyperlink<File> link = new CEntityHyperlink<File>(new Command() {
                    @Override
                    public void execute() {
                        OkDialog dialog = new OkDialog(getValue().imageResource().fileName().getValue()) {
                            @Override
                            public boolean onClickOk() {
                                return true;
                            }
                        };
                        dialog.setBody(new Image(MediaUtils.createSiteImageResourceUrl(getValue().imageResource())));
                        dialog.center();
                    }
                });
                link.setFormat(new IFormat<File>() {
                    @Override
                    public String format(File value) {
                        if (value.blobKey().isNull()) {
                            return i18n.tr("Not set");
                        } else {
                            return value.fileName().getStringView();
                        }
                    }

                    @Override
                    public File parse(String string) {
                        return null;
                    }
                });
                main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().imageResource(), link), 10).build());
            }
            main.setWidget(0, 1, thumb);
            main.getFlexCellFormatter().setRowSpan(0, 1, row + 1);

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            thumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().imageResource()));
        }
    }

    class SiteImageThumbnail extends Image {
        private double thumbSize = 80;

        public SiteImageThumbnail() {
        }

        public SiteImageThumbnail(double size) {
            thumbSize = size;
        }

        @Override
        public void setUrl(String url) {
            super.setUrl(url);
            if (getWidth() > 0 && getHeight() > 0) {
                scaleToFit();
            } else {
                setVisible(false);
                addLoadHandler(new LoadHandler() {
                    @Override
                    public void onLoad(LoadEvent event) {
                        scaleToFit();
                        setVisible(true);
                    }
                });
            }
        }

        private void scaleToFit() {
            if (1.0 * getWidth() / getHeight() > 1) {
                setWidth(thumbSize + "px");
            } else {
                setHeight(thumbSize + "px");
            }
        }

    }
}
