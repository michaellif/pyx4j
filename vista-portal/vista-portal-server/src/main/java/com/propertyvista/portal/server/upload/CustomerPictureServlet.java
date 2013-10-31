/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 21, 2013
 * @author stanp
 * @version $Id$
 */

package com.propertyvista.portal.server.upload;

import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.server.domain.CustomerPictureBlob;

@SuppressWarnings("serial")
public class CustomerPictureServlet extends AbstractFileAccessServlet<CustomerPicture, CustomerPictureBlob> {

    public CustomerPictureServlet() {
        super(CustomerPicture.class, CustomerPictureBlob.class);
    }
}
