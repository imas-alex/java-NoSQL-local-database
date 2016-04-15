package gdt.data.grain;
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
/**
* The string representation of a core including information about containers.
* Used for logging only.
*/
public class Granule {
    String key;
    Core core;
    String element;
    public Granule(String key, String set, Core core) {
        this.key = key;
        this.core = core;
        this.element = set;
    }
    public String toString() {
        return (key + "," + element + "," + core.type + "," + core.name + "," + core.value);
    }
}
