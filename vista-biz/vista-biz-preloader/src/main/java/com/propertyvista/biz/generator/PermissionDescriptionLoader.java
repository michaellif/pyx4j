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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.csv.EntityCSVReciver;
import com.pyx4j.essentials.server.csv.XLSLoad;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.generator.model.Permission;
import com.propertyvista.biz.generator.model.PermissionDescription;
import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.VistaCrmBehaviorDTO;

public class PermissionDescriptionLoader {

    private static final Logger log = LoggerFactory.getLogger(PermissionDescriptionLoader.class);

    private static final String DESCRIPTIONS_FILE = "FunctionalBehaviours.xlsx";

    // Chars to be removed from description names to avoid inconsistencies
    public static final char[] IGNORING_CHARS = { ' ', ':', '&', '\n' };

    private static Map<String, VistaCrmBehavior> behaviorsByName = initbehaviorsByName();

    // CaseInsensitiveMap to avoid inconsistencies between permission names from xls file and permission name from DB
    private static Map<VistaCrmBehavior, String> descriptionsMap;

    private static Map<String, List<VistaCrmBehavior>> behaviorsByRoleName;

    private static synchronized Map<VistaCrmBehavior, String> getDescriptionsMap() {
        if (null == descriptionsMap) {
            initDescriptionsMap();
        }
        return descriptionsMap;
    }

    private static Map<String, VistaCrmBehavior> initbehaviorsByName() {
        Map<String, VistaCrmBehavior> byNames = new HashMap<>();
        for (VistaCrmBehavior b : EnumSet.allOf(VistaCrmBehavior.class)) {
            byNames.put(b.name().toLowerCase(Locale.ENGLISH).replace(" ", ""), b);
        }
        return byNames;
    }

    public static Map<String, List<VistaCrmBehavior>> getDefaultRolesDefinition() {
        // Initialize
        getDescriptionsMap();

        return behaviorsByRoleName;

    }

    public static Collection<VistaCrmBehaviorDTO> enhanceDescriptions(Collection<VistaCrmBehaviorDTO> values) {

        setPermissions(values, getDescriptionsMap());

        return values;
    }

    // Set description from XLS file to permission from DB
    private static void setPermissions(Collection<VistaCrmBehaviorDTO> to, Map<VistaCrmBehavior, String> from) {
        for (VistaCrmBehaviorDTO dto : to) {
            dto.description().setValue(from.get(dto.behavior().getValue()));
        }
    }

    private static VistaCrmBehavior toBehavior(String verbalBehaviour) {
        try {
            verbalBehaviour = StringUtils.replaceChars(verbalBehaviour, new String(IGNORING_CHARS), null);
            VistaCrmBehavior b = behaviorsByName.get(verbalBehaviour.toLowerCase(Locale.ENGLISH));
            if (b != null) {
                return b;
            }
            b = VistaCrmBehavior.valueOf(verbalBehaviour);
            return b;
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    private static void initDescriptionsMap() {
        EntityCSVReciver<PermissionDescription> descriptionReciver = EntityCSVReciver.create(PermissionDescription.class);
        XLSLoad xls = XLSLoad.loadResourceFile(IOUtils.resourceFileName(DESCRIPTIONS_FILE, PermissionDescriptionLoader.class), true, descriptionReciver);

        // Part One Load Description
        List<PermissionDescription> descriptions = descriptionReciver.getEntities();

        Map<VistaCrmBehavior, String> descriptionsMapLocal = new HashMap<>();
        for (PermissionDescription p : descriptions) {
            // Ignore some chars from permission names from XLS file
            VistaCrmBehavior b = toBehavior(p.permission().getValue());
            if (b != null) {
                descriptionsMapLocal.put(b, p.description().getValue());
            } else {
                log.info("Loading permission descriptions: '" + p.permission().getValue() + "' does not match");
            }
        }

        Map<String, List<VistaCrmBehavior>> behaviorsByRoleNameLocal = new HashMap<>();

        // Part 2 Load default Roles
        for (int sheetNumber = 2; sheetNumber < xls.getNumberOfSheets(); sheetNumber++) {
            if (xls.isSheetHidden(sheetNumber)) {
                continue;
            }

            // Read new Entity Role Model.
            String roleName = xls.getSheetName(sheetNumber);
            if (roleName.equalsIgnoreCase("Template")) {
                continue;
            }
            if (roleName.equalsIgnoreCase(CrmRolesPreloader.DEFAULT_ACCESS_ALL_ROLE_NAME)) {
                break;
            }

            EntityCSVReciver<Permission> permissionsReceiver = EntityCSVReciver.create(Permission.class);
            permissionsReceiver = EntityCSVReciver.create(Permission.class);
            List<VistaCrmBehavior> permissions = new ArrayList<>();
            xls.setIgnoreCellValueErrors(true);
            xls.loadSheet(sheetNumber, permissionsReceiver);

            for (Permission permission : permissionsReceiver.getEntities()) {
                VistaCrmBehavior b = toBehavior(permission.permission().getValue());
                if (b != null) {
                    permissions.add(b);
                }
            }

            behaviorsByRoleNameLocal.put(roleName, permissions);
        }

        behaviorsByRoleName = behaviorsByRoleNameLocal;
        descriptionsMap = descriptionsMapLocal;

    }
}
