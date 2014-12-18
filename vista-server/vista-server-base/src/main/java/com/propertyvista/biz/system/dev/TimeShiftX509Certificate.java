/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2014
 * @author vlads
 */
package com.propertyvista.biz.system.dev;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import com.propertyvista.biz.system.WorldDateManager;

public class TimeShiftX509Certificate extends X509Certificate {

    private final X509Certificate wrapped;

    public TimeShiftX509Certificate(X509Certificate wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        wrapped.checkValidity(WorldDateManager.getWorldTime());
    }

    @Override
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        wrapped.checkValidity(WorldDateManager.toWorldTime(date));
    }

    @Override
    public int getBasicConstraints() {
        return wrapped.getBasicConstraints();
    }

    @Override
    public Set<String> getCriticalExtensionOIDs() {
        return wrapped.getCriticalExtensionOIDs();
    }

    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return wrapped.getEncoded();
    }

    @Override
    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        return wrapped.getExtendedKeyUsage();
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        return wrapped.getExtensionValue(oid);
    }

    @Override
    public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
        return wrapped.getIssuerAlternativeNames();
    }

    @Override
    public Principal getIssuerDN() {
        return wrapped.getIssuerDN();
    }

    @Override
    public boolean[] getIssuerUniqueID() {
        return wrapped.getIssuerUniqueID();
    }

    @Override
    public X500Principal getIssuerX500Principal() {
        return wrapped.getIssuerX500Principal();
    }

    @Override
    public boolean[] getKeyUsage() {
        return wrapped.getKeyUsage();
    }

    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        return wrapped.getNonCriticalExtensionOIDs();
    }

    @Override
    public Date getNotAfter() {
        return wrapped.getNotAfter();
    }

    @Override
    public Date getNotBefore() {
        return wrapped.getNotBefore();
    }

    @Override
    public PublicKey getPublicKey() {
        return wrapped.getPublicKey();
    }

    @Override
    public BigInteger getSerialNumber() {
        return wrapped.getSerialNumber();
    }

    @Override
    public String getSigAlgName() {
        return wrapped.getSigAlgName();
    }

    @Override
    public String getSigAlgOID() {
        return wrapped.getSigAlgOID();
    }

    @Override
    public byte[] getSigAlgParams() {
        return wrapped.getSigAlgParams();
    }

    @Override
    public byte[] getSignature() {
        return wrapped.getSignature();
    }

    @Override
    public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        return wrapped.getSubjectAlternativeNames();
    }

    @Override
    public Principal getSubjectDN() {
        return wrapped.getSubjectDN();
    }

    @Override
    public boolean[] getSubjectUniqueID() {
        return wrapped.getSubjectUniqueID();
    }

    @Override
    public X500Principal getSubjectX500Principal() {
        return wrapped.getSubjectX500Principal();
    }

    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        return wrapped.getTBSCertificate();
    }

    @Override
    public int getVersion() {
        return wrapped.getVersion();
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return wrapped.hasUnsupportedCriticalExtension();
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }

    @Override
    public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException,
            SignatureException {
        wrapped.verify(key, sigProvider);
    }

    @Override
    public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        wrapped.verify(key);
    }

}
