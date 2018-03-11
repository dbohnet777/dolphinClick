package myGameEngine;

import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class OrbitModeAction extends AbstractInputAction{

	private MyGame game;
	
	public OrbitModeAction(MyGame g) {
		game = g;
	}
	
	public void performAction(float time, Event e) {
		//Switch the current orbit mode
		game.switchOrbitMode();
		System.out.println(game.getOrbitMode());
	}
}