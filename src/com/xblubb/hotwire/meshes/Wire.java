package com.xblubb.hotwire.meshes;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import com.xblubb.framework.Mesh;
import com.xblubb.framework.MeshLoader;
import com.xblubb.framework.Texture;
import com.xblubb.framework.Mesh.PrimitiveType;

import android.content.Context;

public class Wire 
{
	Texture texture;
	Mesh mesh;
	
	public Wire(Context ctx, GL10 gl) throws IOException {
		mesh = MeshLoader.loadObj( gl, ctx.getAssets().open( "wire1.obj" ) );
//		Bitmap bitmap = BitmapFactory.decodeStream( ctx.getAssets().open( "ring.bmp" ) );
//		texture = new Texture( gl, bitmap, TextureFilter.Linear, TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}
	
	public void render(GL10 gl){		
		gl.glPushMatrix();
		
//		gl.glEnable( GL10.GL_TEXTURE_2D );
//		texture.bind();		
		
//		gl.glRotatef( 67, 0, 1, 0 );
//		gl.glDisable( GL10.);
		
		mesh.render( PrimitiveType.Triangles );
		
		gl.glPopMatrix();
	}
}
