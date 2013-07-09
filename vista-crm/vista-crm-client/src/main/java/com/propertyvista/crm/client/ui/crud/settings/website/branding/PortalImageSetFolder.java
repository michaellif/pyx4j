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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IFile;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.CImage.Type;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.settings.website.general.AvailableLocaleSelectorDialog;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PortalImageSet;
import com.propertyvista.domain.site.SiteImageResource;

public class PortalImageSetFolder extends VistaBoxFolder<PortalImageSet> {
    private static final I18n i18n = I18n.get(PortalImageSetFolder.class);

    private final Set<AvailableLocale> usedLocales = new HashSet<AvailableLocale>();

    public PortalImageSetFolder(boolean editable) {
        super(PortalImageSet.class, editable);
        this.addValueChangeHandler(new ValueChangeHandler<IList<PortalImageSet>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<PortalImageSet>> event) {
                updateUsedLocales();
            }
        });
    }

    private void updateUsedLocales() {
        usedLocales.clear();
        for (PortalImageSet items : getValue()) {
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
                PortalImageSet item = EntityFactory.create(PortalImageSet.class);
                item.locale().set(event.getValue());
                PortalImageSetFolder.super.addItem(item);
            }
        }).show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PortalImageSet) {
            return new PortalImageSetEditor();
        }
        return super.create(member);
    }

    class PortalImageSetEditor extends CEntityDecoratableForm<PortalImageSet> {

        public PortalImageSetEditor() {
            super(PortalImageSet.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            CEntityLabel<AvailableLocale> locale = new CEntityLabel<AvailableLocale>();
            locale.setEditable(false);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().locale(), locale), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().imageSet(), new CImage<SiteImageResource>(Type.multiple) {

                @Override
                public String getImageUrl(SiteImageResource file) {
                    return MediaUtils.createSiteImageResourceUrl(file);
                }

                @Override
                public SiteImageResource getNewValue(IFile file) {
                    return null;
                }
            }), 20).build());

            return main;
        }
    }

}
