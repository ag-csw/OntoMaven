package de.csw.ontomaven;

import org.apache.maven.plugin.logging.Log;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class LogProgressMonitor implements ReasonerProgressMonitor {
	
	private Log log;
	private int lastPercentage;
	
	public LogProgressMonitor( Log log ) {
		this.log = log;
	}

	@Override
	public void reasonerTaskStarted(String taskName) {
		log.info("Reasoner performing task: " + taskName);
	}

	@Override
	public void reasonerTaskStopped() {
		log.info("    ... finished");
		lastPercentage = 0;
	}

	@Override
	public void reasonerTaskProgressChanged(int value, int max) {
		if (max > 0) {
			int percent = value * 100 / max;
			if (percent > lastPercentage + 10) {
				log.info("    " + percent + "%" );
				lastPercentage = percent;
			}
		}
	}

	@Override
	public void reasonerTaskBusy() {
		log.info("    busy ...");
	}
}
