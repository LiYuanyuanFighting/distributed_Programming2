package it.polito.dp2.NFFG.sol3.client2;

public class MyNamedEntityReader implements it.polito.dp2.NFFG.NamedEntityReader {

	private String name;
	
	public MyNamedEntityReader(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

}
