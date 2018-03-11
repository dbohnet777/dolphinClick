package myGameEngine;

import a1.MyGame;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.Engine;
import ray.rage.scene.Camera;
import ray.rage.scene.Node;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3pController{
	private boolean orbitMode;
	private Camera camera;			//the camera that will use this contoller
	private SceneNode cameraNode;	//the node the camera is attached to
	private SceneNode targetNode;	//the target the camera looks at
	private float cameraAzimuth;	//rotation of camera around Y axis
	private float cameraElevation;	//elevation of camera above target
	private float radius;			//distance between camera and the target
	private Vector3 targetPos;		//target's position in the world
	private Vector3 worldUpVector;
	
	public Camera3pController(boolean mode, Camera cam, SceneNode camN,  SceneNode targ, String controllerName, InputManager im) {
		orbitMode = mode;
		camera = cam;
		cameraNode = camN;
		targetNode = targ;
		cameraAzimuth = 225.0f;		//to start Behind and Above target
		cameraElevation = 20.0f;	//measured in degrees
		radius = 2.0f;
		worldUpVector = Vector3f.createFrom(0.0f, 1.0f, 0.0f);
		setupInput(im, controllerName);
		updateCameraPosition();
	}
	
	public void updateCameraPosition() {
		double theta = Math.toRadians(cameraAzimuth);		//rotation around target
		double phi = Math.toRadians(cameraElevation);		//altitude angle
		double x = radius * Math.cos(phi) * Math.sin(theta);
		double y = radius * Math.sin(phi);
		double z = radius * Math.cos(phi) * Math.cos(theta);
		cameraNode.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(targetNode.getWorldPosition()));
		cameraNode.lookAt(targetNode, worldUpVector);
	}
	
	private void setupInput(InputManager im, String cn) {
		Action orbitAAction = new OrbitAroundAction();
		Action orbitRAction = new OrbitRadiusAction();
		Action orbitEAction = new OrbitElevationAction();
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.I, orbitAAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.J, orbitRAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(cn,  net.java.games.input.Component.Identifier.Key.L, orbitEAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

	}
	
	private class OrbitAroundAction extends AbstractInputAction{
		//Moves the camera around the target (changes camera azimuth)
		public void performAction(float time, net.java.games.input.Event evt) {
			float rotAmount;
			if(evt.getValue() < -0.2) {rotAmount = -0.2f;}
			else {
				if(evt.getValue() > 0.2) {
					rotAmount = 0.2f;
				}
				else {
					rotAmount = 0.0f;
				}
			}
			if(orbitMode == true) {	//camera turns independently of the dolphin heading
				cameraAzimuth += rotAmount;
				cameraAzimuth = cameraAzimuth % 360;
			}
			//not working properly
			if(orbitMode == false) {	//camera turns dependently of the dolphin heading
				cameraAzimuth = targetNode.getLocalRotation().toQuaternion().angle().valueDegrees();
			}
			updateCameraPosition();
		}
	}
	
	private class OrbitRadiusAction extends AbstractInputAction{
		//Adjusts the camera radius from the target
		public void performAction(float time, net.java.games.input.Event evt) {
			float deltaR;		//temporary radius variable
			if(evt.getValue() < -.02) {
				deltaR = -.02f;
			}
			else {
				if(evt.getValue() > .02) {
					deltaR = 0.2f;
				}
				else {
					deltaR = 0.0f;
				}
			}
			radius += deltaR;
			updateCameraPosition();
		}
	}
	
	private class OrbitElevationAction extends AbstractInputAction{
		//Adjusts the camera elevation from the target
		public void performAction(float time, net.java.games.input.Event evt) {
			float deltaE;
			if(evt.getValue() < -.02) {
				deltaE = -0.2f;
			}
			else {
				if(evt.getValue() > 0.2) {
					deltaE = 0.2f;
				}
				else {
					deltaE = 0.0f;
				}
			}
			cameraElevation += deltaE;
			if(cameraElevation <= 0.0f) {
				cameraElevation = 0.0f;
			}
			if(cameraElevation >= 89.9f) {
				cameraElevation = 89.9f;
				
			}
			updateCameraPosition();
		}
	}
	
}

