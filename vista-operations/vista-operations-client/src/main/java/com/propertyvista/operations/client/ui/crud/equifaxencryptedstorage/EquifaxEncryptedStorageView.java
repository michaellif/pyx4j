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
package com.propertyvista.operations.client.ui.crud.equifaxencryptedstorage;

import com.pyx4j.site.client.ui.IView;

import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public interface EquifaxEncryptedStorageView extends IView {

    interface Presenter extends IView.Presenter {

        void createNewKey(char[] keyPassword);

        void makeCurrentKey(EncryptedStorageKeyDTO keyToActivate);

        void decryptionEnable(EncryptedStorageKeyDTO keyToEnableDecryption, char[] password);

        void downloadPrivateKey(EncryptedStorageKeyDTO key);
    }

    void setPresenter(Presenter presenter);

    void populate(EncryptedStorageDTO dto);

}
