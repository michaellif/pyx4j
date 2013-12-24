/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.legal;

import com.google.gwt.core.client.GWT;

import com.propertyvista.operations.client.ui.crud.OperationsViewerViewImplBase;
import com.propertyvista.operations.domain.legal.VistaTerms;
import com.propertyvista.operations.rpc.services.version.VistaTermsVersionService;

public class VistaTermsViewerViewImpl extends OperationsViewerViewImplBase<VistaTerms> implements VistaTermsViewerView {

    public VistaTermsViewerViewImpl() {
        enableVersioning(VistaTerms.VistaTermsV.class, GWT.<VistaTermsVersionService> create(VistaTermsVersionService.class));
        setForm(new VistaTermsForm(this));
    }

    @Override
    public void populate(VistaTerms value) {
        setCaptionBase("T&C");
        super.populate(value);
    }
}
