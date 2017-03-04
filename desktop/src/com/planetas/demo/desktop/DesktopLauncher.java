package com.planetas.demo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.planetas.demo.Demo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.height = Demo.HEIGHT;
		config.width = Demo.WIDTH;
		config.fullscreen = Demo.FULL_SCREEN;
		config.title = Demo.TITLE;
		
		new LwjglApplication(new Demo(), config);
	}
}
