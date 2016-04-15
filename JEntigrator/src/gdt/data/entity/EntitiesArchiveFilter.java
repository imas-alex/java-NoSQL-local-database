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

import javax.swing.filechooser.FileFilter;
/**
* This class detects archive that contains entities.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class EntitiesArchiveFilter extends FileFilter {
	@Override
	 /**
     * accept archive file
     */
	public boolean accept(File file) {
		String fileName=file.getName(); 
		if (fileName.endsWith(".tar")
					||fileName.endsWith(".TAR")){
					  if(!ArchiveHandler.hasEntitiesDirInTar(file.getPath()))
						  return false;
					  if(ArchiveHandler.hasPropertyIndexInTar(file.getPath()))
						  return false;
					  return true;
				  }
					if(fileName.endsWith(".tgz")
					||fileName.endsWith(".TGZ")){
						  if(!ArchiveHandler.hasEntitiesDirInTgz(file.getPath()))
							  return false;
						  if(ArchiveHandler.hasPropertyIndexInTgz(file.getPath()))
							  return false;
						  return true;
					}
					if(fileName.endsWith(".zip")
					||fileName.endsWith(".ZIP")
						  ) {
						  if(!ArchiveHandler.hasEntitiesDirInZip(file.getPath()))
							  return false;
						  if(ArchiveHandler.hasPropertyIndexInZip(file.getPath()))
							  return false;
						  return true;	
		          }
			return false;
	}

	@Override
	public String getDescription() {
		
		return "Entity archives"; 
	}

}
