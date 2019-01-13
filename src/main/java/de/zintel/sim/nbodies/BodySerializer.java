/**
 * 
 */
package de.zintel.sim.nbodies;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

import de.zintel.physics.Body;

/**
 * @author friedemann.zintel
 *
 */
public class BodySerializer extends BodyConsumer {

	private BufferedWriter writer;

	/**
	 * @throws IOException
	 * 
	 */
	public BodySerializer(final String filename) throws IOException {

		writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(Paths.get(filename),
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))));
	}

	@Override
	public void accept(Collection<Body> bodies) {
		try {

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream outstream = new ObjectOutputStream(baos);

			outstream.writeObject(bodies);
			outstream.close();

			writer.write(Base64.getEncoder().encodeToString(baos.toByteArray()));
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}
