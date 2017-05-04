package gdt.data.entity;
/*
 * Copyright 2016 Alexander Imas
 * This file is part of JEntigrator.

    JEntigrator is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JEntigrator is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JEntigrator.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import gdt.data.grain.Core;
import gdt.data.grain.Locator;
import gdt.data.grain.Sack;
import gdt.data.store.Entigrator;
import gdt.data.store.FileExpert;

import gdt.jgui.console.JMainConsole;
import gdt.jgui.entity.JEntityPrimaryMenu;
/**
* This class contains methods to process archive files
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class ArchiveHandler {
	/**
	* type of archive
	*/
	public static final String ARCHIVE_TYPE="archive type";
	/**
	* Tag of 'archiveFile' method  
	*/
	public static final String ARCHIVE_FILE="archiveFile";
	/**
	* Archive type 'tar'  
	*/
	public static final String ARCHIVE_TYPE_TAR="archive type tar";
	/**
	* Archive type 'tgz'  
	*/
	public static final String ARCHIVE_TYPE_TGZ="archive type tgz";
	/**
	* Archive type 'zip'  
	*/
	public static final String ARCHIVE_TYPE_ZIP="archive type zip";
	/**
	* This field contains the type of the archive file.  
	*/
	public static final String ARCHIVE_CONTENT="archive content";
	/**
	* Archive content is a whole database  
	*/
	public static final String ARCHIVE_CONTENT_DATABASE="archive content database";
	/**
	* Archive content is a setof entities  
	*/
	public static final String ARCHIVE_CONTENT_ENTITIES="archive content entities";

	/**
	* Tag of 'compressEntitiesToTar' method  
	*/
	public static final String ARCHIVE_METHOD_COMPRESS_ENTITIES_TO_TAR="compressEntitiesToTar";
	/**
	* The name of entities directory within the database file system   
	*/
	//public static final String ENTITIES_DIR = "_xXsnV5R_SGxkuH_SpLJDeXCglybws/data/";
	
	private Logger LOGGER=Logger.getLogger(getClass().getName());
	private String method$;
	private String archiveType$;
	private String archiveFile$;
	static boolean debug=false;
	 /**
	 * Build the set of arguments needed to create an instance of ArchiveHandler 
	 * and pack them in the special string parameter - locator. 
	 *  
	 * @return The locator string.
	 */	 
	public  String getLocator(){
		Properties locator=new Properties();
		locator.setProperty(Locator.LOCATOR_TITLE, "Archive handler");
		locator.setProperty(Locator.LOCATOR_TYPE, ARCHIVE_TYPE);
		locator.setProperty(BaseHandler.HANDLER_CLASS,getClass().getName());
		if(method$!=null)
			locator.setProperty(BaseHandler.HANDLER_METHOD,method$);
		if(archiveType$!=null)
			locator.setProperty(ARCHIVE_TYPE,archiveType$);
		if(archiveFile$!=null)
			locator.setProperty(ARCHIVE_FILE,archiveFile$);
		return Locator.toString(locator);
	}
	private void getTarEntries(TarArchiveEntry tarEntry, Stack<TarArchiveEntry> s, String root$) {
        if (tarEntry == null)
            return;
        if (tarEntry.isDirectory()) {
            try {
                TarArchiveEntry[] tea = tarEntry.getDirectoryEntries();
                if (tea != null) {
                    for (TarArchiveEntry aTea : tea) {
                        getTarEntries(aTea, s, root$);
                    }
                }

            } catch (Exception e) {
            	LOGGER.severe(":getTarEntities:"+e.toString());
            }
        } else {
            String entryName$;
            entryName$ = tarEntry.getName().substring(root$.length());
            tarEntry.setName(entryName$);
            s.push(tarEntry);
        }
    }
	
	private boolean append(Entigrator entigrator,String root$, String source$, TarArchiveOutputStream aos) {
	        try {

	            File[] fa = null;
	            File source = new File(source$);
	            if (source.exists())
	                if (source.isFile())
	                    fa = new File[]{source};
	                else
	                    fa = source.listFiles();
	            if (fa == null)
	                return true;
	            File recordFile = null;

	            Stack<TarArchiveEntry> s = new Stack<TarArchiveEntry>();
	            int cnt = 0;

	            TarArchiveEntry entry = null;
	            for (File aFa : fa) {
	                recordFile = aFa;
	                entry = new TarArchiveEntry(recordFile);
	                entry.setSize(recordFile.length());
	                s.clear();
	                getTarEntries(entry, s, root$);
	                cnt = s.size();
	  
	                File nextFile = null;
	                for (int j = 0; j < cnt; j++) {
	                    entry = (TarArchiveEntry) s.pop();
	                    try {
	                        String nextFile$ = entigrator.getEntihome() + "/" + entry.getName();
	  	                        nextFile = new File(nextFile$);
	                        if (!nextFile.exists() || nextFile.length() < 1) {
	                            if(debug)
	                        	System.out.println("ArchiveHandler:append:wrong next file=" + nextFile$);
	                            continue;
	                        }
	                        aos.putArchiveEntry(entry);
	                        IOUtils.copy(new FileInputStream(nextFile$), aos);
	                        // System.out.println("EximpExpert:tar_write:j="+j);
	                        aos.closeArchiveEntry();
	                    } catch (Exception ee) {
	                     //   System.out.println("EximpExpert:append:" + ee.toString());
	                    	LOGGER.severe(":append:"+ee.toString());
	                    }
	                }
	            }
	//System.out.println("EximpExpert:tar_write:finish");
	            return true;

	//System.out.println("EximpExpert:tar_write:exit");
	        } catch (Exception e) {
	        	LOGGER.severe(":append:"+e.toString());
	            return false;
	        }
	    }
	private static void compressGzipFile(String tarFile$, String gzipFile$) {
        try {
            FileInputStream fis = new FileInputStream(tarFile$);
            FileOutputStream fos = new FileOutputStream(gzipFile$);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (Exception e) {
            Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        }
         
    }
	 /**
     * Discover the type of the archive file.
     * @param fname$ the name of the file.
     * 
     * @return ARCHIVE_TYPE_TAR for *.tar file, 
     * ARCHIVE_TYPE_TGZ for *.tar.gz file
     * ARCHIVE_TYPE_ZIP for *.zip file
     * and null otherwise.
     */
	
	public static String detectTypeOfArchive(String fname$){
		try{
			String ext$=FileExpert.getExtension(fname$);
			if("tar".equalsIgnoreCase(ext$))
				return ARCHIVE_TYPE_TAR;
			if("tgz".equalsIgnoreCase(ext$))
				return ARCHIVE_TYPE_TGZ;
			if("zip".equalsIgnoreCase(ext$))
				return ARCHIVE_TYPE_ZIP;
			
		}catch (Exception e) {
            Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        }
		return null;
	}
	 /**
     * Discover the type of content within the archive file.
     * @param file$ the path of the archive.
     * 
     * @return ARCHIVE_CONTENT_DATABASE if the file contains a database, 
     * ARCHIVE_CONTENT_ENTITIES if the file contains a set of entities
     * and null otherwise.
     */
	public static String detectContentOfArchive(String file$){
		try{
			String ext$=FileExpert.getExtension(file$);
			if("tar".equalsIgnoreCase(ext$)){
				if(hasEntitiesDirInTar(file$)){
					if(hasPropertyIndexInTar(file$))
						return ARCHIVE_CONTENT_DATABASE;
					else
						return ARCHIVE_CONTENT_ENTITIES;
				}else
					return null;
			}
			if("tgz".equalsIgnoreCase(ext$)){
				if(hasEntitiesDirInTgz(file$)){
					if(hasPropertyIndexInTgz(file$))
						return ARCHIVE_CONTENT_DATABASE;
					else
						return ARCHIVE_CONTENT_ENTITIES;
				}else
					return null;
			}
			if("zip".equalsIgnoreCase(ext$)){
				if(hasEntitiesDirInZip(file$)){
					if(hasPropertyIndexInZip(file$))
						return ARCHIVE_CONTENT_DATABASE;
					else
						return ARCHIVE_CONTENT_ENTITIES;
				}else
					return null;
			}
			
		}catch (Exception e) {
            Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        }
		return null;
	}
	/**
	 * Compress the database into the tar archive file. 
	 * @param  entigrator entigrator instance,
	 * @param locator$ container of arguments 
	 * in the string form. 
	 * @return true if success false otherwise.
	 */
	 public boolean compressDatabaseToTar(Entigrator entigrator,String locator$){
			try{
				if(debug)
				System.out.println("ArchiveHandler:compressDatabaseToTar:locator="+locator$);
				Properties locator=Locator.toProperties(locator$);
				archiveType$=locator.getProperty(ARCHIVE_TYPE);
				archiveFile$=locator.getProperty(ARCHIVE_FILE);
		        String tarfile$ =archiveFile$; 
	            File tarfile = new File(tarfile$);
	            if (!tarfile.exists())
	                tarfile.createNewFile();
	            TarArchiveOutputStream aos = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream("tar", new FileOutputStream(tarfile$));
	            aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
	            String entihome$=entigrator.getEntihome();
	            append(entigrator,entihome$, entihome$, aos);
	            aos.close();
	            return true;
			}catch(Exception e){
				 Logger.getLogger(getClass().getName()).severe(e.toString());
				 return false;
			}
	 }
	 /**
		 * Compress the database into the tgz archive file. 
		 * @param  entigrator entigrator instance
		 * @param locator$ container of arguments in the string form. 
		 * @return true if success false otherwise.
		 */
	 public boolean compressDatabaseToTgz(Entigrator entigrator,String locator$){
			try{
				Properties locator=Locator.toProperties(locator$);
				archiveType$=locator.getProperty(ARCHIVE_TYPE);
				archiveFile$=locator.getProperty(ARCHIVE_FILE);
				 String tgzFile$ =archiveFile$; 
		            File tgzFile = new File(tgzFile$);
		            if (!tgzFile.exists())
		                tgzFile.createNewFile();
		           // String userHome$=System.getProperty("user.home");
		            File tarFile=new File (tgzFile$.replace(".tgz", "")+".tar");
		            if (!tarFile.exists())
		                tarFile.createNewFile();
				TarArchiveOutputStream aos = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream("tar", new FileOutputStream(tarFile));
	            aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
	            String entihome$=entigrator.getEntihome();
	            append(entigrator,entihome$, entihome$, aos);
	            aos.close();
	            compressGzipFile(tarFile.getPath(),tgzFile.getPath());
	            tarFile.delete();
	            return true;
			}catch(Exception e){
				 Logger.getLogger(getClass().getName()).severe(e.toString());
				 return false;
			}
	 }
	 /**
		 * Compress the entities into the tar archive file. 
		 * @param  entigrator entigrator instance
		 * @param locator$ container of arguments in the string form. 
		 * @return true if success false otherwise.
		 */	 
	public boolean compressEntitiesToTar(Entigrator entigrator,String locator$){
		try{
			Properties locator=Locator.toProperties(locator$);
			archiveType$=locator.getProperty(ARCHIVE_TYPE);
			archiveFile$=locator.getProperty(ARCHIVE_FILE);
	        String entityList$=locator.getProperty(EntityHandler.ENTITY_LIST);
	        String[] sa=Locator.toArray(entityList$);
	        if(debug)
	         System.out.println("ArchiveHandler:compressEntitiesToTar:sa="+sa.length);
	        String tarfile$ =archiveFile$; 
            File tarfile = new File(tarfile$);
            if (!tarfile.exists())
                tarfile.createNewFile();
            String entityBody$ = null;
            String entityHome$ = null;
            TarArchiveOutputStream aos = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream("tar", new FileOutputStream(tarfile$));
            aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            String entihome$=entigrator.getEntihome();
            String entitiesHome$ = entihome$ + "/" + Entigrator.ENTITY_BASE + "/data/";
        
            String iconsHome$=entihome$ + "/"+Entigrator.ICONS+"/";
            String icon$;
            for (String aSa: sa) {
                entityBody$ = entitiesHome$ + aSa;
                append(entigrator,entigrator.getEntihome(), entityBody$, aos);
                entityHome$ = entigrator.ent_getHome(aSa);
                if (new File(entityHome$).exists()) {
                    append(entigrator,entigrator.getEntihome(), entityHome$, aos);
                }
                icon$=entigrator.ent_getIconAtKey(aSa);
                if(icon$!=null)
                	append(entigrator,entigrator.getEntihome(), iconsHome$+icon$, aos);
            }
            aos.close();
			return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
			return false;
		}
	}
	 /**
	 * Compress the entities into the tgz archive file. 
	 * @param  entigrator entigrator instance 
	 * @param locator$ container of arguments in the string form. 
	 * @return true if success false otherwise.
	 */	 
	public boolean compressEntitiesToTgz(Entigrator entigrator,String locator$){
		try{
			Properties locator=Locator.toProperties(locator$);
			archiveType$=locator.getProperty(ARCHIVE_TYPE);
			archiveFile$=locator.getProperty(ARCHIVE_FILE);
	        String entityList$=locator.getProperty(EntityHandler.ENTITY_LIST);
	        String[] sa=Locator.toArray(entityList$);
	        String tgzFile$ =archiveFile$; 
            File tgzFile = new File(tgzFile$);
            if (!tgzFile.exists())
                tgzFile.createNewFile();
            File tarFile=new File (tgzFile$.replace(".tgz", "")+".tar");
            if (!tarFile.exists())
                tarFile.createNewFile();
            String entityBody$ = null;
            String entityHome$ = null;
            TarArchiveOutputStream aos = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream("tar", new FileOutputStream(tarFile));
            aos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            String entihome$=entigrator.getEntihome();
            String entitiesHome$ = entihome$ + "/" + Entigrator.ENTITY_BASE + "/data/";
            String iconsHome$=entihome$ + "/"+Entigrator.ICONS+"/";
            String icon$;
            for (String aSa: sa) {
                entityBody$ = entitiesHome$ + aSa;
                append(entigrator,entigrator.getEntihome(), entityBody$, aos);
                entityHome$ = entigrator.ent_getHome(aSa);
                if (new File(entityHome$).exists()) 
                    append(entigrator,entigrator.getEntihome(), entityHome$, aos);
                icon$=entigrator.ent_getIconAtKey(aSa);
                if(icon$!=null)
                	append(entigrator,entigrator.getEntihome(), iconsHome$+icon$, aos);
       
            }
            aos.close();
            compressGzipFile(tarFile.getPath(),tgzFile.getPath());
            tarFile.delete();
			return true;
		}catch(Exception e){
			LOGGER.severe(e.toString());
			return false;
		}
	}
	//zip
	private static void getAllFiles(File dir, ArrayList<File> fileList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					if(debug)
					System.out.println("directory:" + file.getPath());
					getAllFiles(file, fileList);
				} else {
					fileList.add(file);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
		}
	}
private static void appendToZip(String directoryToZip$, File file, ZipOutputStream zos){
	try{
			FileInputStream fis = new FileInputStream(file);
			String zipFile$ = file.getPath().substring(directoryToZip$.length() + 1,file.getPath().length());
			ZipEntry zipEntry = new ZipEntry(zipFile$);
			zos.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
			fis.close();
}catch(Exception e){
	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
}
}
/**
 * Compress the database into the zip archive file. 
 * @param  entigrator entigrator instance
 * @param locator$ container of arguments in the string form. 
 * @return true if success false otherwise.
 */
public boolean compressDatabaseToZip(Entigrator entigrator,String locator$){
	try{
		Properties locator=Locator.toProperties(locator$);
		archiveType$=locator.getProperty(ARCHIVE_TYPE);
		archiveFile$=locator.getProperty(ARCHIVE_FILE);
        String zipFile$ =archiveFile$; 
        File zipFile = new File(zipFile$);
        if (!zipFile.exists())
            zipFile.createNewFile();
	FileOutputStream fos = new FileOutputStream(zipFile$);
	ZipOutputStream zos = new ZipOutputStream(fos);
	ArrayList<File>fl=new ArrayList<File>();
	String entihome$=entigrator.getEntihome();
	File entihome=new File(entihome$);
	getAllFiles(entihome,fl);
	for (File file : fl) {
		if (!file.isDirectory()) {
			appendToZip(entihome$, file, zos);
		}
	}
	zos.close();
	fos.close();
	return true;
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
		return false;
	}
}
/**
 * Compress the entities into the zip archive file. 
 * @param  entigrator entigrator instance
 * @param locator$ container of arguments in the string form. 
 * @return true if success false otherwise.
 */	 
public  boolean compressEntitiesToZip(Entigrator entigrator,String locator$){
		try{
			Properties locator=Locator.toProperties(locator$);
			archiveType$=locator.getProperty(ARCHIVE_TYPE);
			archiveFile$=locator.getProperty(ARCHIVE_FILE);
	        String entityList$=locator.getProperty(EntityHandler.ENTITY_LIST);
	        String[] sa=Locator.toArray(entityList$);
	        String zipFile$ =archiveFile$; 
            File zipFile = new File(zipFile$);
            if (!zipFile.exists())
                zipFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(zipFile$);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ArrayList<File>fl=new ArrayList<File>();
		String entihome$=entigrator.getEntihome();
		String entibase$=entihome$+"/"+Entigrator.ENTITY_BASE+"/data/";
		File entityBody;
		File entityHome;
		String iconsHome$=entihome$ + "/"+Entigrator.ICONS+"/";
        String icon$;
        File icon;
		for(String aSa:sa){
			entityBody=new File(entibase$+aSa);
			fl.add(entityBody);
			entityHome=new File(entihome$+"/"+aSa);
			if(entityHome.exists()&&entityHome.isDirectory())
				getAllFiles(entityHome,fl);
			//icon$=entigrator.indx_getIcon(aSa);
			icon$=entigrator.ent_getIconAtKey(aSa);
            if(icon$!=null){
            	icon=new File(iconsHome$+icon$);
            	if(icon.exists())
            		fl.add(icon);
            }
		}
		for (File file : fl) 
			if (!file.isDirectory()) 
				appendToZip(entihome$, file, zos);
    	zos.close();
		fos.close();
        return true;
	} catch (Exception e) {
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
		return false;
	}
}
/**
 * Discover if the tar file contains the default directory for entities entities
 * @param  tarfile$ the path of the archive file.
 *  @return true if contains, false otherwise.
 */
public static boolean hasEntitiesDirInTar(String tarfile$) {
	try{
		TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(new File(tarfile$)));
		return hasEntitiesDirInTarStream(tis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
	return false;
}
private static boolean hasEntitiesDirInTarStream(TarArchiveInputStream tis) {
    try {
        TarArchiveEntry entry = null;
        String entryName$;
        while ((entry = tis.getNextTarEntry()) != null) {
            entryName$ = entry.getName();
            if (entryName$.startsWith(Entigrator.ENTITY_BASE)) {
                tis.close();
                return true;
            }
        }
        tis.close();
        return false;
    } catch (Exception e) {
    	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        return false;
    }
}
/**
 * Discover if the tar.gz file contains the default directory for entities entities
 * @param  tgzfile$ the path of the archive file.
 *  @return true if contains, false otherwise.
 */
public static boolean hasEntitiesDirInTgz(String tgzfile$) {
    try {
    	GZIPInputStream gis = new GZIPInputStream(new FileInputStream(new File(tgzfile$)));    	
        TarArchiveInputStream tis = new TarArchiveInputStream(gis);
        return hasEntitiesDirInTarStream(tis);
    } catch (Exception e) {
    	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
    }
    return false;
}
private static boolean hasPropertyIndexInTarStream(TarArchiveInputStream tis) {
    try {
        TarArchiveEntry entry = null;
        String entryName$;
        while ((entry = tis.getNextTarEntry()) != null) {
            entryName$ = entry.getName();
            if (entryName$.equals(Entigrator.PROPERTY_INDEX)) {
                tis.close();
                return true;
            }
        }
        tis.close();
        return false;
    } catch (Exception e) {
    	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        return false;
    }
}
/**
 * Discover if the tar file contains the property index
 * @param  tarfile$ the path of the archive file.
 *  @return true if contains, false otherwise.
 */
public static boolean hasPropertyIndexInTar(String tarfile$) {
	try{
		TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(new File(tarfile$)));
		return hasPropertyIndexInTarStream(tis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
	return false;
}
/**
 * Discover if the tar.gz file contains the property index
 * @param  tgzfile$ archive file.
 *  @return true if contains, false otherwise.
 */
public static boolean hasPropertyIndexInTgz(String tgzfile$) {
    try {
    	GZIPInputStream gis = new GZIPInputStream(new FileInputStream(new File(tgzfile$)));    	
        TarArchiveInputStream tis = new TarArchiveInputStream(gis);
        return hasPropertyIndexInTarStream(tis);
    } catch (Exception e) {
    	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
    }
    return false;
}
private static boolean hasPropertyIndexInZipStream(ZipInputStream zis) {
    try {
    	 ZipEntry entry = null;
    	 String entryName$;
         while ( (entry = zis.getNextEntry()) != null ) 
         {
        	 entryName$ = entry.getName();
             if (entryName$.equals(Entigrator.PROPERTY_INDEX)) {
                 zis.close();
                 return true;
             }
         }
        zis.close();
        return false;
    } catch (Exception e) {
    	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        return false;
    }
}
/**
 * Discover if the zip file contains the property index
 * @param  zipFile$ the path of the archive file.
 *  @return true if contains, false otherwise.
 */
public static boolean hasPropertyIndexInZip(String zipFile$) {
	try{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(zipFile$)));
		return hasPropertyIndexInZipStream(zis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
	return false;
}	
private static boolean hasEntitiesDirInZipStream(ZipInputStream zis) {
    try {
    	ZipEntry entry = null;
        String entryName$;
        while ((entry = zis.getNextEntry()) != null) {
            entryName$ = entry.getName();
            if (entryName$.startsWith(Entigrator.ENTITY_BASE)) {
                zis.close();
                return true;
            }
        }
        zis.close();
        return false;
    } catch (Exception e) {
    	Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
        return false;
    }
}
/**
 * Discover if the zip file contains the default directory for entities entities
 * @param  zipFile$ archive file.
 *  @return true if contains, false otherwise.
 */
public static boolean hasEntitiesDirInZip(String zipFile$) {
	try{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(zipFile$)));
		return hasEntitiesDirInZipStream(zis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
	return false;
}
/**
 * Extract tar archive stream into the target directory
 * @param targetDirectory$ the path of the target directory
 * @param tis the tar archive input stream.
 */
public static void extractEntitiesFromTar(String targetDirectory$,TarArchiveInputStream tis){
	try{
		 
		 TarArchiveEntry entry = null;
         File outputFile;
         File outputDir;
         FileOutputStream outputStream;
		 while ((entry = tis.getNextTarEntry()) != null) {
			
			 outputFile = new File(targetDirectory$ + "/" + entry.getName());
             outputDir = outputFile.getParentFile();
             if (!outputDir.exists())
                 outputDir.mkdirs();
             if(entry.isDirectory()){
            	 outputFile.mkdir();
            	 
             }else{
             outputStream = new FileOutputStream(outputFile);
             IOUtils.copy(tis, outputStream);
             outputStream.close();
             }
		 }
		 tis.close();
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
/**
 * Extract zip archive stream into the target directory
 * @param targetDirectory$ the path of the target directory
 * @param zis the zip archive input stream.
 */
public static void extractEntitiesFromZip(String targetDirectory$,ZipInputStream zis){
	try{
		 ZipEntry entry = null;
         File outputFile;
         File outputDir;
         FileOutputStream outputStream;
		 while ((entry = zis.getNextEntry()) != null) {
			 outputFile = new File(targetDirectory$ + "/" + entry.getName());
             outputDir = outputFile.getParentFile();
             if (!outputDir.exists())
                 outputDir.mkdirs();
             if(entry.isDirectory()){
            	 outputFile.mkdir();
             }else{
             outputStream = new FileOutputStream(outputFile);
             IOUtils.copy(zis, outputStream);
             outputStream.close();
             }
		 }
		 zis.close();
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
private static void extractEntitiesFromTar(String targetDirectory$,String tarfile$){
	try{
		TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(new File(tarfile$)));
		extractEntitiesFromTar(targetDirectory$,tis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
private static void extractEntitiesFromTgz(String targetDirectory$,String tgzfile$){
	try{
		 GZIPInputStream gis = new GZIPInputStream(new FileInputStream(new File(tgzfile$)));
		 TarArchiveInputStream tis = new TarArchiveInputStream(gis);
		 extractEntitiesFromTar(targetDirectory$,tis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
private static void extractEntitiesFromZip(String targetDirectory$,String zipfile$){
	try{
		 ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(zipfile$)));
		 extractEntitiesFromZip(targetDirectory$,zis);
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
/**
 * Extract archive file into the target directory
 * @param targetDirectory$ the path of the target directory
 * @param file$ the archive file path
 * 
 * 
 */
public static void extractEntities(String targetDirectory$,String file$){
	try{
		if(file$.endsWith(".tar")||file$.endsWith(".TAR"))
			 extractEntitiesFromTar(targetDirectory$,file$);
		if(file$.endsWith(".tgz")||file$.endsWith(".TGZ"))
			 extractEntitiesFromTgz(targetDirectory$,file$);
		if(file$.endsWith(".zip")||file$.endsWith(".ZIP"))
			 extractEntitiesFromZip(targetDirectory$,file$);

	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
private static Sack prepareUndo(Entigrator entigrator,String cache$){
	try{

		int cnt=0;
		String[] sa=entigrator.indx_listEntities("entity", "undo");
		if(sa!=null){
			String label$;
			int max=0;
			for(String s:sa){
				label$=entigrator.indx_getLabel(s);
				cnt=Integer.parseInt(label$.substring(5, label$.length()));
				if (cnt>max){
					max=cnt;
				}
			}
			cnt=max+1;
		}
		Sack undo=entigrator.ent_new("undo", "undo_"+String.valueOf(cnt));
		entigrator.ent_assignProperty(undo, "folder", undo.getProperty("label"));
		undo.createElement("entity");
		undo.createElement("content");
		undo.createElement("icon");
		File entityBodies=new File(cache$+"/"+Entigrator.ENTITY_BASE+"/data/");
		File[] efa=entityBodies.listFiles();
		String entityKey$;
		File entityHome;
		File[] entityContent;
		for(File f:efa){
			entityKey$=f.getName();
			undo.putElementItem("entity", new Core(null,entityKey$,null));
			entityHome=new File(cache$+"/"+entityKey$);
			if(entityHome.exists()){
				entityContent=entityHome.listFiles();
				if(entityContent!=null)
					for (File cf:entityContent){
						undo.putElementItem("content", new Core(null,cf.getName(),entityKey$));
					}
			}
		}
		File icons=new File(cache$+"/"+Entigrator.ICONS);
		if(icons.exists()){
			File[] ifa=icons.listFiles();
			if(ifa!=null)
			   for(File f:ifa)
				   undo.putElementItem("icon", new Core(null,f.getName(),null));
		}
		entigrator.save(undo);
		entigrator.ent_reindex(undo);
		return undo;
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
	return null;
}
private static void fillUndo(Entigrator entigrator,Sack undo){
	try{
		String [] sa=undo.elementList("entity");
		if(sa!=null){
		Sack entity;
		String undoHome$=entigrator.ent_getHome(undo.getKey());
		File entityBodies=null;
		File entityHome;
		File entityUndoHome;
		File icon;
		File undoIcon;
		String icons=entigrator.getEntihome()+"/"+Entigrator.ICONS;
		for(String s:sa){
		 entity=entigrator.getEntityAtKey(s);
		 	if(entity!=null&&entityBodies==null){
		   		  entityBodies=new File(undoHome$+"/"+Entigrator.ENTITY_BASE+"/data/");
		   		  if(!entityBodies.exists())
		   			  entityBodies.mkdirs();
		 		}
		 entity.saveXML(entityBodies.getPath()+"/"+s);
		 entityHome=new File(entigrator.ent_getHome(s));
		 if(entityHome.exists()){
			  entityUndoHome=new File(undoHome$+"/"+s);
			  entityUndoHome.mkdir();
			  FileExpert.copyAll(entityHome.getPath(), entityUndoHome.getPath());
		 }
		}
		sa=undo.elementList("icon");
		if(sa!=null){
			File undoIcons=new File(undoHome$+"/"+Entigrator.ICONS);
			undoIcons.mkdir();
			for(String s:sa){
				icon=new File(icons+"/"+s);
				if(icon.exists()){
					undoIcon=new File(undoIcons.getPath()+"/"+s);
					FileExpert.copyFile(icon, undoIcon);
				}
				}
		}
		}
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
}
/**
 * Insert entities from the cache directory into the database. 
 * @param  entigrator entigrator instance
 * @param cache$ directory path
 * @param keep boolean value. 
 * If keep is true then keep existing entities. 
 * If keep is false then replace existing entities.  
 *  @return array of keys of inserted entities.
 */
public static String[] insertCache(Entigrator entigrator,String cache$,boolean keep){
	try{
		File entityBodies=new File(cache$+"/"+Entigrator.ENTITY_BASE+"/data/");
		String[] sa=entityBodies.list();
		Sack entity;
		File cacheEntityHome;
		File entityHome;
		for(String s:sa){
			if(keep&&entigrator.indx_getLabel(s)!=null)
					continue;
			entity=Sack.parseXML(entigrator,entityBodies.getPath()+"/"+s);
			if(entity!=null)
				entigrator.save(entity);
			    entigrator.ent_reindex(entity);
			    cacheEntityHome=new File(cache$+"/"+s);
			    if(cacheEntityHome.exists()){
			    	entityHome=new File(entigrator.ent_getHome(s));
			    	if(!entityHome.exists())
			    		entityHome.mkdir();
			    	FileExpert.copyAll(cacheEntityHome.getPath(), entityHome.getPath());
			    }
		}
		File cacheIcons=new File(cache$+"/"+Entigrator.ICONS);
		if(cacheIcons.exists())
			if(!keep)
			FileExpert.copyAll(cacheIcons.getPath(),entigrator.getEntihome()+"/"+Entigrator.ICONS);
			else{
				
				String icons=entigrator.getEntihome()+"/"+Entigrator.ICONS;
				File[] fa=cacheIcons.listFiles();
				File icon;
				for(File f:fa){
					icon=new File(icons+"/"+f.getName());
					if(icon.exists())
						continue;
					icon.createNewFile();
					FileExpert.copyFile(f, icon);
				}
			}
		return sa;
	}catch(Exception e){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(e.toString());
	}
	return null;
}
/**
 * Insert entities from the archive file into the database. 
 * @param  console main console instance
 * @param entihome$ the root directory of the database
 * @param file$ the path of the archive file. 
 * @return the key of 'undo' entity.
 */
public static String insertEntities(JMainConsole console,String entihome$,String file$){
	try{
       if(!ARCHIVE_CONTENT_ENTITIES.equals(detectContentOfArchive(file$))){
    	  if(debug)
    	   System.out.println("ArchiveHandler:insertEntites:wrong archive="+file$);
    	   return null;
       }
    	   
		File cache=new File(System.getProperty("user.home")+"/.entigrator/cache");
        if(!cache.exists())
        	cache.mkdirs();
        FileExpert.clear(cache.getPath());
		ArchiveHandler.extractEntities(cache.getPath(), file$);
		Entigrator entigrator=console.getEntigrator(entihome$);
		Sack undo=ArchiveHandler.prepareUndo(entigrator, cache.getPath());
		String undoLocator$=EntityHandler.getEntityLocator(entigrator, undo);
		JEntityPrimaryMenu.reindexEntity(console, undoLocator$);
		ArchiveHandler.fillUndo(entigrator, undo);
		  int response = JOptionPane.showConfirmDialog(console.getContentPanel(), "Keep existing entities ?", "Confirm",
			        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		  if (response == JOptionPane.YES_OPTION) 
			 ArchiveHandler.insertCache(entigrator, cache.getPath(),true);
		  else 
		   ArchiveHandler.insertCache(entigrator, cache.getPath(),false);
		String[] sa=undo.elementList("entity");
		if(sa!=null){
			String entityLocator$;
			for(String s:sa){
				entityLocator$=EntityHandler.getEntityLocatorAtKey(entigrator, s);
				JEntityPrimaryMenu.reindexEntity(console, entityLocator$);
			}
		}
		return undo.getKey();
	}catch(Exception ee){
		Logger.getLogger(ArchiveHandler.class.getName()).severe(ee.toString());
		return null;
	}
}

}