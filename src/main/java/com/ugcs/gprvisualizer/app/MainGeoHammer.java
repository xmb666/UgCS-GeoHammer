package com.ugcs.gprvisualizer.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.thecoldwine.sigrun.common.ext.ResourceImageHolder;
import com.ugcs.gprvisualizer.draw.Change;
import com.ugcs.gprvisualizer.draw.SmthChangeListener;
import com.ugcs.gprvisualizer.draw.WhatChanged;
import com.ugcs.gprvisualizer.gpr.Model;
import com.ugcs.gprvisualizer.gpr.Settings.RadarMapMode;
import com.ugcs.gprvisualizer.math.HyperFinder;
import com.ugcs.gprvisualizer.math.LevelFilter;
import com.ugcs.gprvisualizer.math.TraceDecimator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class MainGeoHammer extends Application implements SmthChangeListener {

	private static final String TITLE_VERSION = "UgCS GeoHammer v.0.9.7";
	private static final int RIGHT_BOX_WIDTH = 330;
	private Scene scene;
	private Stage stage;
	private BorderPane bPane;
	private ModeFactory modeFactory;
	private VBox rightBox = new VBox();
	//private VBox centerBox = new VBox();
	private Model model = new Model();
	private ToolBar toolBar = new ToolBar();

	
	SplitPane sp;
	
	MapView layersWindowBuilder;
	ProfileView cleverImageView;
	
	public MainGeoHammer() {
		
		AppContext.model = model;
		AppContext.levelFilter = new LevelFilter(model);
		AppContext.loader = new Loader(model);
		
		AppContext.pluginRunner = new PluginRunner(model);		
		AppContext.navigator = new Navigator(model);
		
		AppContext.statusBar = new StatusBar(model);
		
		layersWindowBuilder = new MapView(model);
		AppContext.smthListener.add(layersWindowBuilder);

		cleverImageView = new ProfileView(model);
		
		AppContext.smthListener.add(this);
	}
	
	public static void main(String args[]) {
		launch(args);
		
		
	}

	@Override
	public void start(Stage stage) throws Exception {

		this.stage = stage;
		
		AppContext.saver = new Saver(model, stage);

		stage.getIcons().add(ResourceImageHolder.IMG_LOGO24);
		
		scene = createScene();

		stage.setTitle(TITLE_VERSION);
		stage.setScene(scene);
		stage.show();

		model.getSettings().center_box_width = (int) (bPane.getWidth() - rightBox.getWidth()); 
		model.getSettings().center_box_height = (int) (bPane.getHeight() - toolBar.getHeight());
		
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});		
		
		
		if(getParameters().getRaw().size() > 0) {
			String name = getParameters().getRaw().get(0);
			System.out.println("args " + name);
			
			List<File> f = new ArrayList<>();
			f.add(new File(name));
			AppContext.loader.load(f, new ProgressListener() {
				
				@Override
				public void progressPercent(int percent) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void progressMsg(String msg) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}

	private Scene createScene() {

		bPane = new BorderPane();

		bPane.setOnDragOver(AppContext.loader.getDragHandler());
		bPane.setOnDragDropped(AppContext.loader.getDropHandler());

		bPane.setTop(getToolBar());

		ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
			model.getSettings().center_box_width = (int) (bPane.getWidth() - RIGHT_BOX_WIDTH); 
			model.getSettings().center_box_height = (int) (bPane.getHeight() - toolBar.getHeight());
		};
		bPane.widthProperty().addListener(stageSizeListener);
		bPane.heightProperty().addListener(stageSizeListener);

		sp = new SplitPane();
		sp.setDividerPositions(0.2f, 0.6f, 0.2f);
		
		
		sp.getItems().add(layersWindowBuilder.getCenter());
		
		sp.getItems().add(cleverImageView.getCenter());
		
		sp.getItems().add(getRightPane());
		
		bPane.setCenter(sp);
		
		rightBox.getChildren().clear();
		
        TabPane tabPane = new TabPane();

        Tab tab1 = new Tab("Gain", new Label("Show all planes available"));
        Tab tab2 = new Tab("Search"  , new Label("Show all cars available"));
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        
        VBox t1 = new VBox();
        t1.getChildren().addAll(layersWindowBuilder.getRight());
        //t1.getChildren().addAll();
        tab1.setContent(t1);

        prepareTab2(tab2);
        
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() { 
            @Override 
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if(newTab.equals (tab1)) {            
                    System.out.print("gain mode");
                    model.getSettings().radarMapMode = RadarMapMode.AMPLITUDE;
                    model.getSettings().hyperliveview = false;
                    
                    //todo toggle btn
                    
                }else if(newTab.equals (tab2)) {
                	System.out.print("search mode");
                	model.getSettings().radarMapMode = RadarMapMode.SEARCH;
                }
                AppContext.notifyAll(new WhatChanged(Change.adjusting));
            }

        });        
        
        rightBox.getChildren().addAll(tabPane);
        rightBox.getChildren().addAll( cleverImageView.getRight());
		//rightBox.getChildren().addAll();
		//rightBox.getChildren().addAll(cleverImageView.getRight());
		
		/////////
		
		////
		bPane.setBottom(AppContext.statusBar);
		
		
		
		scene = new Scene(bPane, 1280, 768);
		
		
		
		return scene;
	}

	private ToggleButton hyperLiveViewBtn = new ToggleButton("Hyperbola detection mode", ResourceImageHolder.getImageView("hypLive.png"));
	private EventHandler<ActionEvent> showMapListener = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			model.getSettings().hyperliveview = hyperLiveViewBtn.isSelected();
			AppContext.notifyAll(new WhatChanged(Change.justdraw));
			//repaintEvent();
		}
	};
	
	public void prepareTab2(Tab tab2) {
		VBox t2 = new VBox(10);		
        t2.getChildren().addAll(cleverImageView.getRightSearch());
        
		///		
		Button buttonHyperFinder = new Button("Start algorithmic search");
		buttonHyperFinder.setOnAction(e -> {
			HyperFinder hf = new HyperFinder(model);
			hf.process();
		});

		
		
		hyperLiveViewBtn.setTooltip(new Tooltip("Hyperbola view mode"));
		hyperLiveViewBtn.setOnAction(showMapListener);

		Button buttonDecimator = new Button("Trace decimator");
		buttonDecimator.setOnAction(e -> {
			
			new TraceDecimator().process(model);
		});
		
		
		
		t2.getChildren().addAll(buttonHyperFinder, hyperLiveViewBtn, buttonDecimator);
        tab2.setContent(t2);
        
        
	
	
	
    	


        
        
	}


	private Node getToolBar() {
		toolBar.setDisable(true);
		
		toolBar.getItems().addAll(AppContext.saver.getToolNodes());
		
		toolBar.getItems().add(getSpacer());
		
		toolBar.getItems().addAll(AppContext.levelFilter.getToolNodes());
		
		toolBar.getItems().add(getSpacer());

		toolBar.getItems().addAll(AppContext.pluginRunner.getToolNodes());
		
		//toolBar.getItems().add(getSpacer());
		//toolBar.getItems().addAll(AppContext.navigator.getToolNodes());
		///
		
		return toolBar;
	}

	private Region getSpacer() {
		Region r3 = new Region();
		r3.setPrefWidth(10);
		return r3;
	}

	public ModeFactory getModeFactory() {
		return modeFactory;
	}

	public void setModeFactory(ModeFactory modeFactory) {
		this.modeFactory = modeFactory;

		
		//centerBox.getChildren().clear();
		//centerBox.getChildren().add(getModeFactory().getCenter());


		showCenter();
	}

	private void showCenter() {
		if(getModeFactory() != null) {
			getModeFactory().show();
		}
	}

	private Node getRightPane() {
		rightBox = new VBox();
		rightBox.setPadding(new Insets(3, 13, 3, 3));
		rightBox.setPrefWidth(RIGHT_BOX_WIDTH);
		rightBox.setMinWidth(RIGHT_BOX_WIDTH);
		rightBox.setMaxWidth(RIGHT_BOX_WIDTH);
		
		return rightBox;
	}

	@Override
	public void somethingChanged(WhatChanged changed) {


		if(changed.isFileopened()) {
			toolBar.setDisable(!model.isActive());
			
			AppContext.levelFilter.clearForNewFile();
			//gpsMode.setSelected(true);
			//setModeFactory(modeMap.get(gpsMode));			
		}
		
	}

}