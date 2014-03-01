/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade.u_1_0_9;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.financial.ARCode;

class ArrearsStatusGadgetSettingsUpgrader {

    private static final Logger log = LoggerFactory.getLogger(ArrearsStatusGadgetSettingsUpgrader.class);

    private static final Pattern categoryPattern = Pattern.compile("(\\<category>(\\w*)\\</category>)");

    private static final Map<String, String> categoryUpgradeMap;
    static {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("total", "");
        map.put("lease", ARCode.Type.Residential.name());
        map.put("parking", ARCode.Type.Parking.name());
        map.put("pet", ARCode.Type.Pet.name());
        map.put("addOn", ARCode.Type.AddOn.name());
        map.put("utility", ARCode.Type.Utility.name());
        map.put("locker", ARCode.Type.Locker.name());
        map.put("booking", ARCode.Type.OneTime.name());
        map.put("deposit", ARCode.Type.DepositSecurity.name());
        map.put("accountCharge", ARCode.Type.AccountCharge.name());
        map.put("nsf", ARCode.Type.NSF.name());
        map.put("latePayment", ARCode.Type.LatePayment.name());
        map.put("other", ARCode.Type.ExternalCharge.name());

        categoryUpgradeMap = Collections.unmodifiableMap(map);
    }

    /**
     * converts category: in previous versions it was DebitType, but in 1.0.9 it's going to be ARCode.Type
     */
    public static final String upgradeSettings(String settingsXml) {
        Matcher m = categoryPattern.matcher(settingsXml);
        if (m.find()) {
            String beforeCategoryName = m.group(2);
            String upgradedCategoryName = categoryUpgradeMap.get(beforeCategoryName);
            if (upgradedCategoryName == null) {
                log.warn("couldn't find a matching upgradable ARCodeType for DebitType '" + beforeCategoryName
                        + "', the default mapping for 'total' will be used. settings: '" + settingsXml + "'");
                upgradedCategoryName = "";
            }
            String upgradedConfigPortion =//@formatter:off 
                "<filterByCategory>" + !"".equals(upgradedCategoryName) + "</filterByCategory>" +
                "<category>" + upgradedCategoryName + "</category>";
            //@formatter:on

            String upgradedSettingXml = m.replaceAll(upgradedConfigPortion);
            log.info("Upgraded arrears status gadget settings :'" + upgradedSettingXml + "'");
            return m.replaceAll(upgradedConfigPortion);
        } else {
            log.info("Could not find 'category' setting in arrears status gadget settings (upgrade is not required) for the following settings: '"
                    + settingsXml + "'");
            return settingsXml;
        }
    }
}
