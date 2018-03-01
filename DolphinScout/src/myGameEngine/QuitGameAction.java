package myGameEngine;

import ray.input.action.AbstractInputAction;
import net.java.games.input.Event;
import a1.MyGame;

public class QuitGameAction extends AbstractInputAction{
	
	private MyGame game;
	
	public QuitGameAction(MyGame g){ 
		game = g;
	}
	
	public void performAction(float time, Event event){ 
		System.out.println("shutdown requested");
		game.shutdown();
	}
}