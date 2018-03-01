package myGameEngine;

import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveLeftAction extends AbstractInputAction{

	private MyGame game;
	
	public MoveLeftAction(MyGame g) {
		game = g;
	}
	
	public void performAction(float time, Event e) {
		//get name of node to be moved and move left by small amount
		game.getEngine().getSceneManager().getSceneNode(game.getActiveNode().getName()).moveLeft(-0.05f);
	}
}
