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
package com.propertyvista.portal.rpc.pt.experimental;

public class PetServiceCall extends ServiceCall {
    public enum Method {
        getAllPets, findPet, findPetByName, removePet, savePet
    }

    public enum Argument {
        name, id
    }

    private Method method;

    public PetServiceCall() {
        name = Name.Pet;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
