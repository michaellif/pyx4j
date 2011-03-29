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
package com.propertyvista.portal.rpc.pt;

public interface ApplicationDocumentServletParameters {

    /**
     * Tenent Id parameter name passed along with binary document on upload form
     */
    public static final String TENANT_ID = "TenantId";

    /**
     * Document type parameter name passed along with binary document on upload form
     */
    public static final String DOCUMENT_TYPE = "DocumentType";

    /**
     * Document Id parameter name passed to download document servlet
     */
    public static final String DOCUMENT_ID = "DocumentId";
}
