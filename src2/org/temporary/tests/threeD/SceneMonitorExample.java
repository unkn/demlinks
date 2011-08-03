

package org.temporary.tests.threeD;



import jmetest.effects.water.TestProjectedWater;

import com.acarter.scenemonitor.SceneMonitor;



/**
 * @author Carter
 * 
 */
public class SceneMonitorExample extends TestProjectedWater {
	
	@Override
	protected void simpleInitGame() {

		super.simpleInitGame();
		
		SceneMonitor.getMonitor().registerNode( rootNode, "Root Node" );
		SceneMonitor.getMonitor().showViewer( true );
		display.setVSyncEnabled( true );
	}
	
	@Override
	protected void simpleUpdate() {

		super.simpleUpdate();
		
		SceneMonitor.getMonitor().updateViewer( tpf );
	}
	
	@Override
	protected void simpleRender() {

		super.simpleRender();
		
		SceneMonitor.getMonitor().renderViewer( display.getRenderer() );
	}
	
	@Override
	protected void cleanup() {

		super.cleanup();
		
		SceneMonitor.getMonitor().cleanup();
	}
	
	public static void main( String[] args ) {

		SceneMonitorExample app = new SceneMonitorExample();
		
		app.setConfigShowMode( ConfigShowMode.ShowIfNoConfig );
		app.start();
	}
}
