package Backpropagate;

import General.SpecialNetManager;

public interface BackpropagateManager extends SpecialNetManager {
		// This is called at the beginning of each iteration of learning
		// this is for data that needs to be reset or generated on each iteration of learning
		void BackpropagationSetup();
		// This runs before the backpropagation step is called each time
		// this is for data that needs to be reset or generated on each iteration of backpropagtion
		void BackIterationHandling();
		//this is the algorithm to determine if each output neuron should have activated or not.
		//This is where you will use the data you set up in BackIterationHandling
		public void setAct();
}
