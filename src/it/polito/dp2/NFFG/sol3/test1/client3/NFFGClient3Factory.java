package it.polito.dp2.NFFG.sol3.test1.client3;

import it.polito.dp2.NFFG.lab3.NFFGClientException;

public class NFFGClient3Factory extends it.polito.dp2.NFFG.lab3.test1.NFFGClient3Factory {

	@Override
	public NFFGClient3 newNFFGClient3() throws NFFGClientException {try {
		NFFGClient3 client = new NFFGClient3();
		return client;
	//} catch (UnknownNameException | ServiceException e) {
	}	catch (Exception e) {
		e.printStackTrace();
		throw new NFFGClientException();
	}
	}

}
