package org.haion.tools.databuilders.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	public static final void zipDirectory(File directory, File zip) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		zip(directory, directory, zos);
		zos.close();
	}

	private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				FileInputStream in = new FileInputStream(files[i]);
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
				zos.putNextEntry(entry);
				while (-1 != (read = in.read(buffer))) {
					zos.write(buffer, 0, read);
				}
				in.close();
			}
		}
	}

	public static final void unzip(File zipFile, File extractTo) throws IOException {
		ZipFile archive = new ZipFile(zipFile);
		Enumeration e = archive.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			File file = new File(extractTo, entry.getName());
			
			if (entry.isDirectory() && !file.exists())
				file.mkdirs();
			else {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();

				InputStream in = archive.getInputStream(entry);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

				CommonUtils.bufferedCopy(in, out);

				in.close();
				out.close();
			}
		}
	}

	public static final void unzipEntry(File zipFile, String filter, OutputStream outputStream) throws IOException {
		ZipFile archive = new ZipFile(zipFile);
		Enumeration e = archive.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			if (entry.isDirectory()) // search only for file
				continue;
			
			String entryName = entry.getName();
			if (!entryName.matches(filter))
				continue;
			
			InputStream in = archive.getInputStream(entry);
			BufferedOutputStream out = new BufferedOutputStream(outputStream);

			CommonUtils.bufferedCopy(in, out);

			in.close();
			out.close();

			return; 
		}
		throw new FileNotFoundException("There is no file in zip archive that match mask: " + filter);
	}

	public static final void unzipEntry(File zipFile, Map<String, OutputStream> filterStreamMap, IStringComparer comparer) throws IOException {
		ZipFile archive = new ZipFile(zipFile);
		Enumeration e = archive.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			if (entry.isDirectory()) // search only for file
				continue;
			
			String entryName = entry.getName();
			String filter = null;
			OutputStream outputStream = null;
			for (Entry<String, OutputStream> filterStream : filterStreamMap.entrySet()) {
				if (comparer.compare(entryName, filterStream.getKey())) {
					filter = filterStream.getKey();
					outputStream = filterStream.getValue();
					break;
				}
			}
			if (filter == null)
				continue;
			
			filterStreamMap.remove(filter);
			
			InputStream in = archive.getInputStream(entry);
			BufferedOutputStream out = new BufferedOutputStream(outputStream);

			CommonUtils.bufferedCopy(in, out);

			out.close();
			in.close();

			if (filterStreamMap.size() == 0)
				break;
		}
		archive.close();
		if (filterStreamMap.size() != 0)
			throw new FileNotFoundException("There are no files in zip archive that match masks: " + filterStreamMap.keySet());
	}
}
