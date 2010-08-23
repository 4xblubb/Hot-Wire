package com.xblubb.framework;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class Texture {
	/**
	 * Texture filter enum featuring the 3 most used filters
	 * @author mzechner
	 *
	 */
	public enum TextureFilter
	{
		Nearest,
		Linear,
		MipMap
	}
	
	/**
	 * Texture wrap enum
	 * @author mzechner
	 *
	 */
	public enum TextureWrap
	{
		ClampToEdge,
		Wrap
	}
	
	/** the texture handle **/
	private int textureHandle;
	/** handle to gl wrapper **/
	private GL10 gl;
	/** height of original image in pixels **/
	private int height;    
	/** width of original image in pixels **/
	private int width;        	
		
	/**
	 * Creates a new texture based on the given image
	 * 
	 * @param gl
	 * @param bitmap
	 */
	public Texture( GL10 gl, Bitmap image, TextureFilter minFilter, TextureFilter maxFilter, TextureWrap sWrap, TextureWrap tWrap )
	{
		this.gl = gl;
	
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureHandle = textures[0];
		
		this.width = image.getWidth();
		this.height = image.getHeight();		
		
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureHandle );
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, getTextureFilter( minFilter ) );
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, getTextureFilter( maxFilter ) );
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, getTextureWrap( sWrap ) );
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, getTextureWrap( tWrap ) );		
        gl.glMatrixMode( GL10.GL_TEXTURE );
        gl.glLoadIdentity();
		buildMipmap( gl, image );
		image.recycle();			
	}		

	public Texture(GL10 gl, int width, int height, TextureFilter minFilter, TextureFilter maxFilter,
			TextureWrap sWrap, TextureWrap tWrap) 
	{
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap image = Bitmap.createBitmap(width, height, config);
	
		this.gl = gl;
		
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureHandle = textures[0];
		
		this.width = image.getWidth();
		this.height = image.getHeight();		
		
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureHandle );
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, getTextureFilter( minFilter ) );
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, getTextureFilter( maxFilter ) );
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, getTextureWrap( sWrap ) );
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, getTextureWrap( tWrap ) );		
        gl.glMatrixMode( GL10.GL_TEXTURE );
        gl.glLoadIdentity();
		buildMipmap( gl, image );
		image.recycle();		
	}
	

	private int getTextureFilter( TextureFilter filter )
	{
		if( filter == TextureFilter.Linear )
			return GL10.GL_LINEAR;
		else
		if( filter == TextureFilter.Nearest )
			return GL10.GL_NEAREST;
		else
			return GL10.GL_LINEAR_MIPMAP_NEAREST;
	}
	
	private int getTextureWrap( TextureWrap wrap )
	{
		if( wrap == TextureWrap.ClampToEdge )
			return GL10.GL_CLAMP_TO_EDGE;
		else
			return GL10.GL_REPEAT;
	}

	private void buildMipmap(GL10 gl, Bitmap bitmap) 
	{

		int level = 0;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();	      	       		

		while(height >= 1 || width >= 1 && level < 4 ) {
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
			if(height == 1 || width == 1) 
			{
				break;
			}

			level++;
			if( height > 1 )
				height /= 2;
			if( width > 1 )
				width /= 2;

			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			bitmap.recycle();
			bitmap = bitmap2;
		}		
	}



	/**
	 * Draws the given image to the texture
	 * @param gl
	 * @param bitmap
	 * @param x
	 * @param y
	 */
	public void draw( Object bmp, int x, int y )
	{
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureHandle );		
		Bitmap bitmap = (Bitmap)bmp;
		int level = 0;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();	      	       		

		while(height >= 1 || width >= 1 && level < 4 ) {
			GLUtils.texSubImage2D( GL10.GL_TEXTURE_2D, level, x, y, (Bitmap)bitmap );
			if(height == 1 || width == 1) 
			{
				break;
			}

			level++;
			if( height > 1 )
				height /= 2;
			if( width > 1 )
				width /= 2;

			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			bitmap.recycle();
			bitmap = bitmap2;
		}	
	}

	/**
	 * Binds the texture
	 * @param gl
	 */
	public void bind(  )
	{				
		gl.glBindTexture( GL10.GL_TEXTURE_2D, textureHandle );
	}

	/**
	 * Disposes the texture and frees the associated resourcess
	 * @param gl
	 */
	public void dispose( )
	{
		int[] textures = { textureHandle };
		gl.glDeleteTextures( 1, textures, 0 );
		textureHandle = 0;		
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
