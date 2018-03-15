package BackEvolution.Trader;
/**
 * main.java 1.0 March 6, 2018
 *
 * Copyright (c) 2018 Blair Helms
 * Mebane, North Carolina 27302 U.S.A
 * All Rights Reserved
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import BackEvolution.Main;
import BackEvolution.NeuralNetManager;
import BackEvolution.Singleton;
import BackEvolution.SpecialMain;

import java.io.*;

/**/
@SuppressWarnings("unused")

public class TraderMain implements SpecialMain {
	public TraderMain(){};
	public TraderSingleton SetupStartup() {
		try {
			MarketManager.start();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return TraderSingleton.getInstance();
	}
	public void AfterStartup() {
		try {
			ProgressTracker.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

