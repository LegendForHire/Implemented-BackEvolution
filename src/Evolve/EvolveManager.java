package Evolve;

import General.SpecialNetManager;

public interface EvolveManager extends SpecialNetManager{
	//this is called when evolve begins.
	//in the case that this network doesn't run backpropagation this is called at the begginning of each iteration of learning.
	void EvolveSetup();
	//this is called after evolve finishes
	void EvolveTeardown();
}
