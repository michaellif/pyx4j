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
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.media.CrmMediaFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class FloorplanForm extends CrmEntityForm<FloorplanDTO> {

    private static final I18n i18n = I18n.get(FloorplanForm.class);

    public FloorplanForm(IForm<FloorplanDTO> view) {
        super(FloorplanDTO.class, view);

        Tab tab = addTab(createGeneralTab(i18n.tr("General")));
        selectTab(tab);

        addTab(createMediaTab(i18n.tr("Media")));

    }

    private CEntityFolder<FloorplanAmenity> createAmenitiesListEditor() {
        return new VistaTableFolder<FloorplanAmenity>(FloorplanAmenity.class, isEditable()) {
            @Override
            public List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "45em"));
                return columns;
            }
        };
    }

    private FormFlexPanel createMediaTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Media Information"));
        main.setWidget(++row, 0, inject(proto().media(), new CrmMediaFolder(isEditable(), ImageTarget.Floorplan)));
        return main;
    }

    private FormFlexPanel createGeneralTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Floorplan Information"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().marketingName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 30).build());
        main.getFlexCellFormatter().setRowSpan(row, 0, 3);

        row += 4; // leave space for right column items...
        main.setH1(++row, 0, 2, proto().amenities().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().amenities(), createAmenitiesListEditor()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = 0;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().floorCount()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().bedrooms()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().dens()), 3).build());

        // shift one column left because description field RowSpan:
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bathrooms()), 3).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().halfBath()), 3).build());

        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().area()), 8).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().areaUnits()), 8).build());

        main.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        return main;
    }
}