/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 15, 2015
 * @author ernestog
 */
package com.propertyvista.biz.preloader.pmc;

public interface CommunicationsHandler {

//    private static CommunicationsHandler instance;
//
//    private CommunicationsHandler() {
//
//    }
//
//    public static CommunicationsHandler instance() {
//        if (instance == null) {
//            synchronized (CommunicationsHandler.class) {
//                if (instance == null) {
//                    instance = new CommunicationsHandler();
//                }
//            }
//        }
//
//        return instance;
//    }
//
//    public void startCommunications() {
//        synchronized (instance) {
//            ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(false);
//            Mail.getMailService().setDisabled(false);
//        }
//    }
//
//    public void stopCommunications() {
//        synchronized (instance) {
//            Mail.getMailService().setDisabled(true);
//            ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
//        }
//    }

    public void startCommunications();

    public void stopCommunications();

//    public static void startCommunications() {
//        ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(false);
//        Mail.getMailService().setDisabled(false);
//    }
//
//    public static void stopCommunications() {
//        Mail.getMailService().setDisabled(true);
//        ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
//    }

}
