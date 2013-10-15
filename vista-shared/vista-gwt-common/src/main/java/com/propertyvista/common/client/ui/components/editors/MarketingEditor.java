/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 21, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.marketing.MarketingContact;
import com.propertyvista.domain.marketing.MarketingContactEmail;
import com.propertyvista.domain.marketing.MarketingContactPhone;
import com.propertyvista.domain.marketing.MarketingContactUrl;
import com.propertyvista.domain.marketing.ils.ILSOpenHouse;
import com.propertyvista.shared.config.VistaFeatures;

public class MarketingEditor extends CEntityDecoratableForm<Marketing> {

    public MarketingEditor() {
        super(Marketing.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        main.setWidth("100%");

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().name()), 16).build());
        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().name()).setEditable(false);
        }
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().visibility()), 10).build());

        main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());

        main.setH1(++row, 0, 2, proto().marketingAddress().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().marketingAddress(), new AddressStructuredEditor(false)));

        main.setH1(++row, 0, 2, proto().marketingContacts().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().marketingContacts().url(), new MarketingContactEditor<MarketingContactUrl>(MarketingContactUrl.class)));
        main.setWidget(++row, 0, 2, inject(proto().marketingContacts().email(), new MarketingContactEditor<MarketingContactEmail>(MarketingContactEmail.class)));
        main.setWidget(++row, 0, 2, inject(proto().marketingContacts().phone(), new MarketingContactEditor<MarketingContactPhone>(MarketingContactPhone.class)));

        main.setH1(++row, 0, 2, proto().adBlurbs().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().adBlurbs(), new VistaTableFolder<AdvertisingBlurb>(AdvertisingBlurb.class, isEditable()) {
            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                List<EntityFolderColumnDescriptor> columns;
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().content(), "60em"));
                return columns;
            }

            @Override
            protected IFolderDecorator<AdvertisingBlurb> createFolderDecorator() {
                TableFolderDecorator<AdvertisingBlurb> decor = (TableFolderDecorator<AdvertisingBlurb>) super.createFolderDecorator();
                decor.setShowHeader(false);
                return decor;
            }
        }));

        main.setH1(++row, 0, 2, proto().openHouseSchedule().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().openHouseSchedule(), new OpenHouseScheduleFolder()));

        return main;
    }

    public static class MarketingContactEditor<T extends MarketingContact> extends CEntityDecoratableForm<T> {
        public MarketingContactEditor(Class<T> valueClass) {
            super(valueClass);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().value())).build());
            content.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().description())).build());

            return content;
        }
    }

    private class OpenHouseScheduleFolder extends VistaBoxFolder<ILSOpenHouse> {

        public OpenHouseScheduleFolder() {
            super(ILSOpenHouse.class);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof ILSOpenHouse) {
                return new ILSOpenHouseEditor();
            } else {
                return super.create(member);
            }
        }

        private class ILSOpenHouseEditor extends CEntityDecoratableForm<ILSOpenHouse> {
            public ILSOpenHouseEditor() {
                super(ILSOpenHouse.class);
            }

            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().eventDate())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().startTime())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().endTime())).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().appointmentRequired())).build());

                content.setWidget(0, 1, new FormDecoratorBuilder(inject(proto().details())).build());
                content.getFlexCellFormatter().setRowSpan(0, 1, row + 1);
                content.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

                return content;
            }
        }
    }
}
