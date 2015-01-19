/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 10, 2014
 * @author arminea
 */
package com.propertyvista.biz.legal;

import com.propertyvista.domain.legal.n4.pdf.N4FormFieldsData;
import com.propertyvista.domain.legal.n4cp.pdf.N4CPFormFieldsData;

public interface N4CPGenerationFacade {

    byte[] generateN4CPLetter(N4CPFormFieldsData formData);

    N4CPFormFieldsData prepareN4CPData(N4FormFieldsData n4Data);
}
