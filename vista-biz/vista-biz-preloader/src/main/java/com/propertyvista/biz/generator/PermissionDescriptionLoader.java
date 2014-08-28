/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.generator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.generator.model.PermissionDescription;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class PermissionDescriptionLoader {

    private static final String DESCRIPTIONS_FILE = "permission_descriptions.xls";

    // Chars to be removed from description names to avoid inconsistencies
    public static final char[] IGNORING_CHARS = { ' ', ':' };

    // CaseInsensitiveMap to avoid inconsistencies between permission names from xls file and permission name from DB
    private static CaseInsensitiveMap<String, String> descriptionsMap;

    private static synchronized Map<String, String> getDescriptionsMap() {
        if (null == descriptionsMap) {
            initDescriptionsMap();
        }

        return descriptionsMap;
    }

    public static Collection<VistaCrmBehaviorDTO> enhanceDescriptions(Collection<VistaCrmBehaviorDTO> values) {

        setPermissions(values, getDescriptionsMap());

        return values;
    }

    // Set description from XLS file to permission from DB
    private static void setPermissions(Collection<VistaCrmBehaviorDTO> to, Map<String, String> from) {

        for (VistaCrmBehaviorDTO dto : to) {
            // Ignore some chars from permission names from DB
            String key = StringUtils.replaceChars(dto.permission().getValue(), new String(IGNORING_CHARS), null);
            if (from.containsKey(key))
                dto.description().setValue(from.get(key));
        }
    }

    private static void initDescriptionsMap() {
        List<PermissionDescription> descriptions = EntityCSVReciver.create(PermissionDescription.class).loadResourceFile(
                IOUtils.resourceFileName(DESCRIPTIONS_FILE, PermissionDescriptionLoader.class));

        descriptionsMap = new CaseInsensitiveMap<String, String>(descriptions.size());

        for (PermissionDescription p : descriptions) {
            // Ignore some chars from permission names from XLS file
            String permissionNameIgnoreChars = StringUtils.replaceChars(p.permission().getValue(), new String(IGNORING_CHARS), null);
            descriptionsMap.put(permissionNameIgnoreChars, p.description().getValue());
        }
    }

}
