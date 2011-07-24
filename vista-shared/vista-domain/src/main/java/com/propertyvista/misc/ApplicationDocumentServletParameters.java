/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2011
 * @author sergei
 * @version $Id$
 */
package com.propertyvista.misc;

import java.util.Arrays;
import java.util.EnumSet;

import com.pyx4j.essentials.rpc.report.DownloadFormat;

public interface ApplicationDocumentServletParameters {

    /**
     * Tenant Id parameter name passed along with binary document on upload form
     */
    public static final String TENANT_ID = "TenantId";

    /**
     * Income Id parameter name passed along with binary document on income documents
     * upload form
     */
    //public static final String INCOME_ID = "IncomeId";

    /**
     * Document type parameter name passed along with binary document on upload form
     */
    //public static final String DOCUMENT_TYPE = "DocumentType";

    /**
     * Application Document Data Id parameter name passed to download document servlet
     */
    public static final String DATA_ID = "dataId";

    /**
     * array of file extensions allowed to upload as application document
     */
    public static final EnumSet<DownloadFormat> SUPPORTED_FILE_EXTENSIONS = EnumSet.copyOf(Arrays.asList(new DownloadFormat[] { DownloadFormat.JPEG,
            DownloadFormat.GIF, DownloadFormat.PNG, DownloadFormat.TIF, DownloadFormat.BMP, DownloadFormat.PDF }));

}
