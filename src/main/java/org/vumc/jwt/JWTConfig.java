/* Project: continuum
 * File: JWTConfig.java
 * Created: Mar 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTConfig
{

  @Bean("jwtSecret")
  byte[] getHmacSecret()
  {
    return new byte[] {123, 44, -32, -81, 56, -17, 17, 2, 58, -48, -111, 28, 84, -53, -21, -102,
                       -93, 68, 45, 64, -121, 21, -66, 82, -114, 74, -67, -55, -41, 34, 64, -123,
                       -2, -8, 116, -74, 110, 29, 71, 40, 16, -106, 45, -9, -42, 52, 108, 21, -35,
                       -22, -104, 113, 12, -49, 113, -25, 109, 17, 36, -72, -23, 24, -111, 11, -80,
                       6, 58, -123, 93, -3, 8, 15, 106, -54, 23, 9, -77, -40, -10, 11, 1, 27, 50,
                       -119, -46, 123, -40, 124, 43, -59, -17, -15, -7, 70, -91, 27, -36, -80, 15,
                       -29, -67, 126, 6, -107, 27, 95, -44, -36, -107, -124, -34, 114, 22, -9, -75,
                       -49, -79, 104, 80, 43, 85, -114, -101, -95, -93, 119, 49, -90, 4, -93, 108,
                       78, -19, -119, -93, -87, -75, 62, 1, 1, -73, -28, -46, -25, 7, -78, -23, -42,
                       -57, 33, -42, -2, 64, -27, -30, 45, 114, 18, 103, 52, -13, 6, -97, -87, 45,
                       104, 48, 83, 113, 59, -1, 84, 107, -73, 17, -12, -43, -82, -5, 71, 45, 94,
                       67, -80, 107, -56, -60, 32, 99, -73, -29, -33, 27, 41, -63, -115, -52, 51,
                       113, 62, 30, 78, -29, -28, 28, -126, -22, -36, 108, 103, -19, 36, 70, 41,
                       110, 15, -60, -96, 105, -9, 118, 65, -4, -64, 40, -127, -92, 92, 83, 96, 10,
                       103, -9, -19, 2, 81, 80, 112, -123, -50, -44, -25, -94, 77, 113, -63, 69,
                       -123, -79, 5, -128, 44, 44, -35, 77, 113, 111, 26, -52, -33, 108, 121, -115,
                       87, -127, 36, 4, 28, -101, -24, 86, 114, 83, -119, -43, -81, 34, 99, -36, 71,
                       -66, 56, -92, 91, -116, 85, -6, 101, 115, -66, -102, -4, 74, -69, 111, 91,
                       64, 45, -90, -125, -43, -2, -73, 45, -66, -96, -69, -1, 78, 114, -15, -9,
                       114, 81, -14, -82, 82, -44, -59, -108, -55, -64, -95, 38, -115, -57, -97, 80,
                       -50, 53, -79, 74, 15, 39, 32, -40, -80, -101, -33, 74, 8, 89, -18, 6, 30, 66,
                       -94, 10, 74, 82, -119, 32, 27, -88, -105, 26, 43, 106, -119, -107, -85, -1,
                       -62, 116, -95, -71, 43, 50, 19, -83, 121, -88, -69, -45, 110, -72, 56, 67,
                       -4, 24, -96, -44, 65, 70, 37, -67, -48, -17, 29, 78, -33, 43, -111, 126, 77,
                       84, 117, -101, -120, 32, -27, 7, 44, 55, -12, -62, 13, 27, -102, -47, 48, 84,
                       118, 61, -39, -11, -20, -67, -86, -103, 125, -4, 93, -114, 94, 29, 107, -95,
                       60, 96, 65, 108, 16, -121, -126, 35, -46, 0, 39, -80, -69, -118, -57, 85, 66,
                       98, -60, 92, 2, 91, -121, -90, 103, -90, -56, -2, -126, 115, -2, -18, -19,
                       -92, 113, 104, 40, 111, -24, -128, 33, 119, 86, 115, -93, -63, -64, -43, 77,
                       -61, 115, 12, -37, 50, -27, -86, -101, -21, 35, -41, 20, 42, 71, 117, -96,
                       44, -33, 96, -96, -62, 125, -51, -78, -31, -7, -57, -17, -74, 45, 14, 60, 64,
                       -119, -6, -124, -63, -105, -114, 43, -31, -2, 57, 30, 95, 81, -87, -107, -10,
                       -74, -78, -33, -72, 120, 82, 28, 90, -89, -25, 47, -8, 86, -58, 56, -68, -74,
                       -86, -4, -33, -116, 93, -65, -85, -32, 25, 66, 5, 54, -52, 39, -73, 7, 89,
                       104, -6, -30, -91, 24, -87, 61, 26, -39, -99, 21, 65, 63, 18, -32, -122, -75,
                       -9, 24, 95, -106, -11, -127, -29, 94, 74, -20, 66, 68, -58, -16, 40, 87, 38,
                       94, 69, -75, 113, -119, -34, 126, 63, -57, -58, -124, 13, 117, -39, -62, 74,
                       112, 85, -32, -87, -30, 95, 64, 39, 5, -68, -9, -22, -7, -27, 124, 103, 36,
                       72, 62, 15, -115, -128, 110, -20, 4, 90, 85, 2, -14, -128, 79, 30, 61, 43,
                       -127, 67, 56, -14, 126, 110, 62, 109, 80, -65, -78, 31, 101, 92, -101, -51,
                       88, 104, 80, 127, -2, -114, -8, 14, 43, 41, 15, -38, 67, -23, 77, -2, -73,
                       117, -31, -81, 2, -114, 94, 42, 37, 80, 38, -119, 56, 7, -81, 13, -25, 57,
                       -83, -51, 84, -52, 69, 18, 112, 60, -101, -104, -111, -91, 66, -110, 88, -8,
                       -91, 39, 96, -75, 72, -102, -98, -70, 40, 123, -114, -18, 8, -84, -29, 114,
                       -110, -60, -12, -55, -43, -98, -118, -14, 99, 70, 61, -39, 6, -82, 92, 18,
                       -89, 116, 37, -30, -30, 21, -60, -128, -18, -127, 2, -125, -24, -6, 26, -112,
                       115, -127, 105, 97, 27, -18, -39, -43, 37, -50, 58, -6, 13, -115, 74, 14, 60,
                       111, -10, -86, -82, 105, 40, -29, 90, -86, -125, -31, -66, 9, 2, 100, -30,
                       -72, -25, -89, 1, 92, -12, 78, 88, -116, -14, -117, 59, -66, 35, 116, 60,
                       -19, -110, -94, 49, 13, 118, 7, 80, -127, -79, 74, 98, 59, 111, 99, 39, 28,
                       84, 111, 3, -65, -74, -82, -66, 121, -53, -71, -86, -7, 36, -17, -12, 111,
                       32, 74, -68, 3, 0, -57, -100, 32, -3, 64, -14, -119, -28, -108, 97, -31, 120,
                       84, -86, -45, -128, -14, 82, -18, 110, -54, -99, 108, -92, -44, -101, -125,
                       119, 127, 38, 5, 7, 109, 84, -30, -78, 12, 40, -63, -31, -2, 20, 9, 10, 49,
                       -46, -69, -57, -13, 16, 15, -111, 13, -82, -83, 114, 19, 25, 125, -25, 112,
                       29, -127, 60, -56, 88, 87, -41, 115, 3, -107, -120, 67, 61, 66, -76, 109, 63,
                       98, 106, -98, 1, -25, -68, -29, -58, -28, -91, -111, 111, -16, -81, 71, -67,
                       12, -101, -16, 33, -29, 20, 124, -67, -102, -55, 53, -120, -55, -9, -4, 59,
                       -83, 66, 125, 42, 25, 71, -39, -127, 106, 18, -69, -28, -75, 7, 118, -87,
                       -25, 16, 103, 14, -6, 13, 65, -57, 36, -74, 73, 99, -80, 122, 124, 39, -72,
                       -70, -104, -97, 83, -116, -71, 67, -53, -85, -7, 123, 102, -30, -16, -59, 47,
                       16, 1, 111, -48, -117, -64, -94, -113, 126, 89, -48, 25, 65, 12, -77, 52, 32,
                       50, 42, -103, 24, -44, 88, -26, -84, 59, -38, 53, -47, -125, 66, 48, -27,
                       103, 92, -106, -102, 21, -74, -120, -94, -55, -73, 5, -122, 90, 48, -45, -9,
                       58, -52, -6, -30, -95, 38, 95, -105, -70, -16, 85, -66, 17, 55, 11, 77, 80,
                       114, -93, -80, -98, 123, -63, 63, 15, 76, 18, -88, -24, 83, -66, 30, -35,
                       105, -57, 53, -34, 20, -36, -5, 86, 127, -29, 41, -53, -67, -85, 119, 41,
                       -60, 78, 45, -25, 48, -47, 18, -26, -106, 124, -98, 54, 13, -41, 17, 70,
                       -112, -1, 81, -113, -3, -88, 33, -106, -101, -48, -93, 44, -25, -52, -123,
                       99, 21, -113, -45, 33, 3, -5, 80, 109, -24, -10, -108, 115, -102, 81, -121,
                       37, -109, 26, -58, -50, -90, 52, 81, -58, -45, -65, 104, -47, 42, -39, 14,
                       105, 26, -122, -57, -45, 1, 101, 79, 103, 28, -127, 84, 58, -55, 91, 113, -9,
                       -15, -47, 78, -81, -74, 101, -68, -44, 19, -60, -111, 121, 58, 86, 27, -53,
                       107, 17, -24, 119, 97, 86, -9, 36, -111, -97, -85, -82, 1, 86, 75, -24, 124,
                       -80, -4, -80, 118, -123, 28, -103, 13, -19, -65, -87, 85, 15, -31, -121, 121,
                       -37, -75, 111, 79, 34, -31, 43, -78, 27, 40, 78, -23, -22, 13, -39, 40, 32,
                       64, -29, -62, 73, -45, 3, 90, 115, 111, 12, -37, 58, 6, 68, -114, 38, -21,
                       -67, 75, 2, 91, 80, 62, 8, 60, -95, -125, -32, 42, 23, 84, 113, 57, -112, 36,
                       -39, 92, -13, -60, 112, 123, 2, 113, 39, -30, 51, 103, 61, 81, -73, 4, -95,
                       -127, -16, -50, 56, -110, 28, 32, -13, -38, 26, 85, -43, 90, -87, -106, -77,
                       -66, -15, -42, 41, -33, -47, 48, -81, -78, 117, 84, -35, -75, -118, -78, 60,
                       -45, -101, 22, 14, -111, -108, -121, -127, -100, -42, -112, 35, 31, -66, 109,
                       38, -110, 6, -106, -44, -120, -9, 21, 32, -76, -16, -68, -16, 60, -121, -30,
                       -50, -36, -83, -21, -39, -101, -121, 113, 72, 74, -82, 65, 40, -123, 41,
                       -124, -2, -22, -123, 24, -8, 40, 127, 28, 74, -48, 19, 81, -61, 82, 2, 35,
                       48, 0, -65, 86, -3, -124, -17, -126, 17, -14, -18, -76, 85, 25, 15, -120,
                       -121, 117, 113, -110, 111, -90, 27, -118, -38, -115, 108, -82, -95, -123,
                       -105, 80, -69, -68, -45, -126, 35, -23, 98, 15, -16, 33, -55, 112, 81, 90,
                       27, -38, -125, 50, -121, 14, -40, -35, -83, -5, -87, 1, 19, -21, 31, -32,
                       -54, 89, 124, 101, -28, 49, 51, 47, -31, -89, -74, -115, 68, -17, -105, 52,
                       -3, -93, 115, -105, 119, 58, -5, 87, 63, 87, -34, 70, -60, -128, 65, 71, 61,
                       61, -30, -54, -76, -23, 89, 60, -11, -5, -127, 62, 52, 100, -25, 56, 74, 62,
                       -45, -28, 108, 18, 20, 104, 16, -72, 0, 29, -19, -29, 4, -4, 105, -33, -22,
                       -83, -66, -13, 113, -107, 88, 80, -16, -11, 71, -120, -17, -27, 86, 16, 8,
                       -19, -122, 38, -64, 87, -106, -41, -66, -76, 59, -17, -13, 109, -78, 12,
                       -100, -110, 81, -19, 22, 72, 63, -123, -53, 122, -87, -37, -119, -121, -61,
                       -37, -63, -85, -1, 120, 18, -122, 95, -81, 96, 119, 41, -37, -114, 76, -107,
                       42, -57, 80, -66, -95, -57, -45, 126, 54, 70, -95, 7, -85, 100, 124, 20, 34,
                       -80, -19, 29, -111, -9, -43, -118, -109, -124, -108, 49, -105, 90, 23, -82,
                       -94, 73, 59, -20, 0, 44, 37, -103, 31, -114, -102, -16, 53, -35, 47, -76, 82,
                       -47, 113, 39, 95, 81, 29, 81, 23, 3, -54, 86, -72, -20, -13, 108, 113, -102,
                       36, 27, 98, 58, 40, -99, 54, 116, 106, 45, 86, 73, 20, 118, -8, -77, 109,
                       -108, 91, 29, 61, 114, -24, -92, -58, -56, -75, 34, -124, -97, 36, -94, 88,
                       -126, 43, 13, 87, -110, -1, -33, -70, -33, -18, 77, -77, 89, -2, -29, 82, 97,
                       122, 16, -29, -110, -58, -7, 43, -93, -97, -89, 120, -108, 113, -87, -103,
                       114, -30, -57, -15, 99, 118, 120, 85, 9, 78, 122, 123, 89, -36, 125, 25, 125,
                       80, -48, 67, 103, -72, -63, 113, -57, 80, 124, 87, 88, -47, 92, 115, 107,
                       -84, 122, 42, 64, -22, 32, 20, 47, 120, -83, -53, 99, 46, -2, -11, -64, 31,
                       -102, -122, 14, 54, -77, -99, -46, 99, 59, 113, -126, -123, -9, -94, 74, 8,
                       10, 117, -81, 91, 48, 90, -28, 111, 27, -71, 17, 110, 124, 30, 47, 79, -71,
                       91, 4, -106, 113, -23, 35, 4, -123, 63, 43, -98, 120, 10, 1, 50, 40, -40, -6,
                       -57, 70, -3, 71, -81, -19, -17, -89, -99, 70, 92, 114, -83, 66, 15, 59, 33,
                       113, 126, 22, 88, -14, -50, 57, 104, -115, -44, -81, 52, 9, -101, 11, -22,
                       23, -123, 86, -31, -99, 12, -120, -79, 51, -34, -120, 16, 23, 124, -59, -98,
                       -49, 101, 96, 57, 36, -13, -47, 25, 33, 115, -102, 123, 22, -41, -115, -90,
                       46, 53, -80, 108, -126, -50, 61, -106, 53, 74, 4, -41, 74, 70, -95, -24,
                       -114, 111, -57, 38, 122, -128, 57, 3, 125, 89, 11, -82, 38, 45, -31, -5,
                       -102, 79, -112, -95, -113, -46, 21, -104, 7, 102, 48, -128, -67, -97, -23,
                       14, 121, -117, -27, 53, -18, -59, 70, -25, -8, 4, 109, 21, -111, 54, -98, 12,
                       45, -37, -103, -20, 106, 47, 120, 16, 82, 101, -80, 116, 63, -12, -112, 4,
                       -87, -24, -86, -26, -90, -61, -68, -21, 112, -25, 51, 28, -28, 103, -21, -9,
                       64, 72, -6, -99, 41, 45, 13, -56, 94, 123, 0, -50, -87, 109, -63, 58, -86,
                       -102, -99, 47, 26, -37, 113, 123, -104, 112, 62, -121, -6, 20, -11, -3, -114,
                       -80, -118, 80, -25, 105, -68, -4, 83, -47, 56, 125, -80, -52, 122, -7, -114,
                       46, 114, -87, 19, 26, 36, -102, -12, -101, 99, 73, -51, -121, -56, -119, 93,
                       14, -3, -120, -70};
  }

}