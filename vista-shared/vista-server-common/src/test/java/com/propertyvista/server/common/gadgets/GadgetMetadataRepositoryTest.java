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

import java.util.UUID;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetMetadataRepositoryTest extends GadgetMetadataRepositoryTestBase {

    public void testEachGadgetMetadataShouldBeATransientEntity() {
        assertForEachGagetMetadataClass("The following gadget metadata classes must be marked as @Transient", new Predicate() {
            @Override
            public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                return (klass.getAnnotation(Transient.class) == null) ? "" : null;
            }
        });
    }

    public void testEachGadgetsDefaultInitializerWorks() {
        assertForEachGagetMetadataClass("The following gadget metadata classes cannot be instantiated with default settings", new Predicate() {

            @Override
            public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                Throwable instantiationError = null;
                try {
                    GadgetMetadataRepository.get().createGadgetMetadata(klass);
                } catch (Throwable e) {
                    instantiationError = e;
                }
                return instantiationError == null ? null : instantiationError.toString();
            }

        });
    }

    public void testEachInitializedGadgetMetadataMustHaveUUID() {
        assertForEachGagetMetadataClass("In the following gadget metadata field '"
                + EntityFactory.getEntityPrototype(GadgetMetadata.class).gadgetId().getFieldName() + "' must be initialized with a valid UUID by default",
                new Predicate() {
                    @Override
                    public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                        boolean hasUUID = false;
                        try {
                            GadgetMetadata metadata = GadgetMetadataRepository.get().createGadgetMetadata(klass);
                            UUID.fromString(metadata.gadgetId().getValue());
                            hasUUID = true;
                        } catch (Throwable e) {
                            // in this test it's not relevant
                        }
                        return hasUUID ? null : "";
                    }
                });
    }

    public void ingoreTestGadgetMetadataNamingConvention() {
        assertForEachGagetMetadataClass("The following gadget metadata classes must the naming convention (which is <Gadget Name>||GadgetMetadata)",
                new Predicate() {

                    @Override
                    public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                        return !klass.getSimpleName().endsWith("GadgetMetadata") ? "" : null;
                    }
                });
    }

}
