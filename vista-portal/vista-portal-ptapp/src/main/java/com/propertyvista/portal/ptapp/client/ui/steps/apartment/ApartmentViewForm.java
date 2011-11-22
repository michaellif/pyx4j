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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.editors.CEntityDecoratableEditor;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityDecoratableEditor<ApartmentInfoDTO> {

    static I18n i18n = I18n.get(ApartmentViewForm.class);

    private final FormFlexPanel consessionPanel = new FormFlexPanel();

    private final FormFlexPanel includedPanel = new FormFlexPanel();

    private final FormFlexPanel excludedPanel = new FormFlexPanel();

    private final FormFlexPanel chargedPanel = new FormFlexPanel();

    private final FormFlexPanel petsPanel = new FormFlexPanel();

    private final FormFlexPanel parkingPanel = new FormFlexPanel();

    private final FormFlexPanel storagePanel = new FormFlexPanel();

    private final FormFlexPanel otherPanel = new FormFlexPanel();

    private final SimplePanel pictureHolder = new SimplePanel();

    public ApartmentViewForm() {
        super(ApartmentInfoDTO.class, new VistaViewersComponentFactory());
        setEditable(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        HTML welcome = new HTML(HtmlUtils.h3(i18n.tr("Welcome") + " " + ClientContext.getUserVisit().getName() + "!<br>" + i18n.tr("Thank you for choosing")
                + " " + PtAppSite.getPmcName() + " " + i18n.tr("for your future home!")));
        welcome.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        int row = -1;
        main.setWidget(++row, 0, welcome);
        main.setWidget(++row, 0, new HTML(PortalResources.INSTANCE.welcomeNotes().getText()));

        main.setH1(++row, 0, 1, i18n.tr("General Info"));

        FormFlexPanel info = new FormFlexPanel();

        info.setWidget(0, 0, new DecoratorBuilder(inject(proto().floorplan()), 20).build());
        info.setWidget(1, 0, new DecoratorBuilder(inject(proto().bedrooms()), 10).build());
        info.setWidget(1, 1, new DecoratorBuilder(inject(proto().bathrooms()), 10).build());

        info.getColumnFormatter().setWidth(0, "50%");
        info.getColumnFormatter().setWidth(1, "50%");

        main.setWidget(++row, 0, info);

        main.setHR(++row, 0, 1);

        FormFlexPanel apartment = new FormFlexPanel();
        FormFlexPanel apartmentAddress = new FormFlexPanel();
        // address: we don't use any special widgets in order to simplify the view
        int aptRow = -1;
        apartmentAddress.setH3(++aptRow, 0, 2, i18n.tr("Address"));
        apartmentAddress.setWidget(++aptRow, 0, new DecoratorBuilder(inject(proto().address().street1()), 50).customLabel(i18n.tr("Street Address")).build());
        // currently we don't put anything in street2() while converting from dbo 2 dto
        // apartment.setWidget(++aptRow, 0, new DecoratorBuilder(inject(proto().address().street2()), 50).build());
        apartmentAddress.setWidget(++aptRow, 0, new DecoratorBuilder(inject(proto().address().city()), 15).build());

        // Need local variables to avoid extended casting that make the code unreadable
        CComponent<Province, ?> province = (CComponent<Province, ?>) inject(proto().address().province());
        apartmentAddress.setWidget(++aptRow, 0, new DecoratorBuilder(province, 17).build());

        CComponent<Country, ?> country = (CComponent<Country, ?>) inject(proto().address().country());
        apartmentAddress.setWidget(++aptRow, 0, new DecoratorBuilder(country, 15).build());

        CComponent<String, ?> postalCode = (CComponent<String, ?>) inject(proto().address().postalCode());
        apartmentAddress.setWidget(++aptRow, 0, new DecoratorBuilder(postalCode, 7).build());

        apartment.setWidget(0, 0, apartmentAddress);
        apartment.setWidget(0, 1, pictureHolder);

        info.getColumnFormatter().setWidth(0, "40%");
        info.getColumnFormatter().setWidth(1, "60%");

        main.setWidget(++row, 0, apartment);

        main.setH1(++row, 0, 1, i18n.tr("Lease Terms"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseFrom()), 8).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTo()), 8).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitRent()), 8).build());

        consessionPanel.setH1(0, 0, 1, i18n.tr("Promotions, Discounts and Concessions"));
        consessionPanel.setWidget(1, 0, inject(proto().concessions(), new ConcessionsFolder()));
        main.setWidget(++row, 0, consessionPanel);

        includedPanel.setH1(0, 0, 1, i18n.tr("Included Utilities"));
        includedPanel.setWidget(1, 0, inject(proto().includedUtilities(), new UtilityFolder()));
        main.setWidget(++row, 0, includedPanel);

        excludedPanel.setH1(0, 0, 1, i18n.tr("Excluded Utilities"));
        excludedPanel.setWidget(1, 0, inject(proto().externalUtilities(), new UtilityFolder()));
        main.setWidget(++row, 0, excludedPanel);

        chargedPanel.setH1(0, 0, 1, i18n.tr("Billed Utilities"));
        chargedPanel.setWidget(1, 0, inject(proto().agreedUtilities(), new FeatureFolder(Feature.Type.utility, this, false)));
        main.setWidget(++row, 0, chargedPanel);

        main.setH1(++row, 0, 1, i18n.tr("Add-Ons"));

        petsPanel.setH2(0, 0, 1, i18n.tr("Pets"));
        petsPanel.setWidget(1, 0, inject(proto().agreedPets(), new FeatureExFolder(true, Feature.Type.pet, this)));
        main.setWidget(++row, 0, petsPanel);

        parkingPanel.setH2(0, 0, 1, i18n.tr("Parking"));
        parkingPanel.setWidget(1, 0, inject(proto().agreedParking(), new FeatureExFolder(true, Feature.Type.parking, this)));
        main.setWidget(++row, 0, parkingPanel);

        storagePanel.setH2(0, 0, 1, i18n.tr("Storage"));
        storagePanel.setWidget(1, 0, inject(proto().agreedStorage(), new FeatureFolder(Feature.Type.locker, this, true)));
        main.setWidget(++row, 0, storagePanel);

        otherPanel.setH2(0, 0, 1, i18n.tr("Other"));
        otherPanel.setWidget(1, 0, inject(proto().agreedOther(), new FeatureFolder(Feature.Type.addOn, this, true)));
        main.setWidget(++row, 0, otherPanel);

        return main;
    }

    @Override
    public void populate(ApartmentInfoDTO value) {
        super.populate(value);

        pictureHolder.setWidget(MediaUtils.createPublicMediaImage(value.picture().getPrimaryKey(), ThumbnailSize.large));

        //hide/show various panels depend on populated data:
        consessionPanel.setVisible(!value.concessions().isEmpty());
        includedPanel.setVisible(!value.includedUtilities().isEmpty());
        excludedPanel.setVisible(!value.externalUtilities().isEmpty());
        chargedPanel.setVisible(!value.agreedUtilities().isEmpty());

        petsPanel.setVisible(!value.agreedPets().isEmpty() || !value.availablePets().isEmpty());
        parkingPanel.setVisible(!value.agreedParking().isEmpty() || !value.availableParking().isEmpty());
        storagePanel.setVisible(!value.agreedStorage().isEmpty() || !value.availableStorage().isEmpty());
        otherPanel.setVisible(!value.agreedOther().isEmpty() || !value.availableOther().isEmpty());
    }
}
