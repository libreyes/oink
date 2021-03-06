/*******************************************************************************
 * OINK - Copyright (c) 2014 OpenEyes Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package uk.org.openeyes.oink.domain.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hl7.fhir.instance.formats.JsonComposer;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ParserBase.ResourceOrFeed;
import org.hl7.fhir.instance.model.Resource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResourceGsonAdapter implements JsonSerializer<Resource>,
		JsonDeserializer<Resource> {

	@Override
	public Resource deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		JsonParser parser = new JsonParser();
		String jsonString = json.toString();
		InputStream is = new ByteArrayInputStream(jsonString.getBytes());
		try {
			ResourceOrFeed resourceOrFeed = parser.parseGeneral(is);
			return resourceOrFeed.getResource();
		} catch (Exception e) {
			throw new JsonParseException("Invalid Resource structure: "
					+ e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public JsonElement serialize(Resource src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonComposer composer = new JsonComposer();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			composer.compose(os, src, false);
			String element = os.toString();
			
			// Bug - Fhir Java Implementation serializes empty strings as NULL
			//element = element.replaceAll("null", "\"\"");
			
			JsonObject ob = new JsonObject();
			com.google.gson.JsonParser gsonParser = new com.google.gson.JsonParser();
			return gsonParser.parse(element);
		} catch (Exception e) {
			return null;
		}
	}

}
