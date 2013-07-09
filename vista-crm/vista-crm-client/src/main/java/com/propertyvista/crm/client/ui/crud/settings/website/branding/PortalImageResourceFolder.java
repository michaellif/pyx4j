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
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.website.branding;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CFile;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.components.cms.SiteImageResourceProvider;
import com.propertyvista.crm.client.ui.crud.settings.website.SiteImageThumbnail;
import com.propertyvista.crm.client.ui.crud.settings.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.domain.File;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalLogoImageResource;
import com.propertyvista.domain.site.SiteImageResource;

public class PortalImageResourceFolder extends VistaBoxFolder<PortalLogoImageResource> {
    private static final I18n i18n = I18n.get(PortalImageResourceFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public PortalImageResourceFolder(boolean editable) {
        super(PortalLogoImageResource.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PortalLogoImageResource>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PortalLogoImageResource>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PortalLogoImageResource items : getValue()) {
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
                PortalLogoImageResource item = EntityFactory.create(PortalLogoImageResource.class);
                item.locale().set(event.getValue());
                PortalImageResourceFolder.super.addItem(item);
            }
        }).show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PortalLogoImageResource) {
            return new PortalImageResourceEditor();
        }
        return super.create(member);
    }

    class PortalImageResourceEditor extends CEntityDecoratableForm<PortalLogoImageResource> {

        private final SiteImageThumbnail smallThumb = new SiteImageThumbnail(60);

        private final SiteImageThumbnail largeThumb = new SiteImageThumbnail(180);

        public PortalImageResourceEditor() {
            super(PortalLogoImageResource.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locale(), locale), 10).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().small(), new LogoLink(smallThumb)), 20).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().large(), new LogoLink(largeThumb)), 20).build());

            HorizontalPanel thumbsPanel = new HorizontalPanel();
            thumbsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            thumbsPanel.add(smallThumb);
            thumbsPanel.add(largeThumb);
            main.setWidget(0, 1, thumbsPanel);
            main.getFlexCellFormatter().setRowSpan(0, 1, row + 1);

            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            smallThumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().small()));
            largeThumb.setUrl(MediaUtils.createSiteImageResourceUrl(getValue().large()));
        }

        class LogoLink extends CFile<File> {

            private SiteImageThumbnail thumb;

            public LogoLink(SiteImageThumbnail thumb) {
                setCommand(new Command() {
                    @Override
                    public void execute() {
                        OkDialog dialog = new OkDialog(getValue().fileName().getValue()) {
                            @Override
                            public boolean onClickOk() {
                                return true;
                            }
                        };
                        dialog.setBody(new Image(MediaUtils.createSiteImageResourceUrl((SiteImageResource) getValue())));
                        dialog.center();
                    }
                });

                this.thumb = thumb;
            }

            @Override
            public void showFileSelectionDialog() {
                new SiteImageResourceProvider().selectResource(new AsyncCallback<SiteImageResource>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MessageDialog.error(i18n.tr("Action Failed"), caught.getMessage());
                    }

                    @Override
                    public void onSuccess(SiteImageResource rc) {
                        if (rc != null) {
                            setValue(rc);
                            thumb.setUrl(MediaUtils.createSiteImageResourceUrl(rc));
                        }
                    }
                });
            }
        }

    }
}
