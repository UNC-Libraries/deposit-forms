/**
 * Copyright 2010 The University of North Carolina at Chapel Hill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cdr.forms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;

public class DepositFile {
	
	private File file;
	private String filename;
	private String contentType;
	private long size;
	private boolean external;
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getExtension() {
		String extension = "";
		int index = filename.lastIndexOf('.');
		if (index > 0)
			extension = filename.substring(index);
		if (!Pattern.matches("^\\.[a-zA-Z0-9]+$", extension))
			extension = "";
		return extension;
	}
	
	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}
	
	public String getHexDigest(String algorithm) throws NoSuchAlgorithmException, IOException {
		
		return new String(Hex.encodeHex(this.getDigest(algorithm)));
		
	}

	private byte[] getDigest(String algorithm) throws NoSuchAlgorithmException, IOException {
		
		InputStream input = null;
		
		try {

			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.reset();

			input = new FileInputStream(file);
			
			byte[] buffer = new byte[1048576];
			int count;
	
			while ((count = input.read(buffer, 0, buffer.length)) != -1) {
				messageDigest.update(buffer, 0, count);
			}
	
			return messageDigest.digest();
			
		} finally {
			
			if (input != null) {
				input.close();
			}
			
		}

	}
	
}
