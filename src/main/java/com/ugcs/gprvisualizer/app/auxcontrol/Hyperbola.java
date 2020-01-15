package com.ugcs.gprvisualizer.app.auxcontrol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.github.thecoldwine.sigrun.common.ext.Field;
import com.github.thecoldwine.sigrun.common.ext.ResourceImageHolder;
import com.github.thecoldwine.sigrun.common.ext.TraceSample;
import com.github.thecoldwine.sigrun.common.ext.VerticalCutField;
import com.github.thecoldwine.sigrun.common.ext.VerticalCutPart;

public class Hyperbola implements BaseObject {

	private int leftWidth = 100;
	private int rightWidth = 100;
	private int thickness = 30;
	private int hyperkfcInt = 60;

	private VerticalCutPart offset;
	private DragAnchor pinacle;
	private DragAnchor left;
	private DragAnchor right;
	private DragAnchor thick;
	private DragAnchor hyperkfc;
	
	public Hyperbola(int trace, int sample, VerticalCutPart offset) {
		this.offset = offset;
		initDragAnchors();
		
		pinacle.setTrace(offset.globalToLocal(trace));
		pinacle.setSample(sample);
		
		left.setTrace(offset.globalToLocal(trace - leftWidth));
		right.setTrace(offset.globalToLocal(trace + rightWidth));
		thick.setSample(sample + thickness);
		hyperkfc.setSample(sample + hyperkfcInt);
	}
	
	public Hyperbola(JSONObject json, VerticalCutPart offset) {
		this.offset = offset;
		
		initDragAnchors();
		
		pinacle.setTrace((int)(long)(Long)json.get("tracePinacle"));
		pinacle.setSample((int)(long)(Long)json.get("samplePinacle"));
		thickness = ((int)(long)(Long)json.get("thickness"));
		leftWidth = ((int)(long)(Long)json.get("leftWidth"));
		rightWidth = ((int)(long)(Long)json.get("rightWidth"));
		hyperkfcInt = ((int)(long)(Long)json.get("hyperkfc"));
		
		//
		left.setTrace(pinacle.getTrace() - leftWidth);
		right.setTrace(pinacle.getTrace() + rightWidth);
		thick.setSample(pinacle.getSample() + thickness);
		hyperkfc.setSample(pinacle.getSample() + hyperkfcInt);
		
	}

	private void initDragAnchors() {
		
		hyperkfc = new DragAnchor(ResourceImageHolder.IMG_WIDTH, AlignRect.CENTER, offset) {
			public void signal(Object obj) {
				hyperkfcInt = hyperkfc.getSample() - pinacle.getSample();
			}
			
			public int getTrace() {
				return pinacle.getTrace() + rightWidth;
			}
		};
		
		pinacle = new DragAnchor(ResourceImageHolder.IMG_HOR_SLIDER, AlignRect.CENTER, offset) {
			public void signal(Object obj) {
				
				
				left.setTrace(pinacle.getTrace() - leftWidth);
				right.setTrace(pinacle.getTrace() + rightWidth);
				thick.setSample(pinacle.getSample() + thickness);
				hyperkfc.setSample(pinacle.getSample() + hyperkfcInt);
			}
		};
		
		left = new DragAnchor(ResourceImageHolder.IMG_VER_SLIDER, AlignRect.CENTER, offset) {
			public void signal(Object obj) {
				leftWidth = pinacle.getTrace() - left.getTrace();				
			}
			
			public int getSample() {
				return pinacle.getSample();
			}
		};
		right = new DragAnchor(ResourceImageHolder.IMG_VER_SLIDER, AlignRect.CENTER, offset) {
			public void signal(Object obj) {
				rightWidth = right.getTrace() - pinacle.getTrace();
			}
			
			public int getSample() {
				return pinacle.getSample();
			}
		};
		thick = new DragAnchor(ResourceImageHolder.IMG_HOR_SLIDER, AlignRect.CENTER, offset) {
			public void signal(Object obj) {
				thickness = thick.getSample() - pinacle.getSample();
			}
			
			public int getTrace() {
				return pinacle.getTrace();
			}
		};

	}
	
	@Override
	public boolean mousePressHandle(Point localPoint, VerticalCutField vField) {		
		return isPointInside(localPoint, vField);
	}

	@Override
	public boolean mouseReleaseHandle(Point localPoint, VerticalCutField vField) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoveHandle(Point point, VerticalCutField vField) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawOnMap(Graphics2D g2, Field hField) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawOnCut(Graphics2D g2, VerticalCutField vField) {
		drawHyperbola(g2, vField);
		
	}

	@Override
	public Rectangle getRect(VerticalCutField vField) {
		Point lt = vField.traceSampleToScreen(new TraceSample(getTraceStartGlobal(), pinacle.getSample()-10));
		Point rb = vField.traceSampleToScreen(new TraceSample(getTraceFinishGlobal(), pinacle.getSample()+thickness));
		return new Rectangle(lt.x, lt.y, rb.x - lt.x, rb.y - lt.y);
	}

	@Override
	public void signal(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BaseObject> getControls() {

			return
				Arrays.asList(pinacle, left, right, thick, hyperkfc); 

	}

	@Override
	public void saveTo(JSONObject json) {
		json.put("tracePinacle", pinacle.getTrace());
		json.put("samplePinacle", pinacle.getSample());
		json.put("leftWidth", leftWidth);
		json.put("rightWidth", rightWidth);
		json.put("thickness", thickness);
		json.put("hyperkfc", hyperkfcInt);
		
		int y = pinacle.getSample();
		
		int[] topCut = getCutArray(y, y);
		
		JSONArray arr = new JSONArray();
		for(int i : topCut) {
			arr.add(i);
		}		
		json.put("topCut", arr);
		
		int[] botCut = getCutArray(y+thickness, y);
		JSONArray arr2 = new JSONArray();
		for(int i : botCut) {
			arr2.add(i);
		}		
		json.put("botCut", arr2);
		
	}

	private int[] getCutArray(int y, int top) {
		int[] topCut = new int[leftWidth+rightWidth];		
		//double kf = AppContext.model.getSettings().hyperkfc / 100.0;
		double kf = hyperkfcInt / 100.0;
		for(int i=0; i<topCut.length; i++) {
			double x=(i-leftWidth) * kf;
			double c = Math.sqrt(x*x+y*y);
			topCut[i] = (int)c - top;
		}
		return topCut;
	}

	@Override
	public boolean mousePressHandle(Point2D point, Field field) {
		return false;
	}

	@Override
	public boolean isPointInside(Point localPoint, VerticalCutField vField) {
		
		Rectangle rect = getRect(vField);
		
		return rect.contains(localPoint);
	}

	private void drawHyperbola(Graphics2D g2, VerticalCutField vField) {
		
		//Rectangle rect = getRect(vField); 
		//g2.setColor(Color.RED);
		//g2.drawRect(rect.x, rect.y, rect.width, rect.height);
		
		
		g2.setColor(Color.YELLOW);
		//double kf = AppContext.model.getSettings().hyperkfc / 100.0;
		double kf = hyperkfcInt / 100.0;
		int tr = getTracePinacleGlobal();//tracePinacle.intValue();
		double y = pinacle.getSample();
		drawHyperbolaLine(g2, vField, kf, tr, y);
		drawHyperbolaLine(g2, vField, kf, tr, y+thickness);
	}

	private void drawHyperbolaLine(Graphics2D g2, VerticalCutField vField, double kf, int tr, double y) {
		Point prev = null;
		
		int s = getTraceStartGlobal(); //tracePinacle.intValue() - leftWidth;
		int f = getTraceFinishGlobal();//tracePinacle.intValue() + rightWidth;
		
		
		for(int i=s; i<= f; i++) {
			
			double x=(i-tr) * kf;
			double c = Math.sqrt(x*x+y*y);
			
			Point lt = vField.traceSampleToScreen(new TraceSample(i, (int)c));
			if(prev != null) {
				g2.drawLine(prev.x, prev.y, lt.x, lt.y);				
			}
			
			prev = lt;
		}
	}

	public int getTracePinacleGlobal() {
		return offset.localToGlobal(pinacle.getTrace());
	}
	
	public int getTraceStartGlobal() {
		return offset.localToGlobal(left.getTrace());
	}
	
	public int getTraceFinishGlobal() {
		return offset.localToGlobal(right.getTrace());
	}
}
