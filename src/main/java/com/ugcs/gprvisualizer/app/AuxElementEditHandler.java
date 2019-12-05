package com.ugcs.gprvisualizer.app;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.thecoldwine.sigrun.common.ext.AreaType;
import com.github.thecoldwine.sigrun.common.ext.AuxElement;
import com.github.thecoldwine.sigrun.common.ext.AuxRect;
import com.github.thecoldwine.sigrun.common.ext.SgyFile;
import com.github.thecoldwine.sigrun.common.ext.Trace;
import com.github.thecoldwine.sigrun.common.ext.TraceSample;
import com.github.thecoldwine.sigrun.common.ext.VerticalCutField;
import com.ugcs.gprvisualizer.app.auxcontrol.BaseObject;
import com.ugcs.gprvisualizer.app.auxcontrol.FoundPlace;
import com.ugcs.gprvisualizer.draw.Change;
import com.ugcs.gprvisualizer.draw.WhatChanged;
import com.ugcs.gprvisualizer.gpr.Model;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class AuxElementEditHandler implements MouseHandler {

	private Model model;
	private CleverImageView cleverView;
	
	//private AuxElement selectedAuxElement;
	private VerticalCutField field;
	private boolean moved = false;
	
	//private List<BaseObject> controls = null;
	private BaseObject selected;
	private MouseHandler mouseInput;
	
	private Button addBtn = new Button("add hyperbola");
	private Button addSurfaceBtn = new Button("add surface");
	private Button addFoundBtn = new Button("add found");
	private Button delBtn = new Button("delete");
	

	public AuxElementEditHandler(CleverImageView cleverView) {
		this.cleverView = cleverView;
		this.model = this.cleverView.model;
		field = cleverView.getField();
		
		initButtons();
	}

	@Override
	public boolean mousePressHandle(Point localPoint, VerticalCutField vField) {
		
		boolean processed = false;
		if(model.getControls() != null) {
			processed = processPress(model.getControls(), localPoint, vField);
		}
		
		if(!processed && selected != null) {
			processed = selected.mousePressHandle(localPoint, vField);
			if(processed) {
				mouseInput = selected;
			}
		}
		
		if(!processed) {
			processed = processPress1(cleverView.model.getAuxElements(), localPoint, vField);
		}
		
		if(!processed) {
			//deselect
			mouseInput = null;
			selected = null;
			model.setControls(null);
		}
		
		moved = false;

		if(processed) {
			cleverView.repaintEvent();
		}

		return processed;
	}
	
	public Node getRight() {
		HBox box = new HBox( addBtn, addSurfaceBtn, addFoundBtn, delBtn);
		
		return box;
	}
	
	protected void initButtons(){
		delBtn.setOnAction(e -> {		
			
			if(selected != null) {
				System.out.println("removeselected ");
				cleverView.model.getAuxElements().remove(selected);
				
				mouseInput = null;
				selected = null;
				model.setControls(null);
			}
			
			model.updateAuxElements();
			
			cleverView.repaintEvent();
		});
		
		addBtn.setOnAction(e -> {				
				
				
				SgyFile sf = model.getSgyFileByTrace(field.getSelectedTrace());
				
				if(sf != null) {
					AuxRect rect = new AuxRect(field.getSelectedTrace(), field.getStartSample()+30, sf.getOffset());
					
					sf.getAuxElements().add(rect);
					model.updateAuxElements();
				}
				cleverView.repaintEvent();
			}
		);
		addSurfaceBtn.setOnAction(e -> {				
			
			SgyFile sf = model.getSgyFileByTrace(field.getSelectedTrace());
			
			Trace tr1 = sf.getTraces().get(0);
			Trace tr2 = sf.getTraces().get(sf.getTraces().size()-1);
			
			AuxRect rect = new AuxRect(
				tr1.indexInSet, tr2.indexInSet,
				20, 80, sf.getOffset());
				rect.setType(AreaType.Surface);

				
			if(sf != null) {
				sf.getAuxElements().add(rect);
				model.updateAuxElements();
			}
			cleverView.repaintEvent();
		});
		
		addFoundBtn.setOnAction(e -> {				
			
			SgyFile sf = model.getSgyFileByTrace(field.getSelectedTrace());
			
			Trace tr = model.getFileManager().getTraces().get(field.getSelectedTrace());
			
			FoundPlace rect = new FoundPlace(tr, sf.getOffset());
				
			if(sf != null) {
				sf.getAuxElements().add(rect);
				model.updateAuxElements();
			}
			cleverView.repaintEvent();
		});
		
	}

	private boolean processPress(List<BaseObject> controls2, Point localPoint, VerticalCutField vField) {
		for(BaseObject o : controls2) {
			if(o.isPointInside(localPoint, vField)) {
				
				o.mousePressHandle(localPoint, vField);
				mouseInput = o;
				
				return true;
			}
		}
		
		return false;
	}

	private boolean processPress1(List<BaseObject> controls2, Point localPoint, VerticalCutField vField) {
		for(BaseObject o : controls2) {
			if(o.isPointInside(localPoint, vField)) {
				
				selected  = o;
				model.setControls(null);
				List<BaseObject> c = selected.getControls();
				if(c != null) {
					model.setControls(c);
				}
				
				return true;
			}
		}
		
		return false;
	}
	/**
				

	 */

	@Override
	public boolean mouseReleaseHandle(Point localPoint, VerticalCutField vField) {

		if(mouseInput != null) {			
			mouseInput.mouseReleaseHandle(localPoint, vField);
			mouseInput = null;
			
			cleverView.repaintEvent();
			return true;
		}
		
		
		return false;
	}

	@Override
	public boolean mouseMoveHandle(Point localPoint, VerticalCutField vField) {

		if(mouseInput != null) {			
			mouseInput.mouseMoveHandle(localPoint, vField);
			
			cleverView.repaintEvent();
			
			return true;
		}
		
		
		
		return false;
	}


}
