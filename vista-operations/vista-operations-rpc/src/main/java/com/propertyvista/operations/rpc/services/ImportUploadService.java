/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.services;

import com.pyx4j.entity.shared.AbstractIFileBlob;
import com.pyx4j.gwt.rpc.upload.UploadService;

import com.propertyvista.crm.rpc.dto.ImportUploadDTO;

public interface ImportUploadService extends UploadService<ImportUploadDTO, AbstractIFileBlob> {

}
