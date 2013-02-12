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
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system.encryption;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.encryption.EncryptedStorageCurrentKey;
import com.propertyvista.operations.domain.encryption.EncryptedStoragePublicKey;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

public class EncryptedStorageFacadeImpl implements EncryptedStorageFacade {

    private static Map<Key, Boolean> activeKeys = new HashMap<Key, Boolean>();

    @Override
    public Key getCurrentPublicKey() {
        EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));
        if (current != null) {
            return current.current().getPrimaryKey();
        } else {
            return null;
        }
    }

    @Override
    public byte[] encrypt(Key publicKeyKey, byte[] data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] decrypt(Key publicKeyKey, byte[] data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EncryptedStorageDTO getSystemState() {
        EncryptedStorageDTO infoDto = EntityFactory.create(EncryptedStorageDTO.class);

        EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));

        EntityQueryCriteria<EncryptedStoragePublicKey> criteria = EntityQueryCriteria.create(EncryptedStoragePublicKey.class);
        for (EncryptedStoragePublicKey publicKey : Persistence.service().query(criteria)) {
            EncryptedStorageKeyDTO keyDto = EntityFactory.create(EncryptedStorageKeyDTO.class);

            if ((current != null) && publicKey.getPrimaryKey().equals(current.current().getPrimaryKey())) {
                keyDto.isCurrent().setValue(Boolean.TRUE);
            }
            keyDto.decryptionEnabled().setValue(activeKeys.get(publicKey.getPrimaryKey()) != null);
            infoDto.keys().add(keyDto);
        }

        return infoDto;
    }

    @Override
    public byte[] createNewKeyPair(char[] password) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void makeCurrent(Key publicKeyKey) {
        EncryptedStorageCurrentKey current = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStorageCurrentKey.class));
        if (current == null) {
            current = EntityFactory.create(EncryptedStorageCurrentKey.class);
        }
        current.current().setPrimaryKey(publicKeyKey);
        Persistence.service().persist(current);

    }

    @Override
    public void startKeyRotation(Key publicKeyKey) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uploadPrivateKey(Key publicKeyKey, byte[] encryptedPrivateKeyData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void activate(Key publicKeyKey, char[] passwrord) {
        EncryptedStoragePublicKey publicKey = Persistence.service().retrieve(EntityQueryCriteria.create(EncryptedStoragePublicKey.class));
        // asset
        activeKeys.put(publicKeyKey, Boolean.TRUE);
    }

}
