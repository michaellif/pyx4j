/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.other;




import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.impl.BuildingListerGadget;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class BuildingListerGadgetFactory extends AbstractGadgetFactory<BuildingListerGadgetMetadata> {

    public BuildingListerGadgetFactory() {
        super(BuildingListerGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<BuildingListerGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new BuildingListerGadget((BuildingListerGadgetMetadata) gadgetMetadata);
    }

}
