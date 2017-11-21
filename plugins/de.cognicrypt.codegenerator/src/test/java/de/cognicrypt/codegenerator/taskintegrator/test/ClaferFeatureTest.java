package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class ClaferFeatureTest {

	@Test
	public final void testPropertyAmount() {
		ArrayList<FeatureProperty> featureProperties = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			featureProperties.add(new FeatureProperty("featureProperty" + String.valueOf(i), "propertyType"));
		}
		ClaferFeature claferFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, "testFeature", "");
		claferFeature.setFeatureProperties(featureProperties);

		assertEquals(claferFeature.getfeatureProperties(), featureProperties);
	}
	
	public static boolean filesEqual(String expectedFilename, String actualFilename) throws IOException {
		Path expectedFilePath = Paths.get(expectedFilename);
		Path actualFilePath = Paths.get(actualFilename);

		byte[] expectedBytes = Files.readAllBytes(expectedFilePath);
		byte[] actualBytes = Files.readAllBytes(actualFilePath);
		
		return Arrays.equals(expectedBytes, actualBytes);
	}

}
