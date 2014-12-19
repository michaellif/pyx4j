/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-08
 * @author ArtyomB
 */
package com.propertyvista.crm.rpc.services.lease;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.rpc.report.ReportService;

public interface LeaseTermBlankAgreementDocumentDownloadService extends ReportService<IEntity> {

    String LEASE_ID_PARAM_KEY = "LEASE_ID";

    String USE_ONLY_SIGNATURE_PLACEHOLDERS_PARAM_KEY = "USE_ONLY_SIGNATURE_PLACEHOLDERS";

    String CREATE_DRAFT_PARAM_KEY = "MAKE_DRAFT";

}
