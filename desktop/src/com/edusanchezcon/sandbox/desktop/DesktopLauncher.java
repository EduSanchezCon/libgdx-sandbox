package com.edusanchezcon.sandbox.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.edusanchezcon.sandbox.MyApp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode((int) MyApp.WIDTH, (int) MyApp.HEIGHT);
		new Lwjgl3Application(new MyApp(), config);
	}
}
