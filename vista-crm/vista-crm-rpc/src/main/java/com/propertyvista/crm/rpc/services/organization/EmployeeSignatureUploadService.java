/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.organization;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.rpc.upload.UploadService;

import com.propertyvista.domain.blob.EmployeeSignatureBlob;

public interface EmployeeSignatureUploadService extends UploadService<IEntity, EmployeeSignatureBlob> {

}
