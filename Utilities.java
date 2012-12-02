/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2004-2011 Robert Grimm
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package qimpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Implementation of utilities for language processors, focusing on
 * strings and I/O.
 *
 * @author Robert Grimm
 * @version $Revision: 1.48 $
 */
public final class Utilities {

  /** Hide constructor. */
  private Utilities() { /* Nothing to do. */ }

  /**
   * Determine whether the specified name is qualified.
   *
   * @param name The name.
   * @return <code>true</code> if the name is qualified.
   */
  public static boolean isQualified(String name) {
    final int length = name.length();
    boolean   opaque = false;

    for (int i=0; i<length; i++) {
      char c = name.charAt(i);

      if (opaque) {
        if (Constants.END_OPAQUE == c) opaque = false;
      } else {
        if (Constants.QUALIFIER.equals(c)) return true;
        if (Constants.START_OPAQUE == c) opaque = true;
      }
    }

    return false;
  }

  /**
   * Extract the qualifying part from the specified qualified name.
   * For example, the qualifying part for <code>xtc.parser.Rats</code>
   * is <code>xtc.parser</code>.
   *
   * @param qname The qualified name.
   * @return The qualifying part, or <code>null</code> if the
   *   specified name has no qualifying part.
   */
  public static String getQualifier(String qname) {
    boolean opaque = false;

    for (int i=qname.length()-1; i>=0; i--) {
      char c = qname.charAt(i);

      if (opaque) {
        if (Constants.START_OPAQUE == c) opaque = false;
      } else {
        if (Constants.QUALIFIER.equals(c)) return qname.substring(0, i);
        if (Constants.END_OPAQUE == c) opaque = true;
      }
    }

    return null;
  }

  /**
   * Extract the unqualified name from the specified qualified name.
   * For example, the unqualified name for
   * <code>xtc.parser.Rats</code> is <code>Rats</code>.
   *
   * @param qName The qualified name.
   * @return The unqualified name.
   */
  public static String getName(String qName) {
    for (int i = qName.length() - 2; i >= 0; i--) {
      String s = qName.substring(i, i + 2);
      if (Constants.QUALIFIER.equals(s)) return qName.substring(i + 2);
    }

    return qName;
  }

  /**
   * Construct an unqualified name.  If the specified name is
   * qualified, this method returns the unqualified name.  Otherwise,
   * it returns the specified name.
   *
   * @param name The name.
   * @return The unqualified name.
   */
  public static String unqualify(String name) {
    return isQualified(name)? getName(name) : name;
  }

  /**
   * Construct a qualified name.  If the specified qualifier is
   * <code>null</code>, this method returns the unqualified name
   *
   * @param qualifier The qualifier.
   * @param name The unqualified name.
   * @return The corresponding qualified name.
   */
  public static String qualify(String qualifier, String name) {
    return null == qualifier ? name : qualifier + Constants.QUALIFIER + name;
  }

  /**
   * Resolve an unqualified class name based on namespace. Main Java objects receive
   * special treatment. Can optionally make the name for internal use.
   *
   * @param name The class
   *
   */
  public static String resolve(String name, boolean internal) {
    String toResolve = name;
    if (internal) {
      toResolve =  "__" + toResolve;  
    }
    if (name.equals("Object")
      || name.equals("String")
      || name.equals("Class")) { toResolve = Constants.QUALIFIER 
                                           + "java" + Constants.QUALIFIER
                                           + "lang" + Constants.QUALIFIER
                                           + toResolve; }
    return (toResolve.startsWith(Constants.QUALIFIER)) ? toResolve :
      Constants.QUALIFIER + toResolve;
  }

  /**
   * Convert the specified qualified name to its components.
   *
   * @param qname The qualified name.
   * @return The corresponding identifier.
   */
  public static String[] toComponents(String qName) {
    return qName.split(Constants.QUALIFIER);
  }

  /**
   * Replace all occurrences of the qualifier character with the
   * specified character.
   *
   * @param s The string.
   * @param qual The new qualifier.
   * @return The requalified string.
   */
  private static String requalify(String s, char qual) {
    final int     length = s.length();
    StringBuilder buf    = new StringBuilder(length);
    boolean       opaque = false;

    for (int i=0; i<length; i++) {
      char c = s.charAt(i);

      if (opaque) {
        buf.append(c);
        if (Constants.END_OPAQUE == c) opaque = false;

      } else {
        if (Constants.QUALIFIER.equals(c)) {
          buf.append(qual);
        } else {
          buf.append(c);
          if (Constants.START_OPAQUE == c) opaque = true;
        }
      }
    }

    return buf.toString();
  }

  /**
   * Convert the specified qualified name into a programming language
   * identifier.
   *
   * @param qname The qualified name.
   * @return The corresponding identifier.
   */
  public static String toIdentifier(String qname) {
    return requalify(qname, '$');
  }

  /**
   * Convert the specified qualified name into a file path.
   *
   * @param qname The qualified name.
   * @return The corresponding file path.
   */
  public static String toPath(String qname) {
    return requalify(qname, File.separatorChar);
  }

  /**
   * Convert the specified qualified name into a file path.
   *
   * @param qname The qualified name.
   * @param ext The file's extension.
   * @return The corresponding file path.
   */
  public static String toPath(String qname, String ext) {
    return toPath(qname) + "::" + ext;
  }

  // =======================================================================

  /**
   * Locate the specified file.  This method searches for the
   * specified file, relative to each root in the list of
   * <code>java.io.File</code> objects.
   *
   * @param roots The list of file system roots to search.
   * @param path The (relative) file path.
   * @return The corresponding file.
   * @throws FileNotFoundException
   *   Signals that the specified file could not be found.
   */
  public static File locate(List<File> roots, String path)
    throws FileNotFoundException {

    for (File root : roots) {
      File file = new File(root, path);
      if (file.exists() && file.isFile()) {
        return file;
      }
    }

    throw new FileNotFoundException(path + " not found");
  }

  // =======================================================================

  /**
   * Convert the specified list to a human-readable representation.
   * This method uses <code>toString()</code> for each element in the
   * specified list to generate a human-readable representation.
   *
   * @param l The list.
   * @return The human-readable representation.
   */
  public static String format(List<?> l) {
    final int     length = l.size();
    StringBuilder buf    = new StringBuilder();
    Iterator      iter   = l.iterator();
    while (iter.hasNext()) {
      String     el      = iter.next().toString();

      if ((1 < length) && (! iter.hasNext())) {
        buf.append("and ");
      }
      buf.append(el);
      if ((2 == length) && (iter.hasNext())) {
        buf.append(' ');
      } else if (iter.hasNext()) {
        buf.append(", ");
      }
    }

    return buf.toString();
  }

  // =======================================================================

  /**
   * Split the specified identifier.  This method splits identifiers
   * using an upper case character for each word component into a
   * string of lower case words separated by the specified separator.
   *
   * @param id The identifier.
   * @param separator The separator.
   * @return The split identifier.
   */
  public static String split(String id, char separator) {
    // Drop any suffixes.
    int idx = id.indexOf('$');
    if (-1 != idx) {
      id = id.substring(0, idx);
    }

    // Count the number of upper case characters.
    final int length      = id.length();
    boolean   startsUpper = false;
    int       upperCount  = 0;

    for (int i=0; i<length; i++) {
      if (Character.isUpperCase(id.charAt(i))) {
        if (0 == i) startsUpper = true;
        upperCount++;
      }
    }

    // No conversion is necessary if all characters are either lower
    // or upper case.
    if ((0 == upperCount) || (length == upperCount)) {
      return id;
    }

    // Do the actual conversion.
    final int     size = startsUpper ? length+upperCount-1 : length+upperCount;
    StringBuilder buf  = new StringBuilder(size);

    for (int i=0; i<length; i++) {
      char c = id.charAt(i);

      if (Character.isUpperCase(c)) {
        if (0 != i) {
          buf.append(separator);
        }
        buf.append(Character.toLowerCase(c));
      } else {
        buf.append(c);
      }
    }

    return buf.toString();
  }

  /**
   * Get the appropriate indefinite article for the specified noun.
   *
   * @param noun The noun.
   * @return The corresponding indefinite article.
   */
  public static String toArticle(String noun) {
    if (noun.startsWith("a") ||
        noun.startsWith("e") ||
        noun.startsWith("i") ||
        noun.startsWith("o") ||
        noun.startsWith("u")) {
      return "an";
    } else {
      return "a";
    }
  }

}
