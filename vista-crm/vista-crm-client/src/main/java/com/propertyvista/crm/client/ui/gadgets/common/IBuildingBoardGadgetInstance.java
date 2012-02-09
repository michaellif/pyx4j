/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.List;

import com.pyx4j.commons.Key;

/**
 * Gadget instance that is supported by building dashboard, i.e a gadget that implements this interface is expected to display data that is
 * dependent on a set of buildings.
 */
// TODO add marker interface for gadget metadata, and thing how to bind between metadata building interface and this interface as painless as possible
public interface IBuildingBoardGadgetInstance extends IGadgetInstance {

    void setBuildings(List<Key> buildings);

}