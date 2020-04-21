package com.ugcs.gprvisualizer.gpr;

public class PaletteBuilder {

	double delit =25.0;
	int start = 0;
	public PaletteBuilder() {
		
	}

	public PaletteBuilder(double delit, int start) {
		this.delit = delit;
		
		this.start = start;
	}
	
	public int[] build() {
		
		int[] palette = new int[15000];
		
		for(int i=0; i< palette.length; i++){
			
			double t= ((double)i+start) / delit;
			
			int r = ((int)((Math.cos(t*1.50)+1)/2 * 255.0 ) ) & 0xff;
			int g = ((int)((Math.cos(t*1.23)+1)/2 * 255.0 ) ) & 0xff;
			int b = ((int)((Math.cos(t*1.00)+1)/2 * 255.0 ) ) & 0xff;
			int alpha = (int) (i<55.0 ? i/55.0 * 180.0 : 180.0);
			
			palette[i] = r + (g << 8) + (b << 16) + (alpha << 24);
		}
		
		
		return palette;
	}

	public int[] build2() {
		
		int[] palette = new int[15000];
		
		for(int i=0; i< palette.length; i++){
			
			double t= ((double)i+start) / delit;
			
			int r = ((int)((Math.sin(t*1.00)+1)/2 * 255.0 ) ) & 0xff;
			int g = ((int)((Math.cos(t*1.43)+1)/2 * 255.0 ) ) & 0xff;
			int b = ((int)((Math.cos(t*0.78)+1)/2 * 255.0 ) ) & 0xff;
			int alpha = (int) (i<30.0 ? i/30.0 * 180.0 : 180.0);
			
			palette[i] = r + (g << 8) + (b << 16) + (alpha << 24);
		}
		
		
		return palette;
	}

	
}
