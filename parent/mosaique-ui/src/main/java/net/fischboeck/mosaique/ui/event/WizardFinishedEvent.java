package net.fischboeck.mosaique.ui.event;

import net.fischboeck.mosaique.ui.main.BuilderConfiguration;

public class WizardFinishedEvent {

	private BuilderConfiguration		config;
	
	public WizardFinishedEvent(BuilderConfiguration config) {
		this.config = config;
	}
	
	public BuilderConfiguration getConfiguration() {
		return this.config;
	}
}
