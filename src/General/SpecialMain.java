package General;

public interface SpecialMain {
	// Setup threads and data you need to startup before your networks are run
	void SetupStartup();
	//setup threads that track additional information from your neural networks
	void AfterStartup();
}
