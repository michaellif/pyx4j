/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.preloader;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.domain.marketing.PortalResidentMarketingTarget;
import com.propertyvista.domain.marketing.PortalResidentMarketingTip;

public class QuickTipDevPreloader extends BaseVistaDevDataPreloader {

    public static final String INSURANCE_TIP_COMMENTS = "Insurance Sample";

    public static final String INSURANCE_TIP_CONTENT = "<div style=\"padding: 10px\"><div align=\"center\"><b>Don't have Tenant Insurance yet?</b></div><p style=\"font-size:0.8em\">We have teamed up with Highcourt Partners Limited, a licensed broker, to assist you in obtaining your Tenant Insurance.</p><div align=\"center\"><a href=\"#resident_services\" style=\"text-align: center\"><i style=\"font-size:0.8em\">Visit Resident Services page</i></a></div></div>";

    public static final String AUTOPAY_TIP_COMMENTS = "Autopay Sample";

    public static final String AUTOPAY_CONTENT = "<div style=\"padding: 10px\"><div align=\"center\"><b>Pre-authorized payments</b></div><p style=\"font-size:0.8em\">Paying your rent by pre-authorized payments means eliminating the chore of writing cheques and ensuring your payment reaches Property Management Office by the due date. You'll never have to worry about remembering to make a payment or a possible late fee.</p><div align=\"center\"><a href=\"#financial\" style=\"text-align: center\"><i style=\"font-size:0.8em\">Visit Billing &amp; Payment page.</i></a></div></div>";

    public static final String OTHER_TIP_COMMENTS = "Other Sample";

    public static final String OTHER_CONTENT = "<div style=\"padding: 10px\"><div align=\"center\"><b>Request repairs and maintenance as needed</b></div><p style=\"font-size:0.8em\">Submit and track the status of a maintenance request. Convenient, simple, easy.</p><div align=\"center\"><a href=\"#maintenance\" style=\"text-align: center\"><i style=\"font-size:0.8em\">Visit Maintenance page</i></a></div></div>";

    @Override
    public String create() {

        // Create Insurance quick tip
        createQuickTip(INSURANCE_TIP_COMMENTS, PortalResidentMarketingTarget.InsuranceMissing, INSURANCE_TIP_CONTENT);

        // Create AutoPay quick tip
        createQuickTip(AUTOPAY_TIP_COMMENTS, PortalResidentMarketingTarget.AutopayAgreementNotSetup, AUTOPAY_CONTENT);

        // Create Other quick tip
        createQuickTip(OTHER_TIP_COMMENTS, PortalResidentMarketingTarget.Other, OTHER_CONTENT);

        return null;
    }

    private PortalResidentMarketingTip createQuickTip(String comments, PortalResidentMarketingTarget type, String content) {
        PortalResidentMarketingTip tip = EntityFactory.create(PortalResidentMarketingTip.class);
        tip.comments().setValue(comments);
        tip.target().setValue(type);
        tip.content().setValue(content);

        Persistence.service().persist(tip);
        return tip;
    }

    @Override
    public String delete() {
        return null;
    }

}
