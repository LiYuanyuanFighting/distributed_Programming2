package it.polito.dp2.NFFG.lab3.test1;

import it.polito.dp2.NFFG.lab3.ServiceException;

public interface NFFGClient3 {

	/**
	 * Sets the caching status of the NffgService
	 * @param c The caching status to be set
	 * @throws ServiceException if the caching status could not be set
	 */
	void setCaching(Caching c) throws ServiceException;
	
	
	/**
	 * gets the current caching status of the NffgService
	 * @return the current caching status of
	 * @throws ServiceException
	 */
	Caching getCaching() throws ServiceException;
	
}
