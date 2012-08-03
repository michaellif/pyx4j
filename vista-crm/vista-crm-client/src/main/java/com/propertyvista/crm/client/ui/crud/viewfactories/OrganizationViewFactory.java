/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeEditorView;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeListerView;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeListerViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeViewerView;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioEditorView;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioListerView;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioListerViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioViewerView;
import com.propertyvista.crm.client.ui.crud.organisation.portfolio.PortfolioViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorEditorView;
import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorListerView;
import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorListerViewImpl;
import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorViewerView;
import com.propertyvista.crm.client.ui.crud.organisation.vendor.VendorViewerViewImpl;

public class OrganizationViewFactory extends ViewFactoryBase {

    public static <E extends IEntity, T extends IView> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (EmployeeListerView.class.equals(type)) {
                map.put(type, new EmployeeListerViewImpl());
            } else if (EmployeeViewerView.class.equals(type)) {
                map.put(type, new EmployeeViewerViewImpl());
            } else if (EmployeeEditorView.class.equals(type)) {
                map.put(type, new EmployeeEditorViewImpl());
            } else if (PortfolioListerView.class.equals(type)) {
                map.put(type, new PortfolioListerViewImpl());
            } else if (PortfolioViewerView.class.equals(type)) {
                map.put(type, new PortfolioViewerViewImpl());
            } else if (PortfolioEditorView.class.equals(type)) {
                map.put(type, new PortfolioEditorViewImpl());
            } else if (VendorListerView.class.equals(type)) {
                map.put(type, new VendorListerViewImpl());
            } else if (VendorViewerView.class.equals(type)) {
                map.put(type, new VendorViewerViewImpl());
            } else if (VendorEditorView.class.equals(type)) {
                map.put(type, new VendorEditorViewImpl());
            }
        }
        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
