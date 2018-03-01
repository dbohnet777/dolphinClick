package a1;
//game.setState.STOPPING (this should be called instead of game.shutdown

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import myGameEngine.DownPitchAction;
import myGameEngine.LeftYawAction;
import myGameEngine.MountDismountDolphin;
import myGameEngine.MoveBackwardAction;
import myGameEngine.MoveForwardAction;
import myGameEngine.MoveLeftAction;
import myGameEngine.MoveRightAction;
import myGameEngine.QuitGameAction;
import myGameEngine.RightYawAction;
import myGameEngine.UpPitchAction;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rage.util.BufferUtil;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.states.*;
import ray.rage.rendersystem.shader.*;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.*;
import ray.input.*;
import ray.input.action.*;

public class MyGame extends VariableFrameRateGame {
	
	// to minimize variable allocation in update()
	GL4RenderSystem rs;
	float elapsTime = 0.0f;
	String elapsTimeStr, counterStr, dispStr;
	int elapsTimeSec, counter = 0;
	public boolean onDolphin = false;	//boolean flag to keep track which node camera is attached to
	
	//variable declaration for the input manager
	private InputManager im;
	
	//declare scene nodes
	private SceneNode activeNode;
	
	private int diamondCollisionCounter = 0;
	
	//create an arraylist for iterating through diamond nodes for collision checking
	private ArrayList<SceneNode> diamonds = new ArrayList<SceneNode>();
	int[] ints = new int[20];
	
	
//--------------------------------------------------------------------------------------//	
	
	public MyGame() {
		super();
		System.out.println("This is where contol info can be put to display on the console");
	}

//--------------------------------------------------------------------------------------//	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game game = new MyGame();
		try
		{ 	
			game.startup();
			game.run();
		}
		catch (Exception e)
		{ 
			e.printStackTrace(System.err);
		}
		finally
		{ 	
			//game.setState(STOPPING);
			game.shutdown();
			game.exit();
		}
	}
	
//--------------------------------------------------------------------------------------//	

	@Override
	protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge){ 
		rs.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
	}
		
	
//--------------------------------------------------------------------------------------//	
	
	@Override
	protected void setupCameras(SceneManager sm, RenderWindow rw) {
		//create a SceneNode which is the root
		SceneNode rootNode = sm.getRootSceneNode();
		//create a camera object "MainCamera"
		Camera camera = sm.createCamera("MainCamera", Projection.PERSPECTIVE);
		//set camera's viewport
		rw.getViewport(0).setCamera(camera);
		camera.setRt((Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f));
		camera.setUp((Vector3f)Vector3f.createFrom(0.0f, 1.0f, 0.0f));
		camera.setFd((Vector3f)Vector3f.createFrom(0.0f, 0.0f, -1.0f));
		camera.setPo((Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f));
		//create node that the camera will be attached to
		SceneNode cameraNode = rootNode.createChildSceneNode(camera.getName()+"Node");
		//attach camera to the cameraNode
		cameraNode.attachObject(camera);
		//set camera's view mode to perspective of cameraNode
		camera.setMode('r');
		cameraNode.moveLeft(0.5f);
		
	}
	

//--------------------------------------------------------------------------------------//		

	@Override
	protected void setupScene(Engine eng, SceneManager sm) throws IOException {
		//run function to setup inputs
		setupInputs();
		
		//create the dolphin entity
		createDolphin(eng, sm);
		
		//set the active node to the camera node
		activeNode = this.getEngine().getSceneManager().getSceneNode("MainCameraNode");
		
		//set the scene's ambient lighting
		sm.getAmbientLight().setIntensity(new Color(.1f, .1f, .1f));
		//create light source
		Light plight = sm.createLight("testLamp1", Light.Type.POINT);
		plight.setAmbient(new Color(.3f, .3f, .3f));
		plight.setDiffuse(new Color(.2f, .2f, .2f));
		plight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
		plight.setRange(5f);
		//create node to attach light object to
		SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode("plightNode");
		//attach light object to plightNode
		plightNode.attachObject(plight);
		
		
		//rotation controller to make the diamond(1) and earth(2) objects spin in place
		RotationController rc1 = new RotationController(Vector3f.createUnitVectorY(), 0.5f);
		RotationController rc2 = new RotationController(Vector3f.createUnitVectorY(), 0.005f);
		sm.addController(rc1);
		sm.addController(rc2);

		//Create ground plane
		ManualObject groundPlane = makeGroundPlane(eng, sm);
		SceneNode groundPlaneN = sm.getRootSceneNode().createChildSceneNode("groundPlaneNode");
		groundPlaneN.scale(500.0f, 1.0f, 500.0f);
		groundPlaneN.moveBackward(10.0f);
		groundPlaneN.moveLeft(250.0f);
		groundPlaneN.moveDown(0.5f);
		groundPlaneN.attachObject(groundPlane);
		//create a manual diamond objects and attach them to diamond nodes
		for(int i = 0; i < 20; i++) {
			//randomly generated scale multiplier for diamond objects
			float randScaleD = randFloat(0.2f, 0.5f);
			//randomly generated x y and z coordinates for diamond objects
			float randPosXD = randFloat(0.0f, 200.0f);
			//float randPosYD = randFloat(-20.0f, 20.0f);
			float randPosZD = randFloat(0.0f, 200.0f);
			
			//create this diamond object I
			ManualObject diamond = makeDiamond(eng, sm, i);
			//create a diamond node and label it diamond#Node and attach it to the root scene node
			SceneNode diamondN = sm.getRootSceneNode().createChildSceneNode("diamond" + i + "Node");
			//scale and position this node randomly
			diamondN.scale(randScaleD, randScaleD, randScaleD);
			diamondN.moveForward(randPosXD);
			diamondN.moveLeft(randPosZD);
			diamondN.moveUp(0.5f);
			//attach the diamond object to this node
			diamondN.attachObject(diamond);
			diamonds.add(diamondN);
			//add rotation controller to this node
			rc1.addNode(diamondN);
			
			//randomly generated scale multiplier for earth objects
			float randScaleE = randFloat(0.5f, 1.0f);
			//randomly generated x y and z coordinates for earth objects
			float randPosXE = randFloat(0.0f, 200.0f);
			//float randPosYE = randFloat(-20.0f, 20.0f);
			float randPosZE = randFloat(0.0f, 200.0f);
			
			//create earth object I
			// set up earth
			Entity earthE = makeEarth(eng, sm, i);
			SceneNode earthN = sm.getRootSceneNode().createChildSceneNode(earthE.getName() + "Node");
			earthN.attachObject(earthE);
			earthN.scale(randScaleE, randScaleE, randScaleE);
			earthN.moveForward(randPosXE);
			earthN.moveLeft(randPosZE);
			earthN.moveUp(1.0f);
			rc2.addNode(earthN);
		}		
		//set up box object
		SceneNode boxNode = sm.getRootSceneNode().createChildSceneNode("boxNode");
		boxNode.attachObject(makeBox(eng, sm));
		rc2.addNode(boxNode);
	}

//--------------------------------------------------------------------------------------//	

	@Override
	protected void update(Engine engine) {
		// build and set HUD
		rs = (GL4RenderSystem) engine.getRenderSystem();
		elapsTime += engine.getElapsedTimeMillis();
		elapsTimeSec = Math.round(elapsTime/1000.0f);
		elapsTimeStr = Integer.toString(elapsTimeSec);
		counterStr = Integer.toString(counter);
		dispStr = "Time = " + elapsTimeStr + " Diamonds Collected = " + diamondCollisionCounter;
		rs.setHUD(dispStr, 15, 15);
		
		Vector3f distance = (Vector3f)Vector3f.createFrom(1.0f, 0.0f, 0.0f);
		//update the input manager
		im.update(elapsTime);
		
		//if player is off dolphin check how far away they are to snap them back if they get too far away
		if(getActiveNode().getName().equals("MainCameraNode")) {
			//distance vector gets distance from player to dolphin
			
			//I did the distance method in the ugliest way humanly possible 
			distance = (Vector3f) checkObjCollision(engine.getSceneManager().getSceneNode("MainCameraNode"), engine.getSceneManager().getSceneNode("dolphinNode"));
			//check a range to see if player has gone out of bounds
			if(distance.x() > 5.0f || distance.x() < -5.0f || distance.y() > 5.0f && distance.y() < -5.0f || distance.z() > 5.0f || distance.z() < -5.0f) {
				//place player next to dolphin
				engine.getSceneManager().getSceneNode("MainCameraNode").setLocalPosition(engine.getSceneManager().getSceneNode("dolphinNode").getLocalPosition().add(Vector3f.createFrom(0.3f, 0.3f, 0.0f)));
			}
		}
		
		//check if player has collided with a diamond
		if(getActiveNode().getName().equals("MainCameraNode")) {
			for(int i = 0; i < diamonds.size(); i++) {
				
				if(ints[i] != -1) {
					distance = (Vector3f) checkObjCollision(engine.getSceneManager().getSceneNode("MainCameraNode"), engine.getSceneManager().getSceneNode("diamond" + i + "Node"));
				}
				if(distance.x() > -0.2f && distance.x() < 0.2f && distance.y() > -0.2f && distance.y() < 0.2f && distance.z() > -0.2f && distance.z() < 0.2 && ints[i] != -1) {
					
					engine.getSceneManager().destroySceneNode("diamond" + i + "Node");
					ints[i] = -1;
					System.out.print("collided with a node");
					diamondCollisionCounter++;
				}
			}
		}
		
		//check if player has collided with the box
		if(getActiveNode().getName().equals("MainCameraNode")) {
			distance = (Vector3f) checkObjCollision(engine.getSceneManager().getSceneNode("MainCameraNode"), engine.getSceneManager().getSceneNode("boxNode"));

			if(distance.x() > -0.3f && distance.x() < 0.3f && distance.y() > -0.3f && distance.y() < 0.3f && distance.z() > -0.3f && distance.z() < 0.3) {
				diamondCollisionCounter = 0;
			}
		}
	}
	
//--------------------------------------------------------------------------------------//	
	public void setupInputs() {		//update controller inputs
		//create an input manager im
		im = new GenericInputManager();
		
		String kbName = im.getKeyboardName();
		String gpName = im.getFirstGamepadName();
		// build some action objects for doing things in response to user input
		QuitGameAction quitGameAction = new QuitGameAction(this);
		MountDismountDolphin mountDismountAction = new MountDismountDolphin(this, onDolphin);
		MoveForwardAction moveForwardAction = new MoveForwardAction(this);
		MoveBackwardAction moveBackwardAction = new MoveBackwardAction(this);
		MoveLeftAction moveLeftAction = new MoveLeftAction(this);
		MoveRightAction moveRightAction = new MoveRightAction(this);
		UpPitchAction upPitchAction = new UpPitchAction(this);
		DownPitchAction downPitchAction = new DownPitchAction(this);
		LeftYawAction leftYawAction = new LeftYawAction(this);
		RightYawAction rightYawAction = new RightYawAction(this);
		//incrementCounterAction = new IncrementCounterAction(this);
		
		// attach the action objects to keyboard components
		//pitch up and down currently disabled for a2
		//quit action
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.ESCAPE, quitGameAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		//move forward
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//move backward
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//move left
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//move right
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//pitch up
		//im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.UP, upPitchAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//pitch down
		//im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.DOWN, downPitchAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//yaw left
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.LEFT, leftYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//yaw right
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.RIGHT, rightYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);		
		//mount/dismount dolphin
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.SPACE, mountDismountAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		// attach the action objects to gamepad components
		if(gpName != null) {
			//x button(_2) moves player on and off dolphin
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._2, mountDismountAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			//move forward (x axis)
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.Y, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.Y, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.X, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.X, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			//im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.Z, upPitchAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			//im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.Z, downPitchAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.Z, leftYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button.Z, rightYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._10, quitGameAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		}
	}
	
	public void createDolphin(Engine e, SceneManager sm) throws IOException{
		//create the dolphin entity and place it into the scene		
		Entity dolphinE = sm.createEntity("dolphin", "dolphinHighPoly.obj");
		dolphinE.setPrimitive(Primitive.TRIANGLES);
		SceneNode dolphinNode = sm.getRootSceneNode().createChildSceneNode(dolphinE.getName() + "Node");
		dolphinNode.attachObject(dolphinE);
		
		//create node for riding the dolphin directly above the dolphin node
		SceneNode onDolphinNode = sm.getSceneNode("dolphinNode").createChildSceneNode("onDolphinNode");
		onDolphinNode.setLocalPosition(0.0f, 1.0f, 0.0f);
	}
	
	protected ManualObject makeDiamond(Engine e, SceneManager sm, int objNum) throws IOException{
		//instantiate diamond as Manual Object
		ManualObject diamond = sm.createManualObject("Diamond" + objNum);
		//instantiate section of diamond object
		ManualObjectSection diamondSec = diamond.createManualSection("DiamondSection");
		diamond.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		
		Material mat = sm.getMaterialManager().getAssetByPath("sphere.mtl");
		
		float[] vertices = new float[]{ 
			0.0f, 1.0f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, //top1
			0.0f, 1.0f, -0.5f, -0.5f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, //top2
			0.0f, 1.0f, -0.5f, 0.0f, 0.0f, -1.0f, -0.5f, 0.0f, 0.0f, //top3
			0.5f, 0.0f, 0.0f, 0.0f, -1.0f, -0.5f,  0.0f, 0.0f, -1.0f, //bottom1
			-0.5f, 0.0f, 0.0f, 0.0f, -1.0f, -0.5f, 0.5f, 0.0f, 0.0f, //bottom2
			0.0f, 0.0f, -1.0f, 0.0f, -1.0f, -0.5f, -0.5f, 0.0f, 0.0f, //bottom3
		};
		/*
		float[] texcoords = new float[]{ 
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
		};
		
		float[] normals = new float[]{ 
			0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
			0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, -1.0f,
			-1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f
		};
		*/
		int[] indices = new int[] { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17 };
		
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		//FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		//FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		diamondSec.setVertexBuffer(vertBuf);
		//diamondSec.setTextureCoordsBuffer(texBuf);
		//diamondSec.setNormalsBuffer(normBuf);
		diamondSec.setIndexBuffer(indexBuf);
		Texture tex = e.getTextureManager().getAssetByPath("chain-fence.jpeg");
		TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		diamond.setDataSource(DataSource.INDEX_BUFFER);
		diamond.setRenderState(texState);
		diamond.setRenderState(faceState);
		diamond.setMaterial(mat);
		return diamond;
	}
	
	protected ManualObject makeBox(Engine e, SceneManager sm) throws IOException{
		//instantiate diamond as Manual Object
		ManualObject box = sm.createManualObject("Box");
		//instantiate section of diamond object
		ManualObjectSection boxSec = box.createManualSection("BoxSection");
		box.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		
		Material mat = sm.getMaterialManager().getAssetByPath("sphere.mtl");
		
		float[] vertices = new float[]{ 
			0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //front1.1
			1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //front1.2
			1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, //right1.1
			1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, //right1.2
			0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, //left1.1
			0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, //left1.2
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, //back1.1
			0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, //back1.2
			0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, //bottom1.1
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, //bottom1.2
			1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, //insidef1.1
			1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //insidef1.2
			1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //insider1.1
			1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, //insider1.2
			0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, //insidel1.1
			0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, //insidel1.2
			0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //insideback1.1
			1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, //insideback1.2
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, //insidebottom1.1
			1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f //insidebottom1.2
		};
	
		int[] indices = new int[60];
		for(int i = 0; i < 60; i ++) {
			indices[i] = i;
		}
		
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		//FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		//FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		boxSec.setVertexBuffer(vertBuf);
		//diamondSec.setTextureCoordsBuffer(texBuf);
		//diamondSec.setNormalsBuffer(normBuf);
		boxSec.setIndexBuffer(indexBuf);
		Texture tex = e.getTextureManager().getAssetByPath("chain-fence.jpeg");
		TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		box.setDataSource(DataSource.INDEX_BUFFER);
		box.setRenderState(texState);
		box.setRenderState(faceState);
		box.setMaterial(mat);
		return box;
	}
	
	protected ManualObject makeStar(Engine e, SceneManager sm) throws IOException{
		//instantiate diamond as Manual Object
		ManualObject star = sm.createManualObject("Star");
		//instantiate section of diamond object
		ManualObjectSection starSec = star.createManualSection("StarSection");
		star.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		
		Material mat = sm.getMaterialManager().getAssetByPath("sphere.mtl");
		
		float[] vertices = new float[]{ 
			0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //front1.1
			1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, //front1.2
			1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, //right1.1
			
		};
	
		int[] indices = new int[60];
		for(int i = 0; i < 60; i ++) {
			indices[i] = i;
		}
		
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		//FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		//FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		starSec.setVertexBuffer(vertBuf);
		//diamondSec.setTextureCoordsBuffer(texBuf);
		//diamondSec.setNormalsBuffer(normBuf);
		starSec.setIndexBuffer(indexBuf);
		Texture tex = e.getTextureManager().getAssetByPath("chain-fence.jpeg");
		TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		star.setDataSource(DataSource.INDEX_BUFFER);
		star.setRenderState(texState);
		star.setRenderState(faceState);
		star.setMaterial(mat);
		return star;
	}
	
	protected ManualObject makeGroundPlane(Engine e, SceneManager sm) throws IOException{
		//instantiate groundPlane as Manual Object
		ManualObject groundPlane = sm.createManualObject("GroundPlane");
		//instantiate section of diamond object
		ManualObjectSection groundPlaneSec = groundPlane.createManualSection("GroundPlaneSection");
		groundPlane.setGpuShaderProgram(sm.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));
		
		Material mat = sm.getMaterialManager().getAssetByPath("sphere.mtl");
		
		float[] vertices = new float[]{ 
			0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, //front1.1
			2.0f, 0.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, //front1.2

		};
	
		int[] indices = new int[] {0, 1, 2, 3, 4, 5};

		
		FloatBuffer vertBuf = BufferUtil.directFloatBuffer(vertices);
		//FloatBuffer texBuf = BufferUtil.directFloatBuffer(texcoords);
		//FloatBuffer normBuf = BufferUtil.directFloatBuffer(normals);
		IntBuffer indexBuf = BufferUtil.directIntBuffer(indices);
		groundPlaneSec.setVertexBuffer(vertBuf);
		//diamondSec.setTextureCoordsBuffer(texBuf);
		//diamondSec.setNormalsBuffer(normBuf);
		groundPlaneSec.setIndexBuffer(indexBuf);
		Texture tex = e.getTextureManager().getAssetByPath("chain-fence.jpeg");
		TextureState texState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
		texState.setTexture(tex);
		FrontFaceState faceState = (FrontFaceState) sm.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);
		groundPlane.setDataSource(DataSource.INDEX_BUFFER);
		groundPlane.setRenderState(texState);
		groundPlane.setRenderState(faceState);
		groundPlane.setMaterial(mat);
		return groundPlane;
	}
	
	protected Entity makeEarth(Engine e, SceneManager sm, int objNum) throws IOException{
		//create earth entity
		Entity earthE = sm.createEntity("earth" + objNum, "earth.obj");
		earthE.setPrimitive(Primitive.POINTS);
		return earthE;
	}
	
	public Vector3 checkObjCollision(SceneNode n1, SceneNode n2) {
		//get position of vector to position of node 1 and 2
		Vector3f n1V = (Vector3f) n1.getLocalPosition();
		Vector3f n2V = (Vector3f) n2.getLocalPosition();
		//subtract the two vectors to get the vector result as distance between them
		Vector3f result = (Vector3f) n1V.sub(n2V);
		//return resultant vector
		return result;
	}
	/*public void checkPickup(Engine e, SceneManager sm) throws IOException{
		
	}
	public void checkSnapToDolphin(Engine e) throws IOException{
		if(getActiveNode().getName().equals("MainCameraNode")) {
			if(checkObjCollision(e.getSceneManager().getSceneNode("MainCameraNode"), e.getSceneManager().getSceneNode("dolphinNode"))){
				e.getSceneManager().getSceneNode("MainCameraNode").setLocalPosition(e.getSceneManager().getSceneNode("dolphinNode").getLocalPosition().add(Vector3f.createFrom(-0.3f, 0.2f, 0.0f)));
			}
		}
	}*/
	
	//functions to manipulate currently active node
	public SceneNode getActiveNode() {
		return activeNode;
	}
	public void setActiveNode(SceneNode node) {
		activeNode = node;
	}
	//function to generate random floats within a range for random distributions
	public float randFloat(float min, float max) {
		Random rng = new Random();
		float randomVal = min + rng.nextFloat() * (max - min);
		return randomVal;
	}
}
