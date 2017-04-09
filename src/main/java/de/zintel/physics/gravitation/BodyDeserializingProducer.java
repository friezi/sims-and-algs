/**
 * 
 */
package de.zintel.physics.gravitation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.GZIPInputStream;

import de.zintel.gfx.g2d.Field;
import de.zintel.physics.Body;

/**
 * @author friedemann.zintel
 *
 */
public class BodyDeserializingProducer implements IBodyProducer {

	private final BufferedReader reader;

	private Collection<Body> bodies = Collections.emptyList();

	/**
	 * @throws IOException
	 * 
	 */
	public BodyDeserializingProducer(final String filename) throws IOException {
		reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(filename), StandardOpenOption.READ))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.physics.gravitation.IBodyProducer#calculate()
	 */
	@Override
	public void calculate() {

		try {
			final String line = reader.readLine();
			if (line == null) {
				return;
			}
			final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(line)));
			bodies = (Collection<Body>) ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.physics.gravitation.IBodyProducer#getBodies()
	 */
	@Override
	public Collection<Body> getBodies() {
		return bodies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.physics.gravitation.IBodyProducer#setField(de.zintel.gfx.g2d.
	 * Field)
	 */
	@Override
	public void setField(Field field) {

	}

	@Override
	public void shutdown() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
