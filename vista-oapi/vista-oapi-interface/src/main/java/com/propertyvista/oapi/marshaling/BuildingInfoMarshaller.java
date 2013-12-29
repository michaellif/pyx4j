/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.oapi.model.BuildingInfoIO;
import com.propertyvista.oapi.model.types.BuildingTypeIO;

public class BuildingInfoMarshaller implements Marshaller<BuildingInfo, BuildingInfoIO> {

    private static class SingletonHolder {
        public static final BuildingInfoMarshaller INSTANCE = new BuildingInfoMarshaller();
    }

    private BuildingInfoMarshaller() {
    }

    public static BuildingInfoMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public BuildingInfoIO marshal(BuildingInfo info) {
        if (info == null || info.isNull()) {
            return null;
        }
        BuildingInfoIO buildingInfoIO = new BuildingInfoIO();

        buildingInfoIO.address = AddressMarshaller.getInstance().marshal(info.address());
        buildingInfoIO.buildingType = MarshallerUtils.createIo(BuildingTypeIO.class, info.type());
        return buildingInfoIO;
    }

    @Override
    public BuildingInfo unmarshal(BuildingInfoIO buildingInfoIO) {
        if (buildingInfoIO == null) {
            return null;
        }

        BuildingInfo buildingInfo = EntityFactory.create(BuildingInfo.class);

        MarshallerUtils.set(buildingInfo.address(), buildingInfoIO.address, AddressMarshaller.getInstance());
        MarshallerUtils.setValue(buildingInfo.type(), buildingInfoIO.buildingType);
        return buildingInfo;
    }
}
