package com.github.thecoldwine.sigrun.common.ext;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResourceImageHolder {

	public static Image IMG_SHOVEL;
	public static Image IMG_HOR_SLIDER;
	public static Image IMG_VER_SLIDER;
	
	public static Image IMG_LOCK;
	public static Image IMG_UNLOCK;
	
	public static Image IMG_CHOOSE;
	
	public static javafx.scene.image.Image FXIMG_DONE;

	
	static {
		try {
			IMG_SHOVEL = ImageIO.read(ResourceImageHolder.class.getClassLoader().getResourceAsStream("shovel-48.png"));
			IMG_HOR_SLIDER = ImageIO.read(ResourceImageHolder.class.getClassLoader().getResourceAsStream("horSlid16.png"));
			IMG_VER_SLIDER = ImageIO.read(ResourceImageHolder.class.getClassLoader().getResourceAsStream("vertSlid16.png"));
			IMG_LOCK = ImageIO.read(ResourceImageHolder.class.getClassLoader().getResourceAsStream("lock16.png"));
			IMG_UNLOCK = ImageIO.read(ResourceImageHolder.class.getClassLoader().getResourceAsStream("unlock16.png"));
			IMG_CHOOSE = ImageIO.read(ResourceImageHolder.class.getClassLoader().getResourceAsStream("choose16.png"));
			FXIMG_DONE =  new javafx.scene.image.Image(ResourceImageHolder.class.getClassLoader().getResourceAsStream("done-16.png"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
