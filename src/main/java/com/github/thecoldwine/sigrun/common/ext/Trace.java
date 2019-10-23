package com.github.thecoldwine.sigrun.common.ext;

import com.github.thecoldwine.sigrun.common.TraceHeader;

public class Trace {
    
    private final Block headerBlock;
    private final Block dataBlock;
    
    private final TraceHeader header;
    private float[] originalvalues;
    private float[] normvalues;
    
    private LatLon latLon;
    private boolean active = true;

    public Trace(Block headerBlock, Block dataBlock, TraceHeader header, float[] originalvalues, LatLon latLon) {
        
    	
        this.header = header;
        this.headerBlock = headerBlock; 
        this.dataBlock = dataBlock;
        this.originalvalues = originalvalues;
        this.latLon = latLon;
    }

    public TraceHeader getHeader() {
        return header;
    }
    
    public LatLon getLatLon() {
    	return latLon;
    }

    public float[] getOriginalValues() {
    	return originalvalues;
    }
    
    public float[] getNormValues() {
    	return normvalues;
    }

	public Block getHeaderBlock() {
		return headerBlock;
	}

	public Block getDataBlock() {
		return dataBlock;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
	
}