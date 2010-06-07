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



import java.util.Random;
import java.util.logging.Logger;

import com.acarter.scenemonitor.SceneMonitor;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.intersection.CollisionResults;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;



/**
 * Started Date: Jul 24, 2004 <br>
 * <br>
 * Demonstrates intersection testing, sound, and making your own controller.
 * 
 * @author Jack Lindamood
 */
public class OrientedBoundingBoxExampleTest extends SimpleGame {
	
	private static final Logger	logger	= Logger.getLogger( OrientedBoundingBoxExampleTest.class.getName() );
	
	/** Material for my bullet */
	MaterialState				bulletMaterial;
	
	/** Target you're trying to hit */
	Sphere						target;
	
	/** Used to move target location on a hit */
	Random						r		= new Random();
	
	/** A sky box for our scene. */
	Skybox						skybox;
	
	private boolean				collided;
	private CollisionResults	results;
	private Node				scene, n1Target, n2Bullet;
	
	
	public static void main( String[] args ) {

		OrientedBoundingBoxExampleTest app = new OrientedBoundingBoxExampleTest();
		app.setConfigShowMode( ConfigShowMode.ShowIfNoConfig );
		app.start();
	}
	
	@Override
	protected void simpleInitGame() {

		display.setVSyncEnabled( true );
		
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
		
		target.setModelBound( new OrientedBoundingBox() );// BUGGED?
		target.updateModelBound();
		

		results = new BoundingCollisionResults() {
			
			@Override
			public void processCollisions() {

				if ( this.getNumber() > 0 ) {
					collided = true;
				} else {
					collided = false;
				}
			}
		};
		
		scene = new Node( "3D Scene Root" );
		n1Target = new Node( "node1Target" );
		n2Bullet = new Node( "node2Bullet" );
		
		n1Target.attachChild( target );
		scene.attachChild( n1Target );
		scene.attachChild( n2Bullet );
		
		rootNode.attachChild( scene );
		
		/** Create a skybox to surround our world */
		this.setupSky();
		
		// Attach the skybox to our root node, and force the rootnode to show
		// so that the skybox will always show
		rootNode.attachChild( skybox );
		rootNode.setCullHint( Spatial.CullHint.Never );
		
		// create an action to shown button activity//copied from TestInputHandler.java
		InputAction buttonAction = new InputAction() {
			
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
			Sphere bullet = new Sphere( "bullet" + numBullets++, 8, 8, .25f );
			bullet.setModelBound( new BoundingSphere() );
			bullet.updateModelBound();
			/** Move bullet to the camera location */
			bullet.setLocalTranslation( new Vector3f( cam.getLocation() ) );
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
			bullet.addController( new BulletMover( bullet, new Vector3f( cam.getDirection() ) ) );
			n2Bullet.attachChild( bullet );
			bullet.updateRenderState();
		}
	}
	
	class BulletMover extends Controller {
		
		private static final long	serialVersionUID	= 1L;
		/** Bullet that's moving */
		TriMesh						bullet;
		
		/** Direction of bullet */
		Vector3f					direction;
		
		/** speed of bullet */
		float						speed				= 10;	// if too big,
		// it might miss
		
		/** Seconds it will last before going away */
		float						lifeTime			= 50;
		
		BulletMover( TriMesh bullet1, Vector3f direction1 ) {

			bullet = bullet1;
			direction = direction1;
			direction.normalizeLocal();
		}
		
		@Override
		public void update( float time ) {

			// SceneMonitor.getMonitor().updateViewer( 0 );
			lifeTime -= time;
			/** If life is gone, remove it */
			if ( lifeTime < 0 ) {
				n2Bullet.detachChild( bullet );
				boolean test1 = bullet.removeController( this );
				assert test1;
				return;
			}
			/** Move bullet */
			Vector3f bulletPos = bullet.getLocalTranslation();
			bulletPos.addLocal( direction.mult( time * speed ) );
			bullet.setLocalTranslation( bulletPos );
			/** Does the bullet intersect with target? */
			if ( bullet.getWorldBound().intersects( target.getWorldBound() ) ) {
				logger.info( "OUCH!!!" );
				
				// target.setLocalTranslation( new Vector3f( r.nextFloat() * 10, r.nextFloat() * 10, r.nextFloat() * 10
				// ) );
				
				lifeTime = 0;
				
			}
		}
	}
	
	/**
	 * Called every frame for updating
	 */
	@Override
	protected void simpleUpdate() {

		// Move the skybox into position
		skybox.getLocalTranslation().set( cam.getLocation().x, cam.getLocation().y, cam.getLocation().z );
		
		// update the monitor panel
		SceneMonitor.getMonitor().updateViewer( tpf );
		
		// update scene worker tools
		// sceneWorkerHandler.update();
		
		results.clear();
		n1Target.calculateCollisions( scene, results );
		if ( n1Target.hasCollision( scene, false ) ) {
			logger.info( "hasCollision also reports true" );
		}
	}
	

}
