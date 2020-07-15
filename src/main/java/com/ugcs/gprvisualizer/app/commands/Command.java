package com.ugcs.gprvisualizer.app.commands;

import com.github.thecoldwine.sigrun.common.ext.SgyFile;
import com.ugcs.gprvisualizer.app.ProgressListener;

public interface Command extends BaseCommand {
		
	void execute(SgyFile file, ProgressListener listener);

}
