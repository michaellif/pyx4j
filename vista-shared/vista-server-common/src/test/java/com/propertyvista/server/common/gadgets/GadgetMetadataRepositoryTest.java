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

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.ListerGadgetBaseMetadata;

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

    public void testEachListerGadgetHasPageSize() {
        assertForEachGagetMetadataClass("The followning lister gadgets don't have a defined pagesize filed", new Predicate() {

            @Override
            public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                if (ListerGadgetBaseMetadata.class.isAssignableFrom(klass)) {
                    boolean hasPageSize = false;
                    try {
                        ListerGadgetBaseMetadata metadata = (ListerGadgetBaseMetadata) GadgetMetadataRepository.get().createGadgetMetadata(klass);
                        hasPageSize = !metadata.isNull();
                    } catch (Throwable e) {
                        // in this test it's not relevant
                    }
                    return hasPageSize ? null : "";
                } else {
                    return null;
                }
            }
        });
    }

    public void testEveryGadgetMetadataHasValidDescription() {
        assertForEachGagetMetadataClass(
                "The following gadget metadatas must have a valid description (must be annotated with '" + GadgetDescription.class.getSimpleName()
                        + "' with defined values)", new Predicate() {

                    @Override
                    public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                        GadgetDescription d = klass.getAnnotation(GadgetDescription.class);
                        if (d == null) {
                            return "is not annotated with '" + GadgetDescription.class.getSimpleName() + "'";
                        }
                        String error = "";
                        if (d.name().equals("")) {
                            error += "doesn't have a name;";
                        }
                        if (d.description().equals("")) {
                            // TODO maybe worth to add some kind of spell check?
                            error += "doesn't have a description;";
                        }
                        if (d.keywords().length == 0) {
                            error += "doesn't have any keywords;";
                        }

                        return "".equals(error) ? null : error;
                    }
                });
    }

    public void ingore_testGadgetMetadataNamingConvention() {
        assertForEachGagetMetadataClass("The following gadget metadata classes must the naming convention (which is <Gadget Name>||GadgetMetadata)",
                new Predicate() {

                    @Override
                    public String reportWhatIsWrongWith(Class<? extends GadgetMetadata> klass) {
                        return !klass.getSimpleName().endsWith("GadgetMetadata") ? "" : null;
                    }
                });
    }

}
