/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.experimental;

import com.propertyvista.portal.domain.pt.Pet;

/**
 * This is a clean interface that is shielded from the gui, it can be easily tested
 * writing unit tests for this is easy and straightforward
 */
public class PetService {
    public Pet findPetByName(String name) {
        // query for pet and return it
        return null;
    }

    public void savePet(Pet pet) {
        // perform any needed business validation
        // can throw validation exceptions that should be handled 
    }
}
