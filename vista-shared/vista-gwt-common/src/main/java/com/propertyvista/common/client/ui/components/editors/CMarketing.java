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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.marketing.AdvertisingBlurb;
import com.propertyvista.domain.marketing.Marketing;

public class CMarketing extends CDecoratableEntityEditor<Marketing> {

    public CMarketing() {
        super(Marketing.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();
        main.setWidth("100%");

        int row = -1;
        main.setWidget(++row, 0, decorate(inject(proto().name()), 30));
        main.setWidget(++row, 0, decorate(inject(proto().description()), 30));

        main.setHeader(++row, 0, 2, proto().adBlurbs().getMeta().getCaption());
        main.setWidget(++row, 0,
                inject(proto().adBlurbs(), new VistaTableFolder<AdvertisingBlurb>(AdvertisingBlurb.class, i18n.tr("Advertising Blurb"), isEditable()) {
                    @Override
                    protected List<EntityFolderColumnDescriptor> columns() {
                        List<EntityFolderColumnDescriptor> columns;
                        columns = new ArrayList<EntityFolderColumnDescriptor>();
                        columns.add(new EntityFolderColumnDescriptor(proto().content(), "60em"));
                        return columns;
                    }

                    @Override
                    protected IFolderDecorator<AdvertisingBlurb> createDecorator() {
                        TableFolderDecorator<AdvertisingBlurb> decor = (TableFolderDecorator<AdvertisingBlurb>) super.createDecorator();
                        decor.setShowHeader(false);
                        return decor;
                    }
                }));

        return main;
    }
}
