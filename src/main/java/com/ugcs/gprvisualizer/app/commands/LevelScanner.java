package com.ugcs.gprvisualizer.app.commands;

import java.util.List;

import com.github.thecoldwine.sigrun.common.ext.AmplitudeMatrix;
import com.github.thecoldwine.sigrun.common.ext.SgyFile;
import com.github.thecoldwine.sigrun.common.ext.Trace;
import com.ugcs.gprvisualizer.draw.Change;
import com.ugcs.gprvisualizer.math.HorizontalProfile;

/**
 * Find ground level. Put ground value to trace.maxindex
 * @author Kesha
 *
 */
public class LevelScanner implements Command {

	@Override
	public void execute(SgyFile file) {
		
		// copy sgyfile
		SgyFile file2 = file.copy(); 
		
		// remove noise
		new BackgroundNoiseRemover().execute(file2);
		
		
		List<Trace> lst = file2.getTraces();

		AmplitudeMatrix am = new AmplitudeMatrix();
		am.init(lst);
		file.groundProfile = am.findLevel();
	}

	@Override
	public String getButtonText() {

		return "Find ground level";
	}

	@Override
	public Change getChange() {
		return Change.traceValues;
	}

	
	
}
