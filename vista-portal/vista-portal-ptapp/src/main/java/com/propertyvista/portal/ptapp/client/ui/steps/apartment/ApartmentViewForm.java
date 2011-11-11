/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CAddressStructured;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityDecoratableEditor<ApartmentInfoDTO> {

    static I18n i18n = I18n.get(ApartmentViewForm.class);

    private final FormFlexPanel consessionPanel = new FormFlexPanel();

    private final FormFlexPanel chargedPanel = new FormFlexPanel();

    private final FormFlexPanel petsPanel = new FormFlexPanel();

    private final FormFlexPanel parkingPanel = new FormFlexPanel();

    private final FormFlexPanel storagePanel = new FormFlexPanel();

    private final FormFlexPanel otherPanel = new FormFlexPanel();

    private final SimplePanel pictureHolder = new SimplePanel();

    public ApartmentViewForm() {
        super(ApartmentInfoDTO.class, new VistaViewersComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("General Info"));

        FormFlexPanel info = new FormFlexPanel();

        int row1 = -1;
        info.setWidget(++row1, 0, new DecoratorBuilder(inject(proto().floorplan()), 20).build());

        info.setWidget(++row1, 0, new DecoratorBuilder(inject(proto().bedrooms()), 10).build());

        row1 = 0;
        info.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().bathrooms()), 10).build());

        info.getColumnFormatter().setWidth(0, "50%");
        info.getColumnFormatter().setWidth(1, "50%");

        main.setWidget(++row, 0, info);

        main.setHR(++row, 0, 1);

        FormFlexPanel apartment = new FormFlexPanel();

        apartment.setWidget(0, 0, inject(proto().address(), new CAddressStructured(false)));
        apartment.setWidget(0, 1, pictureHolder);

        info.getColumnFormatter().setWidth(0, "40%");
        info.getColumnFormatter().setWidth(1, "60%");

        main.setWidget(++row, 0, apartment);

        main.setH1(++row, 0, 1, i18n.tr("Lease Terms"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 8).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTo()), 8).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitRent()), 8).build());

        main.setH1(++row, 0, 1, i18n.tr("Promotions, Discounts and Concessions"));
        consessionPanel.setWidget(0, 0, inject(proto().concessions(), new ConcessionsFolder()));
        main.setWidget(++row, 0, consessionPanel);

        main.setH1(++row, 0, 1, i18n.tr("Included Utilities"));
        main.setWidget(++row, 0, inject(proto().includedUtilities(), new UtilityFolder()));

        main.setH1(++row, 0, 1, i18n.tr("Excluded Utilities"));
        main.setWidget(++row, 0, inject(proto().externalUtilities(), new UtilityFolder()));

        main.setH1(++row, 0, 1, i18n.tr("Billed Utilities"));
        chargedPanel.setWidget(0, 0, inject(proto().agreedUtilities(), new FeatureFolder(Feature.Type.utility, this, false)));
        main.setWidget(++row, 0, chargedPanel);

        main.setH1(++row, 0, 1, i18n.tr("Add-Ons"));

        main.setH2(++row, 0, 1, i18n.tr("Pets"));
        petsPanel.setWidget(0, 0, inject(proto().agreedPets(), new FeatureExFolder(true, Feature.Type.pet, this)));
        main.setWidget(++row, 0, petsPanel);

        main.setH2(++row, 0, 1, i18n.tr("Parking"));
        parkingPanel.setWidget(0, 0, inject(proto().agreedParking(), new FeatureExFolder(true, Feature.Type.parking, this)));
        main.setWidget(++row, 0, parkingPanel);

        main.setH2(++row, 0, 1, i18n.tr("Storage"));
        storagePanel.setWidget(0, 0, inject(proto().agreedStorage(), new FeatureFolder(Feature.Type.locker, this, true)));
        main.setWidget(++row, 0, storagePanel);

        main.setH2(++row, 0, 1, i18n.tr("Other"));
        otherPanel.setWidget(0, 0, inject(proto().agreedOther(), new FeatureFolder(Feature.Type.addOn, this, true)));
        main.setWidget(++row, 0, otherPanel);

        return main;
    }

    @Override
    public void populate(ApartmentInfoDTO value) {
        super.populate(value);

        pictureHolder.setWidget(MediaUtils.createPublicMediaImage(value.picture().getPrimaryKey(), ThumbnailSize.large));

        //hide/show various panels depend on populated data:
        consessionPanel.setVisible(!value.concessions().isEmpty());
        chargedPanel.setVisible(!value.agreedUtilities().isEmpty());

        petsPanel.setVisible(!value.agreedPets().isEmpty() || !value.availablePets().isEmpty());
        parkingPanel.setVisible(!value.agreedParking().isEmpty() || !value.availableParking().isEmpty());
        storagePanel.setVisible(!value.agreedStorage().isEmpty() || !value.availableStorage().isEmpty());
        otherPanel.setVisible(!value.agreedOther().isEmpty() || !value.availableOther().isEmpty());
    }
}
