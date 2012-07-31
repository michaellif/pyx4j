/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.site.client.ui.reports.HasAdvancedSettings;
import com.pyx4j.site.client.ui.reports.ReportSettings;

@Transient
public interface MockupReportSettings extends ReportSettings, HasAdvancedSettings {

    IPrimitive<String> valueX();

    IPrimitive<String> valueY();

    IPrimitive<String> valueZ();

    IPrimitive<String> advancedValueX();

    IPrimitive<String> advancedValueY();

    IPrimitive<String> advancedValueZ();

}
