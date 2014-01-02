/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.dashboard;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.server.common.gadgets.defaultsettings.GadgetMetadataDefaultSettings;

public abstract class GadgetMetadataRepositoryFacadeBase implements GadgetMetadataRepositoryFacade {

    protected static final class GadgetDefaultSettingsBinding<G extends GadgetMetadata> {

        private final Class<G> gadgetMetadataClass;

        private final Class<? extends GadgetMetadataDefaultSettings<? super G>> gadgetMetadataDefaultSettingsClass;

        public GadgetDefaultSettingsBinding(Class<G> gadgetMetadataClass,
                Class<? extends GadgetMetadataDefaultSettings<? super G>> gadgetMetadataDefaultSettingsClass) {
            this.gadgetMetadataClass = gadgetMetadataClass;
            this.gadgetMetadataDefaultSettingsClass = gadgetMetadataDefaultSettingsClass;
        }
    }

    private final Map<Class<? extends GadgetMetadata>, Class<? extends GadgetMetadataDefaultSettings>> bindingsMap;

    private final Set<Class<? extends GadgetMetadata>> gadgetMetadataClasses;

    protected GadgetMetadataRepositoryFacadeBase(Collection<GadgetDefaultSettingsBinding<?>> bindings) {
        Map<Class<? extends GadgetMetadata>, Class<? extends GadgetMetadataDefaultSettings>> preparingBindingsMap = new HashMap<Class<? extends GadgetMetadata>, Class<? extends GadgetMetadataDefaultSettings>>();
        for (GadgetDefaultSettingsBinding<?> binding : bindings) {
            preparingBindingsMap.put(binding.gadgetMetadataClass, binding.gadgetMetadataDefaultSettingsClass);
        }
        bindingsMap = Collections.unmodifiableMap(preparingBindingsMap);
        gadgetMetadataClasses = Collections.unmodifiableSet(new HashSet<Class<? extends GadgetMetadata>>(preparingBindingsMap.keySet()));
    }

    @Override
    public final Set<Class<? extends GadgetMetadata>> getGadgetMetadataClasses() {
        return gadgetMetadataClasses;
    }

    @Override
    public final GadgetMetadata createGadgetMetadata(GadgetMetadata proto) {
        return createGadgetMetadata((Class<? extends GadgetMetadata>) proto.getInstanceValueClass());
    }

    @Override
    public final GadgetMetadata createGadgetMetadata(Class<? extends GadgetMetadata> clazz) {
        Class<? extends GadgetMetadataDefaultSettings> initializerClass = bindingsMap.get(clazz);

        if (initializerClass == null) {
            throw new RuntimeException(SimpleMessageFormat.format("initializer for gadget metadata \"{0}\" is not defined", clazz.getName()));
        }

        GadgetMetadata gadgetMetadata = EntityFactory.create(clazz);

        GadgetMetadataDefaultSettings initializer = null;
        try {
            initializer = initializerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("failed to instantiate default settings initializer for gadget: " + clazz.getName(), e);
        }

        try {
            initializer.init(gadgetMetadata);
        } catch (Throwable e) {
            throw new RuntimeException("failed to initialize gadget default settings for gadget: " + clazz.getName(), e);
        }

        return gadgetMetadata;
    }

    protected static <G extends GadgetMetadata> GadgetDefaultSettingsBinding<G> bind(Class<G> gadgetMetadataClass,
            Class<? extends GadgetMetadataDefaultSettings<? super G>> gadgetMetadataDefaultSettingsClass) {
        assert gadgetMetadataClass != null & gadgetMetadataDefaultSettingsClass != null;
        return new GadgetDefaultSettingsBinding<G>(gadgetMetadataClass, gadgetMetadataDefaultSettingsClass);
    }

}
