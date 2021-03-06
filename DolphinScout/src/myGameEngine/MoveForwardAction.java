package myGameEngine;

import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.Node;

public class MoveForwardAction extends AbstractInputAction{

	private MyGame game;
	private Node node;
	
	public MoveForwardAction(MyGame g, Node n) {
		game = g;
		node = n;
	}
	
	public void performAction(float time, Event e) {
		node.moveForward(0.1f);
	}
}
