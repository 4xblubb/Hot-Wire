package com.xblubb.framework;

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import com.xblubb.framework.Mesh.PrimitiveType;
import com.xblubb.framework.Texture.TextureFilter;
import com.xblubb.framework.Texture.TextureWrap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;



public class Font
{	
	public class Rectangle 
	{
		public float x;
		public float y;
		public float width;
		public float height;
		
		public Rectangle( )
		{
			x = 0;
			y = 0;
			width = 0;
			height = 0;
		}
		
		public Rectangle( float x, float y, float width, float height )
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	
	/**
	 * A FontStyle defines the style of a font
	 * @author mzechner
	 *
	 */
	public enum FontStyle
	{
		Plain,
		Bold,
		Italic,
		BoldItalic
	}
	
	/** glyph hashmap **/
	private final HashMap<Character, Glyph> glyphs = new HashMap<Character, Glyph>( );
	/** current position in glyph texture to write the next glyph to **/
	private int glyphX = 0;
	private int glyphY = 0;
	private Typeface font;
	private Paint paint;
	private FontMetrics metrics;
	private final Texture texture;
	
	public Font(GL10 gl, String fontName, int size, FontStyle style) 
	{
		this.texture = new Texture( gl, 256, 256, TextureFilter.Nearest, TextureFilter.Nearest, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
		font = Typeface.create( fontName, getFontStyle( style ) );
		paint = new Paint( );
		paint.setTypeface(font);
		paint.setTextSize(size);
		paint.setAntiAlias(false);
		metrics = paint.getFontMetrics();		
	}

	public Font(GL10 gl, AssetManager assets, String file, int size,	FontStyle style) 
	{		
		this.texture = new Texture( gl, 256, 256, TextureFilter.Nearest, TextureFilter.Nearest, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
		font = Typeface.createFromAsset( assets, file );
		paint = new Paint( );
		paint.setTypeface(font);
		paint.setTextSize(size);		
		paint.setAntiAlias(false);
		metrics = paint.getFontMetrics();
	}

	private int getFontStyle( FontStyle style )
	{
		if( style == FontStyle.Bold )
			return Typeface.BOLD;
		if( style == FontStyle.BoldItalic )
			return Typeface.BOLD_ITALIC;
		if( style == FontStyle.Italic )
			return Typeface.ITALIC;
		if( style == FontStyle.Plain )
			return Typeface.NORMAL;
		
		return Typeface.NORMAL;
	}
		
	public int getGlyphAdvance(char character) {
		float[] width = new float[1];
		paint.getTextWidths( "" + character, width );
		return (int)(Math.ceil(width[0]));
	}
	
	public Object getGlyphBitmap(char character) {
		Rect rect = new Rect();		
		paint.getTextBounds( "" + character, 0, 1, rect );		
		Bitmap bitmap = Bitmap.createBitmap( rect.width()==0?1:rect.width() + 5, getLineHeight(), Bitmap.Config.ARGB_8888 );
		Canvas g = new Canvas( bitmap );		
		paint.setColor(0x00000000);
		paint.setStyle(Style.FILL);
		g.drawRect( new Rect( 0, 0, rect.width() + 5, getLineHeight()), paint);
		paint.setColor(0xFFFFFFFF);		
		g.drawText( "" + character, 0, -metrics.ascent, paint );		
		return bitmap;
	}

	public int getLineGap() {	
		return (int)(Math.ceil(metrics.leading));
	}

	public int getLineHeight() {	
		return (int)Math.ceil(Math.abs(metrics.ascent) + Math.abs(metrics.descent));
	}

	public int getStringWidth(String text) 
	{		
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		return rect.width();
	}

	Rect tmpRect = new Rect();
	public void getGlyphBounds(char character, Rectangle rect) {		
		paint.getTextBounds( "" + character, 0, 1, tmpRect );
		rect.width = tmpRect.width() + 5;
		rect.height = getLineHeight();
	}	
	
	/**
	 * Creates a new text run
	 * @return The new text run
	 */
	public Text newText( GL10 gl )
	{
		return new Text(gl);
	}
	
	/**
	 * @return The glyph texture
	 */
	protected Texture getTexture( )
	{
		return texture;
	}
	
	/**
	 * Returns the glyph for the given character
	 * @param character The character
	 * @return The glyph of the character
	 */
	protected Glyph getGlyph( char character )
	{
		Glyph glyph = glyphs.get(character);
		if( glyph == null )
		{
			glyph = createGlyph(character);
			glyphs.put( character, glyph );
		}
		return glyph;
	}
	
	private Glyph createGlyph( char character )
	{
		Object bitmap = getGlyphBitmap( character );
		Rectangle rect = new Rectangle( );
		getGlyphBounds( character, rect );

		if( glyphX + rect.width >= 256)
		{
			glyphX = 0;
			glyphY += getLineGap() + getLineHeight();
		}
		
		texture.draw( bitmap, glyphX, glyphY );		
						
		Glyph glyph = new Glyph( getGlyphAdvance( character ), (int)rect.width, (int)rect.height, glyphX / 256.0f, glyphY / 256.0f, rect.width / 256.0f, rect.height / 256.0f );
		glyphX += rect.width;
		return glyph;	
	}
	
	
	private class Glyph
	{
		public int advance;
		public int width;
		public int height;
		public float u;
		public float v;
		public float uWidth;
		public float vHeight;
		
		public Glyph( int advance, int width, int height, float u, float v, float uWidth, float vHeight )
		{
			this.advance = advance;
			this.width = width;
			this.height = height;
			this.u = u;
			this.v = v;
			this.uWidth = uWidth;
			this.vHeight = vHeight;
		}
	}
	
	/**
	 * A textrun is a mesh that holds the glyphs
	 * of the given string formated to fit the
	 * rectangle and alignment.
	 * 
	 * @author mzechner
	 *
	 */
	public class Text
	{
		private GL10 gl;			
		private Mesh mesh;
		private String text = "";
		private int width;
		private int height;
		private HorizontalAlign hAlign;
		private VerticalAlign vAlign;
		private boolean wordWrap = false;
		private String[] lines;
		private int[] widths;
		private float posX, posY;
		
		protected Text( GL10 gl )
		{			
			this.gl = gl;			
		}
		
		public void setTextArea( int width, int height )
		{
			this.width = width;
			this.height = height;
		}
		
		public void setHorizontalAlign( HorizontalAlign hAlign )
		{
			this.hAlign = hAlign;
		}
		
		public void setVerticalAlign( VerticalAlign vAlign )
		{
			this.vAlign = vAlign;
		}
		
		public void setText( String text )
		{					
			if( this.text.equals( text ) )
				return;
			
			if( text == null )
				text = "";
			
			this.text = text;
			this.lines = text.split( "\n" );
			this.widths = new int[lines.length];
			for( int i = 0; i < lines.length; i++ )
				widths[i] = getStringWidth( lines[i] );
			rebuild( );					
		}
		
		public void setPosition( float x, float y )
		{
			posX = x;
			posY = y;
			rebuild( );
		}
		
		private void rebuild( )
		{					
			if( mesh == null )			
				mesh = new Mesh( gl, 6 * text.length(), false, true, false );
			
			if( mesh.getMaximumVertices() / 6 < text.length() )
			{				
				mesh = new Mesh( gl, 6 * text.length(), false, true, false );
			}
						
			mesh.reset();
			int lineHeight = getLineHeight();
			for( int i = 0; i < lines.length; i++ )
			{
				String line = lines[i];
				int x = 0;
				int y = height;
				
				if( hAlign == HorizontalAlign.Left )
					x = 0;
				if( hAlign == HorizontalAlign.Center )
					x = width / 2 - widths[i] / 2;
				if( hAlign == HorizontalAlign.Right )
					x = width - widths[i];
				
				if( vAlign == VerticalAlign.Top )
					y = height;
				if( vAlign == VerticalAlign.Center )
					y = height / 2 + lines.length * (getLineHeight() + getLineGap()) / 2;				
				if( vAlign == VerticalAlign.Bottom )
					y = lines.length * (getLineHeight() + getLineGap());
				
				y -= i * (getLineHeight() + getLineGap());
				
				for( int j = 0; j < line.length(); j++ )
				{
					Glyph glyph = getGlyph( line.charAt(j) );
					mesh.texCoord( glyph.u, glyph.v );
					mesh.vertex( posX + x, posY + y, 0 );
					mesh.texCoord( glyph.u + glyph.uWidth, glyph.v );
					mesh.vertex( posX + x + glyph.width, posY + y, 0 );
					mesh.texCoord( glyph.u + glyph.uWidth, glyph.v + glyph.vHeight );
					mesh.vertex( posX + x + glyph.width, posY + y - lineHeight, 0 );
					mesh.texCoord( glyph.u + glyph.uWidth, glyph.v + glyph.vHeight );
					mesh.vertex( posX + x + glyph.width, posY + y - lineHeight, 0 );
					mesh.texCoord( glyph.u, glyph.v + glyph.vHeight );
					mesh.vertex( posX + x, posY + y - lineHeight, 0 );
					mesh.texCoord( glyph.u, glyph.v );
					mesh.vertex( posX + x, y, 0 );
					x += glyph.advance;
				}
			}
		}
		
		public void render( )
		{
			if( mesh == null )
				return;
						
			texture.bind();
			mesh.render(PrimitiveType.Triangles);
		}		
		
		public void dispose( )
		{
			if( mesh != null )
				mesh.dispose();
		}
	}
	
	/**
	 * Horizontal text alignement
	 * @author mzechner
	 *
	 */
	public enum HorizontalAlign
	{
		Left,
		Center, 
		Right
	}
	
	/**
	 * Vertical text alignement
	 * @author mzechner	 
	 */
	public enum VerticalAlign
	{
		Top,
		Center,
		Bottom
	}

	public void dispose() {
		texture.dispose();		
	}
}
