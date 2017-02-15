/**
 * 
 */
package it.polito.dp2.NFFG.lab3.test1;

import it.polito.dp2.NFFG.FactoryConfigurationError;
import it.polito.dp2.NFFG.lab3.NFFGClientException;

/**
 * Defines a factory API that enables applications to obtain one or more objects
 * implementing the {@link NFFGClient3} interface.
 *
 */
public abstract class NFFGClient3Factory {

	private static final String propertyName = "it.polito.dp2.NFFG.NFFGClient3Factory";
	
	protected NFFGClient3Factory() {}
	
	/**
	 * Obtain a new instance of a <tt>NFFGClient3Factory</tt>.
	 * 
	 * <p>
	 * This static method creates a new factory instance. This method uses the
	 * <tt>it.polito.dp2.NFFG.NFFGClient3Factory</tt> system property to
	 * determine the NFFGClient3Factory implementation class to load.
	 * </p>
	 * <p>
	 * Once an application has obtained a reference to a
	 * <tt>NFFGClient3Factory</tt> it can use the factory to obtain a new
	 * {@link NFFGClient3} instance.
	 * </p>
	 * 
	 * @return a new instance of a <tt>NFFGClient3Factory</tt>.
	 * 
	 * @throws FactoryConfigurationError if the implementation is not available 
	 * or cannot be instantiated.
	 */
	public static NFFGClient3Factory newInstance() throws FactoryConfigurationError {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if(loader == null) {
			loader = NFFGClient3Factory.class.getClassLoader();
		}
		
		String className = System.getProperty(propertyName);
		if (className == null) {
			throw new FactoryConfigurationError("cannot create a new instance of a NFFGClient3Factory"
												+ "since the system property '" + propertyName + "'"
												+ "is not defined");
		}
		
		try {
			Class<?> c = (loader != null) ? loader.loadClass(className) : Class.forName(className);
			return (NFFGClient3Factory) c.newInstance();
		} catch (Exception e) {
			throw new FactoryConfigurationError(e, "error instantiatig class '" + className + "'.");
		}
	}
	
	
	/**
	 * Creates a new instance of a {@link NFFGClient3} implementation.
	 * 
	 * @return a new instance of a {@link NFFGClient3} implementation.
	 * @throws NFFGClientException if an implementation of {@link NFFGClient3} cannot be created.
	 */
	public abstract NFFGClient3 newNFFGClient3() throws NFFGClientException;
}