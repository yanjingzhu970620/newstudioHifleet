package com.hifleet.map;



public class BingMapTileAlgorithms {
	private final static double EarthRadius = 6378137;
    private final static double MinLatitude = -85.05112878;
    private final static double MaxLatitude = 85.05112878;
    private final static double MinLongitude = -180;
    private final static double MaxLongitude = 180;
    
  /// <summary>
    /// Clips a number to the specified minimum and maximum values.
    /// </summary>
    /// <param name="n">The number to clip.</param>
    /// <param name="minValue">Minimum allowable value.</param>
    /// <param name="maxValue">Maximum allowable value.</param>
    /// <returns>The clipped value.</returns>

    private static double Clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    
  /// <summary>
    /// Determines the map width and height (in pixels) at a specified level
    /// of detail.
    /// </summary>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>The map width and height in pixels.</returns>

    public static int MapSize(int levelOfDetail)
    {
        return  256 << levelOfDetail;
    }
    
    /// <summary>
    /// Determines the ground resolution (in meters per pixel) at a specified
    /// latitude and level of detail.
    /// </summary>
    /// <param name="latitude">Latitude (in degrees) at which to measure the
    /// ground resolution.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>The ground resolution, in meters per pixel.</returns>
    public static double GroundResolution(double latitude, int levelOfDetail)
    {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / MapSize(levelOfDetail);
    }



    /// <summary>
    /// Determines the map scale at a specified latitude, level of detail,
    /// and screen resolution.
    /// </summary>
    /// <param name="latitude">Latitude (in degrees) at which to measure the
    /// map scale.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="screenDpi">Resolution of the screen, in dots per inch.</param>
    /// <returns>The map scale, expressed as the denominator N of the ratio 1 : N.</returns>
    public static double MapScale(double latitude, int levelOfDetail, int screenDpi)
    {
        return GroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }



    /// <summary>
    /// Converts a point from latitude/longitude WGS-84 coordinates (in degrees)
    /// into pixel XY coordinates at a specified level of detail.
    /// </summary>
    /// <param name="latitude">Latitude of the point, in degrees.</param>
    /// <param name="longitude">Longitude of the point, in degrees.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>
    /// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>
    public static PixelXY LatLongToPixelXY(double latitude, double longitude, int levelOfDetail)
    {
    	PixelXY pixelxy = new PixelXY();
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        longitude = Clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360; 
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        int mapSize = MapSize(levelOfDetail);
        int pixelX = (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
        pixelxy.setPx(pixelX);
        pixelxy.setPy(pixelY);
        return pixelxy;
    }



    /// <summary>
    /// Converts a pixel from pixel XY coordinates at a specified level of detail
    /// into latitude/longitude WGS-84 coordinates (in degrees).
    /// </summary>
    /// <param name="pixelX">X coordinate of the point, in pixels.</param>
    /// <param name="pixelY">Y coordinates of the point, in pixels.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="latitude">Output parameter receiving the latitude in degrees.</param>
    /// <param name="longitude">Output parameter receiving the longitude in degrees.</param>
    public static LatLon PixelXYToLatLon(int pixelX, int pixelY, int levelOfDetail)
    {
    	LatLon lonlat;// = new LatLon();
        double mapSize = MapSize(levelOfDetail);
        double x = (Clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (Clip(pixelY, 0, mapSize - 1) / mapSize);

       double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double longitude = 360 * x;
        lonlat = new LatLon(latitude,longitude);
        return lonlat;
    }



    /// <summary>
    /// Converts pixel XY coordinates into tile XY coordinates of the tile containing
    /// the specified pixel.
    /// </summary>
    /// <param name="pixelX">Pixel X coordinate.</param>
    /// <param name="pixelY">Pixel Y coordinate.</param>
    /// <param name="tileX">Output parameter receiving the tile X coordinate.</param>
    /// <param name="tileY">Output parameter receiving the tile Y coordinate.</param>
    public static TileXY PixelXYToTileXY(int pixelX, int pixelY)
    {
       int tileX = pixelX / 256;
       int tileY = pixelY / 256;
       return new TileXY(tileX,tileY);
    }



    /// <summary>
    /// Converts tile XY coordinates into pixel XY coordinates of the upper-left pixel
    /// of the specified tile.
    /// </summary>
    /// <param name="tileX">Tile X coordinate.</param>
    /// <param name="tileY">Tile Y coordinate.</param>
    /// <param name="pixelX">Output parameter receiving the pixel X coordinate.</param>
    /// <param name="pixelY">Output parameter receiving the pixel Y coordinate.</param>
    public static PixelXY TileXYToPixelXY(int tileX, int tileY)
    {
        int pixelX = tileX * 256;
        int pixelY = tileY * 256;
        return new PixelXY(pixelX,pixelY);
    }



    /// <summary>
    /// Converts tile XY coordinates into a QuadKey at a specified level of detail.
    /// </summary>
    /// <param name="tileX">Tile X coordinate.</param>
    /// <param name="tileY">Tile Y coordinate.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>A string containing the QuadKey.</returns>
    public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
    {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }
}
