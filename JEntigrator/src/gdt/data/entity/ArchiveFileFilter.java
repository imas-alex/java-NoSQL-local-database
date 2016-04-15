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
import java.io.FilenameFilter;
/**
* This class detects archive files by extensions tar,tgz and zip.
* @author  Alexander Imas
* @version 1.0
* @since   2016-03-11
*/
public class ArchiveFileFilter implements FilenameFilter{
	 /**
     * accept archive file
     */
	@Override
	public boolean accept(File directory, String fileName) {
		  if (fileName.endsWith(".tar")
			||fileName.endsWith(".TAR")
			||fileName.endsWith(".tgz")
			||fileName.endsWith(".TGZ")
			||fileName.endsWith(".zip")
			||fileName.endsWith(".ZIP")
				  ) {
		              return true;
          }
	return false;
	}

}
