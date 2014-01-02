/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-12
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.biz.dashboard;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.annotations.Caption;

import com.propertyvista.domain.dashboard.gadgets.type.base.DemoGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

/**
 * This class is for generation data that describes gadgets used in vista
 */
public class GadgetMetadadataRepositoryDescriptionGenerator {

    public static void main(String[] args) {
        OutputStream out = System.out;
        PrintWriter writer = new PrintWriter(out);
        List<Class<? extends GadgetMetadata>> gadgets = new ArrayList<Class<? extends GadgetMetadata>>();
        for (Class<? extends GadgetMetadata> gadget : ServerSideFactory.create(GadgetMetadataRepositoryFacade.class).getGadgetMetadataClasses()) {
            if (DemoGadget.class.isAssignableFrom(gadget)) {
                continue;
            }
            gadgets.add(gadget);
        }
        Collections.sort(gadgets, new Comparator<Class<? extends GadgetMetadata>>() {
            @Override
            public int compare(Class<? extends GadgetMetadata> o1, Class<? extends GadgetMetadata> o2) {
                return o1.getSimpleName().compareTo(o2.getSimpleName());
            }
        });

        for (Class<? extends GadgetMetadata> gadget : gadgets) {
            String indent = "";
            GadgetDescription description = gadget.getAnnotation(GadgetDescription.class);
            writer.println(indent + gadget.getSimpleName().replaceFirst("Metadata", "") + ":");
            indent = "    ";
            writer.println(indent + "       name: " + gadget.getAnnotation(Caption.class).name());
            writer.println(indent + "description: " + description.description());
            if (false) {
                indent = indent + "             ";
                for (String desciptionline : description.description().split("\n")) {
                    writer.println(indent + desciptionline);
                }
                indent = "    ";
                writer.println(indent + " behaviours: ");

            }
            writer.println();
        }

        writer.flush();
    }
}
