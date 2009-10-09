/**
 * File creation: Aug 13, 2009 5:37:32 PM
 * 
 * Copyright (C) 2005-2009 AtKaaZ <atkaaz@users.sourceforge.net>
 * Copyright (C) 2005-2009 UnKn <unkn@users.sourceforge.net>
 * 
 * This file and its contents are part of DeMLinks.
 * 
 * DeMLinks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DeMLinks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DeMLinks. If not, see <http://www.gnu.org/licenses/>.
 */


package org.temporary.tests;


/**
 * 
 *
 */
public class XorIntercept {
	
	public static void main( String[] args ) {

		String hexa;
		hexa = "c54c0d1ab56da89dc8c7a2";
		String str = new String();
		// str += "c5";
		// System.out.println( Integer.parseInt( str, 16 ) );
		for ( int i = 0; i < hexa.length() - 1; i += 2 ) {
			System.out.println( hexa.substring( i, i + 2 ) );
			str += (char)Integer.parseInt( hexa.substring( i, i + 2 ), 16 );
		}
		System.out.println( str );
		
		int top = 0;
		String xor1 = new String();
		for ( int i = 0; i < 255; i++ ) {
			for ( int c = 0; c < str.length(); c++ ) {
				xor1 += (char)( str.charAt( c ) ^ (char)i );
			}
			// System.out.println( xor1 );
			int cur = countNorm( xor1 );
			if ( top <= cur ) {
				top = cur;
				System.out.println( xor1 );
			}
			xor1 = "";
		}
		System.out.println( "max:" + ( top * 100 ) / str.length() + "%" );
		
		top = 0;
		String xor2 = new String();
		for ( int i = 0; i < 65535; i++ ) {
			for ( int c = 0; c < str.length() - 1; c += 2 ) {
				byte part1 = (byte)str.charAt( c );
				byte part2 = (byte)str.charAt( c + 1 );
				int res = ( ( part1 * 256 + part2 ) ^ i );
				// xor1 += (byte)(res >>> 8);
				// xor1 += (byte)(res <<< 8);
				// (int)str.substring( c, c + 2 ) ^ i;
				xor2 += (char)( res / 256 );
				xor2 += (char)( res % 256 );
			}
			// System.out.println( xor1 );
			int cur = countNorm( xor2 );
			if ( top < cur ) {
				top = cur;
				System.out.println( xor2 );
			}
			xor2 = "";
		}
		System.out.println( "max:" + ( top * 100 ) / str.length() + "%" );
	}
	
	/**
	 * @param xor1
	 * @return
	 */
	private static int countNorm( String xor1 ) {

		int count = 0;
		for ( int i = 0; i < xor1.length(); i++ ) {
			char cur = xor1.charAt( i );
			if ( ( cur >= 32 ) && ( cur <= 126 ) ) {
				count++;
			}
		}
		return count;
	}
}
