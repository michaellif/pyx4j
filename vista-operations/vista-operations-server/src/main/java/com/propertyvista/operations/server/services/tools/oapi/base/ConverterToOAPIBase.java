/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2014
 * @author ernestog
 */
package com.propertyvista.operations.server.services.tools.oapi.base;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.interfaces.importer.model.BuildingModel;
import com.propertyvista.interfaces.importer.oapi.base.BuildingParser;
import com.propertyvista.interfaces.importer.oapi.base.BuildingProcessor;
import com.propertyvista.oapi.v1.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.xml.ElementIO;
import com.propertyvista.operations.domain.imports.OapiConversion;
import com.propertyvista.operations.domain.imports.OapiConversionFile;
import com.propertyvista.operations.domain.imports.OapiConversionFile.OapiConversionFileType;
import com.propertyvista.operations.domain.imports.blob.OapiConversionBlob;
import com.propertyvista.operations.server.services.tools.oapi.ConverterToOAPI;


// TODO Consider moving this functionality to OAPI Interface project
public class ConverterToOAPIBase implements ConverterToOAPI {

    protected HashMap<String, BuildingIO> buildings = new HashMap<String, BuildingIO>();

    @Override
    public ElementIO process(OapiConversion data) {

        processBuildings(data.conversionFiles());

        return getIOElements();

//        return BuildingMarshaller.getInstance().marshalCollection(BuildingListIO.class, (List<Building>) entities);

    }

    private ElementIO getIOElements() {
        BuildingListIO buildingsIO = new BuildingListIO();
        for (String key : buildings.keySet()) {
            buildingsIO.add(buildings.get(key));
        }
        return buildingsIO;
    }

    private void processBuildings(IList<OapiConversionFile> conversionFiles) {
        for (OapiConversionFile file : conversionFiles) {
            if (file.type().getValue().equals(OapiConversionFileType.BuildingIO)) {
                processBuildingsFile(file);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processBuildingsFile(OapiConversionFile file) {
        List<BuildingModel> models = (List<BuildingModel>) getEntityModels(file);
        List<? extends IEntity> entities = new BuildingProcessor().process(models);

        for (IEntity entity : entities) {
            Building b = (Building) entity;
            add(BuildingMarshaller.getInstance().marshalItem(b));
        }

    }

    private List<? extends IEntity> getEntityModels(OapiConversionFile file) {
        return new BuildingParser().parseBuildings(getBlob(file).data().getValue(),
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(file.file().fileName().getValue())));
    }

    private OapiConversionBlob getBlob(OapiConversionFile file) {
        return Persistence.service().retrieve(OapiConversionBlob.class, file.file().blobKey().getValue());
    }

    protected <E extends ElementIO> void add(E element) {
        if (element.getClass() == BuildingIO.class) {
            addBuilding((BuildingIO) element);
            return;
        } else {
            // TODO search and add different elements in buildings hierarchy
        }
    }

    private void addBuilding(BuildingIO building) {
        buildings.put(getBuildingKey(building), building);
    }

    protected void clear() {
        buildings.clear();
    }

    private String getBuildingKey(BuildingIO building) {
        // For now we use propertyCode as key for buildingIO element
        StringBuilder key = new StringBuilder();
        key.append(building.propertyCode);

        return key.toString();
    }
}
