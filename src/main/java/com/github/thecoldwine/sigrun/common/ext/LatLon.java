package com.github.thecoldwine.sigrun.common.ext;

import java.util.Locale;

import com.ugcs.gprvisualizer.math.CoordinatesMath;

public class LatLon {

	private double latDgr;
	private double lonDgr;
	
	public String toString() {
		
		return dgrToDegreeMinSec(getLatDgr(), true) +  "  " + dgrToDegreeMinSec(getLonDgr(), false);
		
		//return getLatDgr() +  "   " + getLonDgr();
	}
	
	public LatLon(double latDgr, double lonDgr) {
		this.latDgr = latDgr;
		this.lonDgr = lonDgr;
	}
	
	public double getLatDgr() {
		return latDgr;
	}

	public double getLonDgr() {
		return lonDgr;
	}
	
	public void from(LatLon latLon) {
		this.latDgr = latLon.getLatDgr();
		this.lonDgr = latLon.getLonDgr();
	}

	private String dgrToDegreeMinSec(double dgr, boolean lat) {
		String postfix = "";
	
		if (lat) {
			postfix = dgr > 0 ? "N" : "S";			
		} else {
			postfix = dgr > 0 ? "E" : "W";
		}
		
		dgr = Math.abs(dgr);
		
		int justdgr = (int) dgr;
		int justmin = (int) ((dgr-justdgr) *60);
		double justsec = (dgr - (double) justdgr - (double) justmin / 60.0) * 3600;
		
		// 40°02'04.0"S 65°11'00.2"E
		return String.format(Locale.ROOT, "%d°%d'%.3f\"%s", justdgr, justmin, justsec, postfix);
		//return justdgr + "°" + justmin + "'" + justsec + "\"" + postfix;
		
	}
	
	public double getDistance(LatLon another) {
		
		return CoordinatesMath.measure(
				getLatDgr(), getLonDgr(), 
				another.getLatDgr(), another.getLonDgr());
		
		
	}
	
}
