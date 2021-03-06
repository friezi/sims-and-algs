/**
 * 
 */
package de.zintel.gfx.texture;

/**
 * @author friedemann.zintel
 *
 */
public abstract class AFilteredTexture implements ITexture {

	private final ITexture texture;

	public AFilteredTexture(ITexture texture) {
		this.texture = texture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getWidth()
	 */
	@Override
	public int getWidth() {
		return texture.getWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.gfx.texture.ITexture#getHeight()
	 */
	@Override
	public int getHeight() {
		return texture.getHeight();
	}

	public ITexture getTexture() {
		return texture;
	}

}
