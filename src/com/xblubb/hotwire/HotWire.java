package com.xblubb.hotwire;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import com.xblubb.framework.GameActivity;
import com.xblubb.framework.GameListener;
import com.xblubb.hotwire.meshes.Ring;
import com.xblubb.hotwire.meshes.Wire;

import android.hardware.Sensor;
import android.opengl.GLU;
import android.os.Bundle;

public class HotWire extends GameActivity implements GameListener
{
	Ring ring;
	Wire wire;
    float angle = 0;
    int angleY = 0;
	
	public void onCreate( Bundle savedInstance )
	{
		super.onCreate( savedInstance );
		setGameListener( this );
	}

	@Override
	public void setup(GameActivity activity, GL10 gl) 
	{	
		try {
			ring 	  = new Ring(this, gl);	
			wire 	  = new Wire(this, gl);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException( "Couldn't load mesh" );
		}
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		angle = this.getAccelerationOnYAxis() * 10;
		angleY = (int) this.getAccelerationOnXAxis();
		
		if( angle > 360 )
			angle = 0;
		
		if( angleY > 360 )
			angleY = 0;		
	}	
	
	@Override
	public void mainLoopIteration(GameActivity activity, GL10 gl) 
	{	
		gl.glClear( GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT );
		gl.glViewport( 0, 0, activity.getViewportWidth(), activity.getViewportHeight() );
		
		gl.glEnable( GL10.GL_DEPTH_TEST );
		
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		float aspectRatio = (float)activity.getViewportWidth() / activity.getViewportHeight();
		GLU.gluPerspective( gl, 67, aspectRatio, 1, 100 );

		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		GLU.gluLookAt( gl, 2.5f, 2.5f, 2.5f, 0, 0, 0, 0, 1, 0 );
		
		ring.render(gl, angle);
		wire.render(gl);
	}	
}