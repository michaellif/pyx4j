/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.encryptedstorage;

import com.pyx4j.site.client.ui.backoffice.prime.IPrimePane;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public interface EncryptedStorageView extends IPrimePane {

    interface Presenter extends IPrimePane.Presenter {

        // View actions:
        void activateCurrentKeyDecryption(char[] keyPassword);

        void deactivateDecryption();

        void createNewKey(char[] keyPassword);

        // Key related actions

        void makeCurrentKey(EncryptedStorageKeyDTO keyToActivate);

        void activateDecryption(EncryptedStorageKeyDTO keyToEnableDecryption, char[] password);

        void disableDecryption(EncryptedStorageKeyDTO keyToDisableDecryption);

        void startKeyRotation(EncryptedStorageKeyDTO key);
    }

    void setPresenter(Presenter presenter);

    void populate(EncryptedStorageDTO dto);

}
