/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.BuildingDTO;

public interface BuildingEditorView extends IPrimeEditorView<BuildingDTO> {

    interface Presenter extends BuildingPresenterCommon, IPrimeEditorView.IPrimeEditorPresenter {

        void retrieveEmployee(AsyncCallback<Employee> callback, Employee employeeId);

        void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback);
    }
}
