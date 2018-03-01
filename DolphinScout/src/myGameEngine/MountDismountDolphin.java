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
			sm.getSceneNode("onDolphinNode").detachAllChildren();
			//set the offDolphinNode's position and rotation to be the same as the dolphin
			sm.getSceneNode("MainCameraNode").setLocalRotation(sm.getSceneNode("dolphinNode").getLocalRotation());
			sm.getSceneNode("MainCameraNode").setLocalPosition(sm.getSceneNode("dolphinNode").getLocalPosition().add(dismount));
			//make the current active node the off dolphin node
			game.setActiveNode(sm.getSceneNode("MainCameraNode"));			
		}
		else {
			sm.getSceneNode("onDolphinNode").attachChild(game.getEngine().getSceneManager().getSceneNode("MainCameraNode"));
			//
			sm.getSceneNode("MainCameraNode").setLocalPosition(0.0f, 0.0f, 0.0f);
			sm.getSceneNode("MainCameraNode").setLocalRotation(sm.getRootSceneNode().getLocalRotation());
			//
			game.setActiveNode(sm.getSceneNode("dolphinNode"));

		}
		onDolphin = !onDolphin;
	}
	
}
