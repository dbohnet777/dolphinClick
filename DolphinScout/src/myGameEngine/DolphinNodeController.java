package myGameEngine;


import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class DolphinNodeController {
	private SceneNode dolphinNode;
	private float step;
	
	public DolphinNodeController(SceneNode dN, String cn, InputManager im) {
		dolphinNode = dN;
		step = 0.2f;
		
		setupInput(im, cn);
		updateNodePosition();
	}
	
	public void updateNodePosition() {
			
	}
	
	private void setupInput(InputManager im, String cn) {
		Action moveForward = new MoveForwardAction();
		Action moveBackward = new MoveBackwardAction();
		Action moveLeft = new MoveLeftAction();
		Action moveRight = new MoveRightAction();
		Action yawLeft = new YawLeftAction();
		Action yawRight = new YawRightAction();
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.W, moveForward, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.S, moveBackward, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.A, moveLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.D, moveRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.Q, yawLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.E, yawRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

	}
	
	private class MoveForwardAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			dolphinNode.moveForward(step);
		}

	}
	private class MoveBackwardAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			dolphinNode.moveBackward(step);
		}

	}
	private class MoveLeftAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			dolphinNode.moveLeft(step);
		}

	}
	private class MoveRightAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			dolphinNode.moveRight(step);
		}

	}
	private class YawLeftAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			dolphinNode.yaw(Degreef.createFrom(1.0f));
		}

	}
	private class YawRightAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			dolphinNode.yaw(Degreef.createFrom(-1.0f));
		}

	}
	

}
