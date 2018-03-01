package myGameEngine;

import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rml.Degreef;

public class UpPitchAction extends AbstractInputAction{

	private MyGame game;
	
	public UpPitchAction(MyGame g) {
		game = g;
	}
	
	public void performAction(float time, Event e) {
		//get name of node to be moved and pitch upward by small amount
		game.getEngine().getSceneManager().getSceneNode(game.getActiveNode().getName()).pitch(Degreef.createFrom(-1f));;
	}
}