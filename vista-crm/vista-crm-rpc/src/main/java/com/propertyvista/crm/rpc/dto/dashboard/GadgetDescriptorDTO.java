/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-26
 * @author ArtyomB
 */
package com.propertyvista.crm.rpc.dto.dashboard;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetDescriptorDTO implements Serializable {

    private static final long serialVersionUID = 2849287478222328664L;

    String name;

    String description;

    Vector<String> keywords;

    GadgetMetadata proto;

    public GadgetDescriptorDTO() {
        name = null;
        description = null;
        keywords = null;
        proto = null;
    }

    public GadgetDescriptorDTO(String name, String localizedDescription, Collection<String> keywords, GadgetMetadata proto) {
        this.name = name;
        this.description = localizedDescription;
        this.proto = proto;
        this.keywords = new Vector<String>(keywords);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getKeywords() {
        return new HashSet<String>(keywords);
    }

    public GadgetMetadata getProto() {
        return proto;
    }

}
