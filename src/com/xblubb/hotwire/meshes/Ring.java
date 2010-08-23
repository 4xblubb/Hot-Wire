package com.xblubb.hotwire.meshes;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import com.xblubb.framework.Mesh;
import com.xblubb.framework.MeshLoader;
import com.xblubb.framework.Texture;
import com.xblubb.framework.Mesh.PrimitiveType;
import com.xblubb.framework.Texture.TextureFilter;
import com.xblubb.framework.Texture.TextureWrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Ring 
{
	Texture texture;
	Mesh mesh;
	
	public Ring(Context ctx, GL10 gl) throws IOException {
		mesh = MeshLoader.loadObj( gl, ctx.getAssets().open( "ring.obj" ) );
		Bitmap bitmap = BitmapFactory.decodeStream( ctx.getAssets().open( "ring.bmp" ) );
		texture = new Texture( gl, bitmap, TextureFilter.Linear, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}	
	
	public void render(GL10 gl, float angle){
		gl.glPushMatrix();

		gl.glEnable( GL10.GL_TEXTURE_2D );
		texture.bind();		
		
		gl.glRotatef( angle, 0, 1, 0 );
		mesh.render( PrimitiveType.Triangles );
		gl.glPopMatrix();
	}
}
