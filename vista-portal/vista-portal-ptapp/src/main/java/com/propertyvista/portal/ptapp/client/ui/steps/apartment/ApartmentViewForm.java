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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.policy.policies.MiscPolicy;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.resources.PortalResources;
import com.propertyvista.portal.ptapp.client.ui.components.UtilityFolder;
import com.propertyvista.portal.rpc.portal.ImageConsts.ThumbnailSize;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;

public class ApartmentViewForm extends CEntityDecoratableForm<ApartmentInfoDTO> {

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

    private final HTML welcome = new HTML();

    private FeatureExFolder petFolder;

    private FeatureFolder lockerFolder;

    private FeatureExFolder parkingFolder;

    public ApartmentViewForm() {
        super(ApartmentInfoDTO.class);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        boolean modifiable = SecurityController.checkBehavior(VistaCustomerBehavior.ProspectiveApplicant);

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        welcome.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        main.setWidget(++row, 0, welcome);
        main.setWidget(++row, 0, new HTML(PortalResources.INSTANCE.welcomeNotes().getText()));

        main.setH1(++row, 0, 1, i18n.tr("General Info"));

        FormFlexPanel info = new FormFlexPanel();

        info.setWidget(0, 0, new DecoratorBuilder(inject(proto().floorplan()), 20).build());
        info.setWidget(1, 0, new DecoratorBuilder(inject(proto().bedroomsAndDens()), 10).build());
        info.setWidget(1, 1, new DecoratorBuilder(inject(proto().bathrooms()), 10).build());

        info.getColumnFormatter().setWidth(0, "30%");
        info.getColumnFormatter().setWidth(1, "70%");

        main.setWidget(++row, 0, info);

        main.setHR(++row, 0, 1);

        FormFlexPanel address = new FormFlexPanel();

        int addrRow = -1;
        address.setWidget(++addrRow, 0, new DecoratorBuilder(inject(proto().address().city()), 15).build());
        address.setWidget(++addrRow, 0, new DecoratorBuilder(inject(proto().address().province()), 17).build());
        address.setWidget(++addrRow, 0, new DecoratorBuilder(inject(proto().address().country()), 15).build());
        address.setWidget(++addrRow, 0, new DecoratorBuilder(inject(proto().address().postalCode()), 7).build());

        FormFlexPanel apartment = new FormFlexPanel();

        apartment.setWidget(0, 0, new DecoratorBuilder(inject(proto().address().street1()), 50).customLabel(i18n.tr("Street Address")).build());
        apartment.getFlexCellFormatter().setColSpan(0, 0, 2);

        apartment.setWidget(1, 0, address);
        apartment.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);

        apartment.setWidget(1, 1, pictureHolder);

        apartment.getColumnFormatter().setWidth(0, "50%");
        apartment.getColumnFormatter().setWidth(1, "50%");

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
        petsPanel.setWidget(1, 0, inject(proto().agreedPets(), petFolder = new FeatureExFolder(Feature.Type.pet, this, modifiable)));
        main.setWidget(++row, 0, petsPanel);

        parkingPanel.setH2(0, 0, 1, i18n.tr("Parking"));
        parkingPanel.setWidget(1, 0, inject(proto().agreedParking(), parkingFolder = new FeatureExFolder(Feature.Type.parking, this, modifiable)));
        main.setWidget(++row, 0, parkingPanel);

        storagePanel.setH2(0, 0, 1, i18n.tr("Storage"));
        storagePanel.setWidget(1, 0, inject(proto().agreedStorage(), lockerFolder = new FeatureFolder(Feature.Type.locker, this, modifiable)));
        main.setWidget(++row, 0, storagePanel);

        otherPanel.setH2(0, 0, 1, i18n.tr("Other"));
        otherPanel.setWidget(1, 0, inject(proto().agreedOther(), new FeatureFolder(Feature.Type.addOn, this, modifiable)));
        main.setWidget(++row, 0, otherPanel);

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // generate welcome message for logged user:
        welcome.setHTML(HtmlUtils.h3(i18n.tr("Welcome") + " " + ClientContext.getUserVisit().getName() + "!<br>" + i18n.tr("Thank you for choosing") + " "
                + PtAppSite.getPmcName() + " " + i18n.tr("for your future home!")));

        pictureHolder.setWidget(MediaUtils.createPublicMediaImage(getValue().picture().getPrimaryKey(), ThumbnailSize.large));

        //hide/show various panels depend on populated data:
        consessionPanel.setVisible(!getValue().concessions().isEmpty());
        includedPanel.setVisible(!getValue().includedUtilities().isEmpty());
        excludedPanel.setVisible(!getValue().externalUtilities().isEmpty());
        chargedPanel.setVisible(!getValue().agreedUtilities().isEmpty());

        petsPanel.setVisible(!getValue().agreedPets().isEmpty() || !getValue().availablePets().isEmpty());
        parkingPanel.setVisible(!getValue().agreedParking().isEmpty() || !getValue().availableParking().isEmpty());
        storagePanel.setVisible(!getValue().agreedStorage().isEmpty() || !getValue().availableStorage().isEmpty());
        otherPanel.setVisible(!getValue().agreedOther().isEmpty() || !getValue().availableOther().isEmpty());

        // set maximum limits:
        // petFolder.setMaxCount(getValue().maxPets().getValue());
        //parkingFolder.setMaxCount(getValue().maxParkingSpots().getValue());

        ClientPolicyManager.obtainEffectivePolicy(getValue().unit(), MiscPolicy.class, new DefaultAsyncCallback<MiscPolicy>() {
            @Override
            public void onSuccess(MiscPolicy result) {
                petFolder.setMaxCount(result.maxPets().getValue());
                parkingFolder.setMaxCount(result.maxParkingSpots().getValue());
                lockerFolder.setMaxCount(result.maxParkingSpots().getValue());
            }
        });

    }
}
