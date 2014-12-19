/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 22, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.propertyvista.yardi.mock.model.domain.YardiRentableItem;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager.BuildingBuilder;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager.RentableItemBuilder;

public class RentableItemBuilderImpl implements RentableItemBuilder {

    private final YardiRentableItem item;

    private final BuildingBuilder parent;

    RentableItemBuilderImpl(YardiRentableItem item, BuildingBuilder parent) {
        this.item = item;
        this.parent = parent;
    }

    @Override
    public RentableItemBuilder setDescription(String text) {
        item.description().setValue(text);
        return this;
    }

    @Override
    public BuildingBuilder done() {
        return parent;
    }
}
