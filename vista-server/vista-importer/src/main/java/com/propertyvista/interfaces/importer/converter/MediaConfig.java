/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.converter;

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;

public class MediaConfig {

    public String baseFolder;

    public String directory;

    public boolean ignoreMissingMedia;

    public boolean mimizePreloadDataSize = true;

    public static MediaConfig create(ImportUploadDTO importDTO) {
        MediaConfig mediaConfig = new MediaConfig();
        mediaConfig.baseFolder = "data/export/images/" + NamespaceManager.getNamespace();
        mediaConfig.ignoreMissingMedia = importDTO.ignoreMissingMedia().isBooleanTrue();
        return mediaConfig;
    }
}
