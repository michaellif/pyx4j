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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.CrmEditorsComponentFactory;
import com.propertyvista.crm.client.ui.components.media.CrmMediaFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.rpc.portal.ImageConsts.ImageTarget;

public class FloorplanEditorForm extends CrmEntityForm<FloorplanDTO> {

    public FloorplanEditorForm() {
        this(new CrmEditorsComponentFactory());
    }

    public FloorplanEditorForm(IEditableComponentFactory factory) {
        super(FloorplanDTO.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().marketingName()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 30).build());
        main.getFlexCellFormatter().setRowSpan(row, 0, 3);

        row += 2; // leave space for right column items...
        main.setHeader(++row, 0, 2, proto().amenities().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().amenities(), createAmenitiesListEditor()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setHeader(++row, 0, 2, i18n.tr("Media"));
        main.setWidget(++row, 0, inject(proto().media(), new CrmMediaFolder(isEditable(), ImageTarget.Floorplan)));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        row = -1;
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().floorCount()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().bedrooms()), 3).build());
        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().dens()), 3).build());
        // shift one column left because description field RowSpan:
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bathrooms()), 3).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().halfBath()), 3).build());

        main.getColumnFormatter().setWidth(0, "60%");
        main.getColumnFormatter().setWidth(1, "40%");

        return new CrmScrollPanel(main);
    }

    private CEntityFolder<FloorplanAmenity> createAmenitiesListEditor() {
        return new VistaTableFolder<FloorplanAmenity>(FloorplanAmenity.class, i18n.tr("Amenity"), isEditable()) {
            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().description(), "30em"));
                return columns;
            }
        };
    }
}