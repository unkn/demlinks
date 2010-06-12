/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.temporary.tests.threeD;



import java.net.URL;
import java.util.Random;
import java.util.logging.Logger;

import org.jme.addons.SimpleFixedLogicrateGame;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Tube;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.AudioTrack.TrackType;
import com.sceneworker.SceneWorker;
import com.sceneworker.app.ISceneWorkerApp;
import com.sceneworker.app.SceneWorkerAppHandler;



/**
 * Started Date: Jul 24, 2004 <br>
 * <br>
 * Demonstrates intersection testing, sound, and making your own controller.
 * 
 * @author Jack Lindamood
 */
public class HelloIntersection extends SimpleFixedLogicrateGame implements ISceneWorkerApp {
	
	private static final Logger		logger				= Logger.getLogger( HelloIntersection.class.getName() );
	
	/** Material for my bullet */
	MaterialState					bulletMaterial;
	
	/** Target you're trying to hit */
	Sphere							target;
	
	/** Location of laser sound */
	URL								laserURL;
	
	/** Location of hit sound */
	URL								hitURL;
	
	/** Used to move target location on a hit */
	Random							r					= new Random();
	
	/** A sky box for our scene. */
	Skybox							skybox;
	
	/**
	 * The sound tracks that will be in charge of maintaining our sound effects.
	 */
	AudioTrack						laserSound;
	AudioTrack						targetSound;
	AudioTrack						startupSound;
	
	private int						score, oldScore;
	private final int				scoreWhenBulletHits	= 10;
	private Text					scoreText;
	private static String			scoreTxt			= "Score: ";
	private static String			hitsTxt				= "Hits: ";
	private Text					hitsText;
	private int						hits, oldHits;
	private Text					liveBulletsText;
	private static String			liveBulletsTxt		= "Live bullets: ";
	private int						liveBullets, oldLiveBullets;
	// private boolean collision;
	// private CollisionResults results;
	// private Node scene, n1Target, n2Bullet;
	private SceneWorkerAppHandler	sceneWorkerHandler;
	
	// get the current input handler
	@Override
	public InputHandler getInputHandler() {

		return input;
	}
	
	// get the root node of the scene
	@Override
	public Node getRootNode() {

		return rootNode;
	}
	
	// get the tpf parameter for updates
	@Override
	public float getTimePerFrame() {

		return tpf;
	}
	
	/*
	 * set the input handler : SceneWorker has it's own input handler which can be swapped in and out by pressing '1' on
	 * the keyboard.
	 * This will toggle between the applications input handler or scene workers
	 */
	@Override
	public void setInputHandler( InputHandler cl_ip ) {

		input = cl_ip;
	}
	
	// get the display system
	@Override
	public DisplaySystem getDisplaySystem() {

		return display;
	}
	
	// get the current camera
	@Override
	public Camera getCamera() {

		return cam;
	}
	
	public static void main( String[] args ) {

		HelloIntersection app = new HelloIntersection();
		app.setConfigShowMode( ConfigShowMode.ShowIfNoConfig );
		app.start();
	}
	
	@Override
	protected void simpleInitGame() {

		// scene = new Node( "3D Scene Root" );
		// n1Target = new Node( "node1Target" );
		// n2Bullet = new Node( "node2Bullet" );
		
		this.setLogicTicksPerSecond( 60 );// seems this class is already running
		// at a fixed rate; thus setting this to 10 emulates crappy computer
		
		display.setVSyncEnabled( true );
		this.setupSound();
		
		// score thingy
		score = oldScore = 0;
		scoreText = Text.createDefaultTextLabel( "ScoreID", scoreTxt + score );
		scoreText.setLocalTranslation( new Vector3f( display.getWidth() / 2f - 8f, 0, 0 ) );
		statNode.attachChild( scoreText );
		assert statNode.getChild( "ScoreID" ) == scoreText;
		
		hits = 0;
		oldHits = hits - 1;// to force 1st update;
		hitsText = Text.createDefaultTextLabel( "hitsID" );
		hitsText.setLocalTranslation( new Vector3f( display.getWidth() / 2f - ( scoreText.getWidth() * 2f ), 0, 0 ) );
		statNode.attachChild( hitsText );
		
		assert statNode.getChild( "hitsID" ) == hitsText;
		assert 0 == hitsText.getWidth();
		
		liveBullets = 0;
		oldLiveBullets = liveBullets - 1;
		liveBulletsText = Text.createDefaultTextLabel( "livebulletsID" );
		liveBulletsText.setLocalTranslation( hitsText.getLocalTranslation().getX() - ( hitsText.getWidth() * 2f ), 30,
				0 );
		statNode.attachChild( liveBulletsText );
		
		/** Create a + for the middle of the screen */
		Text cross = Text.createDefaultTextLabel( "Crosshairs", "+" );
		
		// 8 is half the width of a font char
		/** Move the + to the middle */
		cross.setLocalTranslation( new Vector3f( display.getWidth() / 2f - 8f, display.getHeight() / 2f - 8f, 0 ) );
		statNode.attachChild( cross );
		

		target = new Sphere( "my sphere", 15, 15, 1 );
		

		Vector3f trans1 = new Vector3f( 1.5345f, -0.0397f, 0.0f );
		target.setLocalTranslation( trans1 );
		Quaternion q1 = new Quaternion( 0.1288f, -0.3779f, -0.8533f, 0.3353f );
		target.setLocalRotation( q1 );
		Vector3f scale1 = new Vector3f( 3.76f, 1.41f, 0.84f );
		target.setLocalScale( scale1 );
		// BoundingSphere x = new BoundingSphere();
		// BoundingVolume y = new BoundingSphere();
		// y = x.transform( q1.clone(), trans1.clone(), scale1.clone() );
		
		// target.setModelBound( new OrientedBoundingBox() );
		target.setModelBound( new OrientedBoundingBox() );// BUGGED with BoundingSphere
		target.updateModelBound();
		target.updateWorldBound();
		

		// results = new BoundingCollisionResults() {
		//
		// @Override
		// public void processCollisions() {
		//
		// if ( this.getNumber() > 0 ) {
		// collision = true;
		// } else {
		// collision = false;
		// }
		// }
		// };
		
		rootNode.attachChild( target );
		// n1Target.attachChild( target );
		// scene.attachChild( n1Target );
		// scene.attachChild( n2Bullet );
		// scene.attachChild(n2);
		
		// rootNode.attachChild( scene );
		
		/** Create a skybox to surround our world */
		this.setupSky();
		
		// Attach the skybox to our root node, and force the rootnode to show
		// so that the skybox will always show
		rootNode.attachChild( skybox );
		rootNode.setCullHint( Spatial.CullHint.Never );
		
		// create an action to shown button activity//copied from TestInputHandler.java
		InputAction buttonAction = new InputAction() {
			
			@Override
			public void performAction( InputActionEvent evt ) {

				// String actionString;
				if ( evt.getTriggerPressed() ) {
					( new FireBullet() ).performAction( evt );
				}
			}
		};
		
		/**
		 * Set the action called "firebullet", bound to KEY_F, to performAction
		 * FireBullet
		 */
		input.addAction( new FireBullet(), "firebullet", KeyInput.KEY_SPACE, false );
		input.addAction( new FireBullet(), "firebullet2", KeyInput.KEY_LCONTROL, true );
		input.addAction( buttonAction, InputHandler.DEVICE_MOUSE, 0/* left click */, InputHandler.AXIS_NONE, false );
		input.addAction( buttonAction, InputHandler.DEVICE_MOUSE, 1/* right click */, InputHandler.AXIS_NONE, true );
		
		/** Make bullet material */
		bulletMaterial = display.getRenderer().createMaterialState();
		bulletMaterial.setEmissive( ColorRGBA.green.clone() );
		
		/** Make target material */
		MaterialState redMaterial = display.getRenderer().createMaterialState();
		redMaterial.setDiffuse( ColorRGBA.red.clone() );
		target.setRenderState( redMaterial );
		
		// SceneMonitor.getMonitor().registerNode( rootNode, "Root Node" );
		// SceneMonitor.getMonitor().showViewer( true );
		
		SceneWorker.inst().initialiseSceneWorkerAndMonitor();
		SceneMonitor.getMonitor().registerNode( rootNode, "root" );
		SceneMonitor.getMonitor().registerNode( statNode, "stat" );
		// Initialize the application handler so we get tools palette, input handler and rendering
		// sceneWorkerHandler = new SceneWorkerAppHandler( this );
		// sceneWorkerHandler.initialise();
		
		startupSound.play();
	}
	
	private void setupSound() {

		/** Set the 'ears' for the sound API */
		AudioSystem audio = AudioSystem.getSystem();
		// audio.getEar().trackOrientation( cam );//3d if u comment this
		audio.getEar().trackPosition( cam );
		
		/** Create program sound */
		targetSound = audio.createAudioTrack( this.getClass().getResource( "/jmetest/data/sound/explosion.ogg" ), false );
		targetSound.setMaxAudibleDistance( 1000 );
		targetSound.setMinVolume( 0.1f );
		targetSound.setMaxVolume( 0.2f );
		targetSound.setVolume( 0.1f );
		laserSound = audio.createAudioTrack( this.getClass().getResource( "/jmetest/data/sound/laser.ogg" ), false );
		laserSound.setMaxAudibleDistance( 1000 );
		laserSound.setMinVolume( 0.1f );
		laserSound.setMaxVolume( 0.2f );
		laserSound.setVolume( 0.1f );
		
		startupSound = audio.createAudioTrack( this.getClass().getResource( "/jmetest/data/sound/CHAR_CRE_1.ogg" ),
				false );
		startupSound.setMinVolume( 0.1f );
		startupSound.setType( TrackType.POSITIONAL );
		startupSound.setMaxAudibleDistance( 10 );
		startupSound.setVolume( 0.6f );
	}
	
	private void setupSky() {

		skybox = new Skybox( "skybox", 200, 200, 200 );
		
		try {
			ResourceLocatorTool.addResourceLocator( ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator(
					this.getClass().getResource( "/jmetest/data/texture/" ) ) );
		} catch ( Exception e ) {
			logger.warning( "Unable to access texture directory." );
			e.printStackTrace();
		}
		
		skybox.setTexture( Skybox.Face.North, TextureManager.loadTexture( "north.jpg",
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear ) );
		skybox.setTexture( Skybox.Face.West, TextureManager.loadTexture( "west.jpg",
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear ) );
		skybox.setTexture( Skybox.Face.South, TextureManager.loadTexture( "south.jpg",
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear ) );
		skybox.setTexture( Skybox.Face.East, TextureManager.loadTexture( "east.jpg",
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear ) );
		skybox.setTexture( Skybox.Face.Up, TextureManager.loadTexture( "top.jpg", Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear ) );
		skybox.setTexture( Skybox.Face.Down, TextureManager.loadTexture( "bottom.jpg",
				Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear ) );
		skybox.preloadTextures();
		
		CullState cullState = display.getRenderer().createCullState();
		cullState.setCullFace( CullState.Face.None );
		cullState.setEnabled( true );
		skybox.setRenderState( cullState );
		
		skybox.updateRenderState();
	}
	
	class FireBullet extends KeyInputAction {
		
		int	numBullets;
		
		@Override
		public void performAction( InputActionEvent evt ) {

			logger.info( "BANG" );
			/** Create bullet */
			liveBullets++;
			Sphere bullet = new Sphere( "bullet" + numBullets++, 8, 8, .25f );
			bullet.setModelBound( new BoundingBox() );
			bullet.updateModelBound();
			/** Move bullet to the camera location */
			Vector3f lT = cam.getLocation().add( cam.getDirection().mult( 2f ) );
			// bullet.setLocalTranslation( new Vector3f( cam.getLocation() ) );
			bullet.setLocalTranslation( lT );
			bullet.setRenderState( bulletMaterial );
			/**
			 * Update the new world location for the bullet before I add a
			 * controller
			 */
			bullet.updateGeometricState( 0, true );
			/**
			 * Add a movement controller to the bullet going in the camera's
			 * direction
			 */
			Tube tube;
			tube = new Tube( "tuberay", 0.2f, 0.1f, 1f );
			tube.setModelBound( new OrientedBoundingBox() );
			tube.updateModelBound();
			// tube.lookAt( lT, cam.getDirection() );
			rootNode.attachChild( tube );
			tube.updateRenderState();
			
			bullet.addController( new BulletMover( tube, bullet, new Vector3f( cam.getDirection().clone() ),
					cam.getUp().clone() ) );
			assert cam.getUp() != cam.getUp().clone();
			// bullet.addController( new BulletMover( bullet, lT ) );
			// n2Bullet.attachChild( bullet );
			rootNode.attachChild( bullet );
			bullet.updateRenderState();
			/** Signal our sound to play laser during rendering */
			// laserSound.setWorldPosition( cam.getLocation() );
			laserSound.setWorldPosition( lT );
			laserSound.play();
			

		}
	}
	
	class BulletMover extends Controller {
		
		private static final long	serialVersionUID	= 1L;
		/** Bullet that's moving */
		TriMesh						bullet;
		Tube						tube;
		
		/** Direction of bullet */
		Vector3f					direction;
		Vector3f					up;
		
		/** speed of bullet */
		float						speed				= 10;		// if too big,
		// it might miss
		float						vary				= 0;
		float						maxVary				= 0.1f;
		float						varyStep			= 0.01f;
		float						vary2Step			= 0.02f;
		boolean						dir					= true;
		float						vary2				= 0;
		
		/** Seconds it will last before going away */
		float						lifeTime			= 50;
		
		BulletMover( Tube tube1, TriMesh bullet1, Vector3f direction1, Vector3f up1 ) {

			tube = tube1;
			bullet = bullet1;
			
			// Quaternion q = new Quaternion( direction.x, direction.y, direction.z, 0 );
			// up = q.getRotationColumn( 1 );
			// LWJGLCamera ac = new LWJGLCamera( display.getWidth(), display.getHeight(), false );
			// ac.setDirection( direction1 );
			// ac.update();
			// up = ac.getUp();
			// up = cam.getUp();
			// direction = ac.getDirection();
			
			direction = direction1;
			direction.normalizeLocal();
			// Quaternion q = new Quaternion( direction.x, direction.y, direction.z, 1 );
			// Quaternion q = new Quaternion();
			// q.fromAngleAxis( 3, direction );
			// Vector3f[] axis = new Vector3f[3];
			// q.toAxes( axis );
			//
			// up = axis[1];
			

			up = up1;
			up.normalizeLocal();
			System.out.println( "UP:" + up );
			System.out.println( "dir:" + direction );
		}
		
		@Override
		public void update( float time ) {

			// SceneMonitor.getMonitor().updateViewer( 0 );
			lifeTime -= time;
			/** If life is gone, remove it */
			if ( lifeTime < 0 ) {
				liveBullets--;
				assert liveBullets >= 0;
				// n2Bullet.detachChild( bullet );
				rootNode.detachChild( bullet );
				boolean test1 = bullet.removeController( this );
				assert test1;
				return;
			}
			/** Move bullet */
			Vector3f bulletPos = bullet.getLocalTranslation();
			bulletPos.addLocal( direction.mult( time * speed ) );
			// bulletPos.addLocal( direction.mult( time * speed ) );
			// bulletPos.addLocal( direction.mult( FastMath.sin( time * speed ) ) );
			
			if ( dir ) {
				vary += varyStep;
				vary2 += vary2Step;
				if ( vary >= maxVary ) {
					dir = false;
				}
			} else {
				vary -= varyStep;
				vary2 -= vary2Step;
				if ( vary <= -maxVary ) {
					dir = true;
				}
			}
			
			// float x = direction.x * time * speed + direction.x * vary;
			// float y = direction.y * time * speed + direction.y * vary;
			// float z = direction.z * time * speed + direction.z * vary;
			// up = direction.clone();
			// up.y = FastMath.abs( up.y );
			// up = direction.cross( Vector3f.UNIT_Y );
			// up.normalizeLocal();
			// Quaternion q = new Quaternion( direction.x, direction.y, direction.z, 1 );
			// Vector3f[] axis = new Vector3f[3];
			// q.toAxes( axis );
			// up = axis[0];
			// LWJGLCamera ac = new LWJGLCamera( display.getWidth(), display.getHeight(), true );
			// ac.setDirection( direction );
			// ac.update();
			// up = ac.getUp();
			// up.normalizeLocal();
			// Quaternion q = new Quaternion();
			// q.fromAngleAxis( 90, direction );
			// q.toAxes( axis )
			// up = q.getRotationColumn( 1 );
			// q.lookAt( direction, Vector3f.UNIT_Y );
			// up.x = q.getX();
			// up.y = q.getY();
			// up.z = q.getZ();
			
			float x = up.x * 1 * vary;
			float y = up.y * 1 * vary;
			float z = up.z * 1 * vary;
			Vector3f res = new Vector3f( x, y, z );
			


			// res = cam.getUp().clone();
			// res.multLocal( vary );
			// res.multLocal( cam.getUp() );
			// res.normalizeLocal();
			// res.cross( cam.getUp() );
			bulletPos.addLocal( res );// direction.x + x, direction.y + y, direction.z + z );
			tube.setLocalTranslation( bulletPos.add( up.mult( 1f ) ) );
			tube.lookAt( bulletPos, direction );
			// tube.lookAt( direction, up );
			
			// direction.x = time * FastMath.sin( direction.x );
			// direction.y = time * FastMath.sin( direction.y );
			// direction.z = time * FastMath.sin( direction.z );
			// // direction.addLocal( cam.getDirection() );
			// direction.normalizeLocal();
			
			bullet.setLocalTranslation( bulletPos );
			/** Does the bullet intersect with target? */
			// if ( bullet.getWorldBound().intersects( target.getWorldBound() ) ) {
			if ( bullet.hasCollision( target, true ) ) {
				score += scoreWhenBulletHits;
				hits++;
				logger.info( "OUCH!!!" );
				targetSound.setWorldPosition( target.getWorldTranslation() );
				
				// target.setLocalTranslation( new Vector3f( r.nextFloat() * 10, r.nextFloat() * 10, r.nextFloat() * 10
				// ) );
				
				lifeTime = 0;
				
				targetSound.play();
			}
		}
	}
	
	/**
	 * Called every frame for updating
	 */
	@Override
	protected void simpleUpdate() {

		if ( oldScore != score ) {
			oldScore = score;
			scoreText.print( scoreTxt + score );
			// scoreText.
			// Spatial a = statNode.getChild( "Score" );
			// scoreText = Text.createDefaultTextLabel( "Score", "Score:" + score );
			// statNode.detachChildNamed( "Score" );
		}
		if ( hits != oldHits ) {
			oldHits = hits;
			hitsText.print( hitsTxt + hits );
		}
		if ( liveBullets != oldLiveBullets ) {
			oldLiveBullets = liveBullets;
			liveBulletsText.print( liveBulletsTxt + liveBullets );
		}
		// Let the programmable sound update itself.
		AudioSystem.getSystem().update();
		// Move the skybox into position
		skybox.getLocalTranslation().set( cam.getLocation().x, cam.getLocation().y, cam.getLocation().z );
		
		// update the monitor panel
		SceneMonitor.getMonitor().updateViewer( tpf );
		
		// update scene worker tools
		// sceneWorkerHandler.update();
		
		// results.clear();
		// n1Target.calculateCollisions( scene, results );
		// if ( n1Target.hasCollision( scene, false ) ) {
		// logger.info( "hasCollision also reports true" );
		// }
	}
	
	@Override
	protected void simpleRender() {

		// do scene worker render
		SceneMonitor.getMonitor().renderViewer( display.getRenderer() );
		// do scene worker render
		// sceneWorkerHandler.render();
		
	}
	
	@Override
	protected void cleanup() {

		super.cleanup();
		if ( AudioSystem.isCreated() ) {
			AudioSystem.getSystem().cleanup();
		}
	}
}
