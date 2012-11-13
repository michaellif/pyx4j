package com.propertyvista.oapi;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "ReceivableServiceImplService", targetNamespace = "http://oapi.propertyvista.com/", wsdlLocation = "http://localhost:8888/WS/ReceivableService?wsdl")
public class ReceivableServiceStub extends Service {

    private final static URL IMPL_SERVICE_WSDL_LOCATION;

    private final static WebServiceException IMPL_SERVICE_EXCEPTION;

    private final static QName IMPL_SERVICE_QNAME = new QName("http://oapi.propertyvista.com/", "ReceivableServiceImplService");

    private final static QName IMPL_PORT_QNAME = new QName("http://oapi.propertyvista.com/", "ReceivableServiceImplPort");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/WS/ReceivableService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        IMPL_SERVICE_WSDL_LOCATION = url;
        IMPL_SERVICE_EXCEPTION = e;
    }

    public ReceivableServiceStub() {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME);
    }

    public ReceivableServiceStub(WebServiceFeature... features) {
        super(getWsdlLocation(), IMPL_SERVICE_QNAME, features);
    }

    public ReceivableServiceStub(URL wsdlLocation) {
        super(wsdlLocation, IMPL_SERVICE_QNAME);
    }

    public ReceivableServiceStub(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, IMPL_SERVICE_QNAME, features);
    }

    public ReceivableServiceStub(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ReceivableServiceStub(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(name = "ReceivableServiceImplPort")
    public ReceivableService getReceivableServicePort() {
        return super.getPort(IMPL_PORT_QNAME, ReceivableService.class);
    }

    @WebEndpoint(name = "ReceivableServiceImplPort")
    public ReceivableService getReceivableServicePort(WebServiceFeature... features) {
        return super.getPort(IMPL_PORT_QNAME, ReceivableService.class, features);
    }

    private static URL getWsdlLocation() {
        if (IMPL_SERVICE_EXCEPTION != null) {
            throw IMPL_SERVICE_EXCEPTION;
        }
        return IMPL_SERVICE_WSDL_LOCATION;
    }

}
