/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 3, 2012
 * @author michaellif
 */
package com.propertyvista.crm.client.visor.dashboard;

import com.pyx4j.site.client.backoffice.ui.visor.IVisor;

public interface IDashboardVisorController extends IVisor.Controller {

    void saveDashboardMetadata();

    void print();

}
