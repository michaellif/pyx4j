/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetMetadataFactory {

    public static String INITIALIZER_CLASS_SUFFIX = "DefaultSettings";

    public static String INITIALIZERS_PACKAGE = GadgetMetadataFactory.class.getPackage().getName() + ".defaultsettings";

//    public static Map<String, GadgetMetadataDefaultSettings<?>> defaultSettingsMap = new ConcurrentHashMap<String, GadgetMetadataDefaultSettings<?>>();

    public static GadgetMetadata createGadgetMetadata(Class<? extends GadgetMetadata> clazz) {
        String initializerClassSimpleName = clazz.getSimpleName() + INITIALIZER_CLASS_SUFFIX;

        ClassLoader classLoader = GadgetMetadataFactory.class.getClassLoader();
        GadgetMetadataDefaultSettings initializer = null;

        try {
            initializer = (GadgetMetadataDefaultSettings<?>) classLoader.loadClass(INITIALIZERS_PACKAGE + "." + initializerClassSimpleName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("failed to instantiate default settings initializer for gadget :" + clazz.getName(), e);
        }

        GadgetMetadata gadgetMetadata = EntityFactory.create(clazz);
        initializer.init(gadgetMetadata);

        return gadgetMetadata;

    }

    public static GadgetMetadata createGadgetMetadata(GadgetMetadata proto) {
        return createGadgetMetadata((Class<? extends GadgetMetadata>) proto.getInstanceValueClass());
    }
}
