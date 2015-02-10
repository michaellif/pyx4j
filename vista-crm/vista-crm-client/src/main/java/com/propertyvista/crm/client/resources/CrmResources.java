/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 */
package com.propertyvista.crm.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.TextResource;

public interface CrmResources extends ClientBundleWithLookup {

    CrmResources INSTANCE = GWT.create(CrmResources.class);

    @Source("BackgroundCheckHelp.html")
    TextResource backgroundCheckHelp();

    @Source("ARPolicyRuleDescription.html")
    TextResource arPolicyRuleDescription();

    @Source("DnsNameSetupPortalHeader.html")
    TextResource dnsNameSetupPortalHeader();

    @Source("DnsNameSetupWebsiteHeader.html")
    TextResource dnsNameSetupWebsiteHeader();

    @Source("DnsNameSetupFooter.html")
    TextResource dnsNameSetupFooter();
}
