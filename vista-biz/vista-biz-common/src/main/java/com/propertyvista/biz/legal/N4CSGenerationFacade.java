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

import com.propertyvista.domain.legal.n4.pdf.N4PdfFormData;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfFormData;
import com.propertyvista.domain.legal.n4cs.pdf.N4CSPdfServiceMethod.ServiceMethod;

public interface N4CSGenerationFacade {

    byte[] generateN4CSLetter(N4CSPdfFormData formData);

    N4CSPdfFormData prepareN4CSData(N4PdfFormData n4Data, ServiceMethod serviceMethod);
}
