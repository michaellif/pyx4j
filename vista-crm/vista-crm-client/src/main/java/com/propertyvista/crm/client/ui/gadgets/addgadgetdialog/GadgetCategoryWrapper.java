/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 7, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.addgadgetdialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;

public class GadgetCategoryWrapper {
    private final String category;

    private final List<IGadgetFactory> gadgets;

    private final boolean hasSubCategories;

    private final String toString;

    private final List<String> history;

    /**
     * @param category
     *            must not be <code>null</code>
     * @param gadgets
     *            must not be <code>null</code>, and contain at least one gadget.
     */
    public GadgetCategoryWrapper(String category, List<IGadgetFactory> gadgets) {
        this(category, Collections.<String> emptyList(), gadgets);
    }

    private GadgetCategoryWrapper(String category, List<String> history, List<IGadgetFactory> gadgets) {
        this.gadgets = gadgets;
        this.category = category;
        this.toString = category + " (" + gadgets.size() + ")";
        this.history = new LinkedList<String>(history);
        this.history.add(category);

        boolean hasSubCategories = false;
        for (IGadgetFactory gadget : gadgets) {
            for (String c : gadget.getCategories()) {
                if (!this.history.contains(c)) {
                    hasSubCategories = true;
                    break;
                }
            }
        }
        this.hasSubCategories = hasSubCategories;
    }

    public List<GadgetCategoryWrapper> partition() {
        List<GadgetCategoryWrapper> partition;
        if (hasSubCategories) {
            Map<String, List<IGadgetFactory>> partitionMap = new HashMap<String, List<IGadgetFactory>>();
            for (IGadgetFactory gadget : gadgets) {
                for (String subCategory : gadget.getCategories()) {
                    if (!history.contains(subCategory)) {
                        if (!partitionMap.containsKey(subCategory)) {
                            partitionMap.put(subCategory, new LinkedList<IGadgetFactory>());
                        }
                        partitionMap.get(subCategory).add(gadget);
                    }
                }
            }

            partition = new ArrayList<GadgetCategoryWrapper>(partitionMap.size());
            for (Entry<String, List<IGadgetFactory>> entry : partitionMap.entrySet()) {
                partition.add(new GadgetCategoryWrapper(entry.getKey(), history, entry.getValue()));
            }
        } else {
            partition = new ArrayList<GadgetCategoryWrapper>(1);
            partition.add(new GadgetCategoryWrapper(category, history, gadgets));
        }
        return partition;
    }

    public Collection<IGadgetFactory> getGadgets() {
        return Collections.unmodifiableCollection(this.gadgets);
    }

    public boolean hasSubCategories() {
        return hasSubCategories;
    }

    @Override
    public String toString() {
        return toString;
    }

}