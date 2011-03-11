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
package com.propertyvista.portal.server.pt.experimental;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.rpc.pt.experimental.PetServiceCall;
import com.propertyvista.portal.rpc.pt.experimental.PetServiceCall.Method;
import com.propertyvista.portal.rpc.pt.experimental.ServiceCall;
import com.propertyvista.portal.rpc.pt.experimental.ServiceResult;
import com.propertyvista.portal.rpc.pt.experimental.VistaPortalService;
import com.propertyvista.portal.server.experimental.PetService;

/**
 * A single file to map and control all the services, if needed can be broken down into
 * multiple classes
 * 
 */
@SuppressWarnings("serial")
public class VistaPortalServiceImpl extends RemoteServiceServlet implements VistaPortalService {
    @Override
    public ServiceResult execute(ServiceCall call) {
        ServiceResult result = new ServiceResult();
        try {
            if (call.getName() == ServiceCall.Name.Pet) {
                PetServiceCall petCall = (PetServiceCall) call;
                PetService petService = new PetService();
                if (petCall.getMethod() == Method.findPetByName) {
                    String petName = (String) petCall.getArg(0);
                    result.setResult(petService.findPetByName(petName));
                } else if (petCall.getMethod() == Method.savePet) {
                    Pet pet = (Pet) petCall.getArg(0);
                    petService.savePet(pet);
                }
            }
        } catch (Exception e) {
            // result.setThroable or any other way you want to let the client know
            // depending on the type of exception
        }
        return result;
    }
}
