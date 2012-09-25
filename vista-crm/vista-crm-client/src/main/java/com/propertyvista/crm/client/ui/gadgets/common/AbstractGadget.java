/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 30, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public abstract class AbstractGadget<GADGET_TYPE extends GadgetMetadata> implements IGadgetFactory, Cloneable {

    protected final String type;

    protected final String name;

    private final String description;

    private final boolean isBuildingGadget;

    private final Class<GADGET_TYPE> gadgetMetadataClass;

    protected AbstractGadget(Class<GADGET_TYPE> gadgetMetadataClassLiteral) {
        gadgetMetadataClass = gadgetMetadataClassLiteral;
        GADGET_TYPE gadgetMetadataProto = EntityFactory.getEntityPrototype(gadgetMetadataClassLiteral);
        isBuildingGadget = gadgetMetadataProto instanceof BuildingGadget;
        type = gadgetMetadataProto.getValueClass().getName();
        name = gadgetMetadataProto.getEntityMeta().getCaption();
        description = gadgetMetadataProto.getEntityMeta().getDescription();
    }

    @Override
    public IGadgetInstance createGadget(GadgetMetadata gadgetMetadata) throws Error {
        if ((gadgetMetadata != null) & !GWT.isProdMode()) {
            String provided = gadgetMetadata.cast().getObjectClass().getName();
            assert getType().equals(provided) : "Gadget Metadata has wrong class, expected '" + getType() + "' , but got '" + provided + "'";
        }

        try {
            GadgetInstanceBase<GADGET_TYPE> gadget = createInstance(gadgetMetadata);
            gadget.initView();
            return gadget;
        } catch (Throwable err) {
            throw new Error("Failed to create gadget: " + err.getLocalizedMessage(), err);
        }
    }

    @Override
    public final String getType() {
        return type;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public final boolean isBuildingGadget() {
        return isBuildingGadget;
    }

    @Override
    public boolean isAcceptedBy(DashboardType dashboardType) {
        return (DashboardType.system.equals(dashboardType) & !isBuildingGadget()) | (DashboardType.building.equals(dashboardType) & isBuildingGadget());
    }

    @Override
    public Class<? extends GadgetMetadata> getGadgetMetadataClass() {
        return gadgetMetadataClass;
    }

    protected abstract GadgetInstanceBase<GADGET_TYPE> createInstance(GadgetMetadata gadgetMetadata) throws Error;
}
