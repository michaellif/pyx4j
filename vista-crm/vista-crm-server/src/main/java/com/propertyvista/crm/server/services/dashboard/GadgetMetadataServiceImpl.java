/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.crm.rpc.dto.dashboard.GadgetDescriptorDTO;
import com.propertyvista.crm.rpc.services.dashboard.GadgetMetadataService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.server.common.gadgets.GadgetMetadataRepository;

// TODO check that translation works
public class GadgetMetadataServiceImpl implements GadgetMetadataService {

    private static final I18n i18n = I18n.get(GadgetMetadataServiceImpl.class);

    @Override
    public void createGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata proto) {
        GadgetMetadata gadgetMetadata = GadgetMetadataRepository.get().createGadgetMetadata(proto);
        gadgetMetadata.ownerUser().setPrimaryKey(CrmAppContext.getCurrentUserPrimaryKey());

        Util.gadgetStorage().save(gadgetMetadata.gadgetId().getValue(), gadgetMetadata, false);
        Persistence.service().commit();

        callback.onSuccess(gadgetMetadata);
    }

    @Override
    public void saveGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata gadgetMetadata) {
        if (gadgetMetadata == null) {
            throw new Error("Got null instead of gadget metadata");
        }
        if (gadgetMetadata.gadgetId().isNull()) {
            throw new Error("got gadget metadata with no defined id:" + gadgetMetadata.toString());
        }
        if (!CrmAppContext.getCurrentUserPrimaryKey().equals(gadgetMetadata.ownerUser().getPrimaryKey())) {
            throw new SecurityViolationException(i18n.tr("Change of gadget settings can be perfromed only by its owner"));
        }

        Util.gadgetStorage().save(gadgetMetadata.gadgetId().getValue(), gadgetMetadata, true);
        Persistence.service().commit();

        callback.onSuccess(gadgetMetadata);
    }

    @Override
    public void listAvailableGadgets(AsyncCallback<Vector<GadgetDescriptorDTO>> callback, DashboardType boardType) {
        Vector<GadgetDescriptorDTO> descriptors = new Vector<GadgetDescriptorDTO>();

        for (Class<? extends GadgetMetadata> gadgetMetadataClass : GadgetMetadataRepository.get().getGadgetMetadataClasses()) {
            if (isAcceptedBy(boardType, gadgetMetadataClass)) {
                GadgetMetadata proto = EntityFactory.getEntityPrototype(gadgetMetadataClass);
                GadgetDescription gadgetDescription = gadgetMetadataClass.getAnnotation(GadgetDescription.class);
                descriptors.add(new GadgetDescriptorDTO(//@formatter:off
                        i18n.translate("", gadgetDescription.name()),
                        i18n.translate("", gadgetDescription.description()),
                        translate("", gadgetDescription.keywords()),
                        proto
                ));//@formatter:on
            }
        }
        callback.onSuccess(descriptors);
    }

    private static boolean isAcceptedBy(DashboardType boardType, Class<? extends GadgetMetadata> gadgetMetadataClass) {
        return (boardType == DashboardType.building & BuildingGadget.class.isAssignableFrom(gadgetMetadataClass))
                | (boardType != DashboardType.building & !BuildingGadget.class.isAssignableFrom(gadgetMetadataClass));
    }

    private static List<String> translate(String context, String... words) {
        ArrayList<String> translatedWords = new ArrayList<String>();
        for (String word : words) {
            translatedWords.add(i18n.translate(context, word));
        }
        return translatedWords;
    }
}
