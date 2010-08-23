package com.xblubb.framework;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * The game activity implements the Activity Life Cycle of the
 * game. A {@link GameListener} can be attached to the activity and will
 * be called whenever a new frame has to be rendered or the
 * application needs to be setup.
 *  
 * @author mzechner
 *
 */
public class GameActivity extends Activity implements GLSurfaceView.Renderer, OnTouchListener, SensorEventListener
{	
	/** GLSurfaceView **/
	private GLSurfaceView glSurface;

	/** with and height of the viewport **/
	private int width, height;

	/** GameListener **/
	private GameListener listener;	

	/** start time of last frame in nano seconds **/
	long lastFrameStart;

	/** delta time in seconds **/
	float deltaTime;

	/** touch coordinates and touched state **/
	int touchX, touchY;
	boolean isTouched;

	/** acceleration on the 3 axis **/
	float[] acceleration = new float[3];

	/**
	 * Called on creation of the Activity
	 */
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		glSurface = new GLSurfaceView( this );
//		glSurface.setEGLConfigChooser(new EGLConfigChooser() {
//
//			@Override
//			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) 
//			{
//				final int R = 4;
//				final int G = 4;
//				final int B = 4;
//				final int A = 0;
//
//				/* The best configuration that was found. */
//				EGLConfig best = null;
//
//				/* Temporary configurations that are found during the process. */
//				EGLConfig[] configs = new EGLConfig[100];
//
//				/* Number of configurations found during a choose configuration. */
//				int[] result = new int[1];
//
//				/* Desired configuration attributes. */
//				int[] attr;
//
//				/* Starting depth. */
//				int depth = 24;
//
//				/*
//				 * Search for configurations that support 24-bit depth first. This
//				 * will handle the Droid case where a higher depth buffer size
//				 * renders faster.
//				 */
//				while ((depth > 4) && (best == null))
//				{
//					Log.i("OGL", "Searching with depth of '"
//							+ Integer.toString(depth) + "' bits.");
//
//					attr = new int[] {
//							EGL10.EGL_RED_SIZE, R, EGL10.EGL_GREEN_SIZE, G,
//							EGL10.EGL_BLUE_SIZE, B, EGL10.EGL_ALPHA_SIZE, A,
//							EGL10.EGL_DEPTH_SIZE, depth, EGL10.EGL_NONE };
//
//					egl.eglChooseConfig(display, attr, configs, configs.length, result);
//
//					for (int i = 0; i < result[0]; i++)
//					{
//						Log.i("OGL", "   Candidate " + Integer.toString(i + 1) + ":");
//						logConfig(egl, display, configs[i]);
//						Log.i("OGL", "   ---------------------------------------");
//					}
//
//					if (result[0] > 0)
//					{
//						best = configs[result[0]-1];
//
//						Log.i("OGL", "Best Config: ");
//						logConfig(egl, display, best);
//
//						break;
//					}
//
//					depth -= 4;
//
//					
//				}
//
//				return best;
//
//			}
//			
//			private void logConfig( EGL10 egl, EGLDisplay display, EGLConfig config )
//			{
//				int r = 0;
//				int g = 0;
//				int b = 0;
//				int a = 0;
//				int d = 0;
//				int s = 0;
//				int mValue[] = new int[1];
//				if( egl.eglGetConfigAttrib(display, config, EGL10.EGL_RED_SIZE, mValue) )
//					r = mValue[0];
//				if( egl.eglGetConfigAttrib(display, config, EGL10.EGL_GREEN_SIZE, mValue) )
//					g = mValue[0];
//				if( egl.eglGetConfigAttrib(display, config, EGL10.EGL_BLUE_SIZE, mValue) )
//					b = mValue[0];
//				if( egl.eglGetConfigAttrib(display, config, EGL10.EGL_ALPHA_SIZE, mValue) )
//					a = mValue[0];
//				if( egl.eglGetConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, mValue) )
//					d = mValue[0];
//				if( egl.eglGetConfigAttrib(display, config, EGL10.EGL_STENCIL_SIZE, mValue) )
//					s = mValue[0];
//				
//				Log.d( "OGL", r + "" + g + "" + b + "" + a + ":" + d + ":" + s );
//			}
//		});
		glSurface.setRenderer( this );
		this.setContentView( glSurface );

		glSurface.setOnTouchListener( this );

		SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		if( manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0 )
		{
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME );
		}
}

/**
 * Called each Frame
 */
@Override
public void onDrawFrame(GL10 gl) 
{						
	long currentFrameStart = System.nanoTime();
	deltaTime = (currentFrameStart-lastFrameStart) / 1000000000.0f;
	lastFrameStart = currentFrameStart;			

	if( listener != null )
		listener.mainLoopIteration( this, gl );		
}

/**
 * Called when the surface size changed, e.g. due to tilting
 */
@Override
public void onSurfaceChanged(GL10 gl, int width, int height) 
{	
	this.width = width;
	this.height = height;		
}

/**
 * Called when the GLSurfaceView has finished initialization
 */
@Override
public void onSurfaceCreated(GL10 gl, EGLConfig config) 
{			
	lastFrameStart = System.nanoTime();
	String renderer = gl.glGetString( GL10.GL_RENDERER );
	if( renderer.toLowerCase().contains("pixelflinger" ) )
		Mesh.globalVBO = false;

	if( listener != null )
		listener.setup( this, gl );
}

/**
 * Called when the application is paused. We need to
 * also pause the GLSurfaceView.
 */
@Override
protected void onPause() {
	super.onPause();
	glSurface.onPause();		
}

/**
 * Called when the application is resumed. We need to
 * also resume the GLSurfaceView.
 */
@Override
protected void onResume() {
	super.onResume();
	glSurface.onResume();		
}		

/**
 * Called when a touch event occurs.
 */
@Override
public boolean onTouch(View v, MotionEvent event) 
{
	if( event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE )
	{
		touchX = (int)event.getX();
		touchY = (int)event.getY();
		isTouched = true;
	}

	if( event.getAction() == MotionEvent.ACTION_UP )
		isTouched = false;

	try
	{
		Thread.sleep( 30 );
	}
	catch( Exception ex )
	{

	}			

	return true;
}

/** 
 * Called when the accuracy of the Sensor has changed.
 * @param sensor
 * @param accuracy
 */
@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) 
{
	// we ignore this
}

/**
 * Called when the sensor has new input
 * @param event The SensorEvent
 */
@Override
public void onSensorChanged(SensorEvent event) {
	System.arraycopy( event.values, 0, acceleration, 0, 3 );
}

/**
 * Sets the {@link GameListener}
 * @param listener the GameListener
 */
public void setGameListener(GameListener listener) 
{	
	this.listener = listener;		
}

/**
 * @return the viewport width in pixels
 */
public int getViewportWidth( )
{
	return width;
}

/**
 * @return the viewport height in pixels
 */
public int getViewportHeight( )
{
	return height;
}

/**
 * @return the delta time in seconds
 */
public float getDeltaTime( )
{
	return deltaTime;
}

/**
 * @return the last known touch coordinate on the x axis
 */
public int getTouchX( )
{
	return touchX;
}

/**
 * @return the last known touch coordinate on the x axis
 */
public int getTouchY( )
{
	return touchY;
}

/**
 * @return wheter the touch screen is touched or not
 */
public boolean isTouched( )
{
	return isTouched;
}

/**
 * @return the acceleration on the x-Axis of the device
 */
public float getAccelerationOnXAxis( )
{
	return acceleration[0];
}

/**
 * @return the acceleration on the x-Axis of the device
 */
public float getAccelerationOnYAxis( )
{
	return acceleration[1];
}

/**
 * @return the acceleration on the x-Axis of the device
 */
public float getAccelerationOnZAxis( )
{
	return acceleration[2];
}
}
