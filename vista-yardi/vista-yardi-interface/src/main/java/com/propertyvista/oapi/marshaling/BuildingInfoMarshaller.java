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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.oapi.model.BuildingInfoIO;
import com.propertyvista.oapi.model.BuildingTypeIO;
import com.propertyvista.oapi.xml.Action;

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
    public BuildingInfoIO unmarshal(BuildingInfo info) {
        BuildingInfoIO buildingInfoIO = new BuildingInfoIO();
        buildingInfoIO.address = AddressMarshaller.getInstance().unmarshal(info.address());
        buildingInfoIO.buildingType = new BuildingTypeIO(info.type().getValue());
        return buildingInfoIO;
    }

    @Override
    public BuildingInfo marshal(BuildingInfoIO buildingInfoIO) throws Exception {
        if (buildingInfoIO == null) {
            return null;
        }
        BuildingInfo buildingInfo = EntityFactory.create(BuildingInfo.class);
        if (buildingInfoIO.getAction() == Action.nil) {
            buildingInfo.set(null);
        } else {
            buildingInfo.address().set(AddressMarshaller.getInstance().marshal(buildingInfoIO.address));
            buildingInfo.type().setValue(buildingInfoIO.buildingType.value);
        }
        return buildingInfo;
    }
}
