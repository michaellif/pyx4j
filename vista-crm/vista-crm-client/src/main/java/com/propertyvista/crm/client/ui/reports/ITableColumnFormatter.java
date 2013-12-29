/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import com.google.gwt.safehtml.shared.SafeHtml;

import com.pyx4j.entity.core.IEntity;

public interface ITableColumnFormatter {

    SafeHtml formatHeader();

    SafeHtml formatContent(IEntity entity);

    /** return width in pixels */
    int getWidth();

}
