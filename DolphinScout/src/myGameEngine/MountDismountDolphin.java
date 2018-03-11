package myGameEngine;

import ray.rage.scene.*;
import ray.rml.*;
import a1.MyGame;
import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;

public class MountDismountDolphin extends AbstractInputAction{
	
	//instantiate variables needed to mount/dismount the dolphin
	private boolean onDolphin;
	private MyGame game;
	private SceneManager sm;
	private Vector3 dismount;
	
	public MountDismountDolphin(MyGame g, boolean riding){
		//assign variables
		game = g;
		onDolphin = riding;
		//get SceneManager
		sm = game.getEngine().getSceneManager();
		//create a vector for dismounting near to the side of the dolphin
		dismount = Vector3f.createFrom(0.4f, 0.2f, 0.0f);
	}
	
	public void performAction(float time, Event e) {
		if(onDolphin) {
			//detach child nodes from the dolphin entity node
			sm.getSceneNode("onDolphin1Node").detachAllChildren();
			//set the offDolphinNode's position and rotation to be the same as the dolphin
			sm.getSceneNode("playerCamera1Node").setLocalRotation(sm.getSceneNode("dolphin1Node").getLocalRotation());
			sm.getSceneNode("playerCamera1Node").setLocalPosition(sm.getSceneNode("dolphin1Node").getLocalPosition().add(dismount));
			//make the current active node the off dolphin node
			game.setActiveNode(sm.getSceneNode("playerCamera1Node"));			
		}
		else {
			sm.getSceneNode("onDolphin1Node").attachChild(game.getEngine().getSceneManager().getSceneNode("playerCamera1Node"));
			//
			sm.getSceneNode("playerCamera1Node").setLocalPosition(0.0f, 0.0f, 0.0f);
			sm.getSceneNode("playerCamera1Node").setLocalRotation(sm.getRootSceneNode().getLocalRotation());
			//
			game.setActiveNode(sm.getSceneNode("dolphin1Node"));

		}
		onDolphin = !onDolphin;
	}
	
}
