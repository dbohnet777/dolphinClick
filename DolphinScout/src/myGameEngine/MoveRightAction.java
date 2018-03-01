package myGameEngine;

import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MoveRightAction extends AbstractInputAction{

	private MyGame game;
	
	public MoveRightAction(MyGame g) {
		game = g;
	}
	
	public void performAction(float time, Event e) {
		//get name of node to be moved and move right by small amount
		game.getEngine().getSceneManager().getSceneNode(game.getActiveNode().getName()).moveRight(-0.05f);
	}
}
