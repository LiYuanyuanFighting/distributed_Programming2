package it.polito.dp2.NFFG.sol3.client1;

import it.polito.dp2.NFFG.sol3.client1.NFFGClient;
import it.polito.dp2.NFFG.lab3.NFFGClientException;
// import it.polito.dp2.NFFG.lab3.ServiceException;
// import it.polito.dp2.NFFG.lab3.UnknownNameException;

public class NFFGClientFactory extends it.polito.dp2.NFFG.lab3.NFFGClientFactory {

	@Override
	public NFFGClient newNFFGClient() throws NFFGClientException {
		try {
			NFFGClient client = new NFFGClient();
			return client;
		//} catch (UnknownNameException | ServiceException e) {
		}	catch (Exception e) {
			e.printStackTrace();
			throw new NFFGClientException();
		}
	}

}
