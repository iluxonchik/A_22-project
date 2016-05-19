package pt.upa.broker.ws.cli;

import pt.upa.broker.exception.BrokerClientException;

public class BrokerClient extends FrontEnd {

    /**
     * Instantiate a BrokerClient from UDDI url and wsName.
     *
     * @param uddiURL
     *            UDDI server address
     * @param wsName
     *            name of the broker to connect to
     * @throws BrokerClientException
     */
    public BrokerClient(String uddiURL, String wsName) {
	super(uddiURL, wsName);
    }

    /**
     * Instantiate BrokerClient directly from an endpoint address
     *
     * @param endpointAddress
     *            the endpoint address of the
     *            {@link pt.upa.broker.ws.BrokerPortType}
     */
    public BrokerClient(String endpointAddress) {
	super(endpointAddress);
    }

}
