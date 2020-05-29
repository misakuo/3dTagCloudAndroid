
package com.moxun.tagcloudlib.view.graphics;

public class Point3DF {
  public float x;
  public float y;
  public float z;

  public Point3DF() {}

  public Point3DF(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point3DF(Point3DF p) {
    this.x = p.x;
    this.y = p.y;
    this.z = p.z;
  }

  /**
   * Set the point's x and y coordinates
   */
  public final void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Set the point's x and y coordinates to the coordinates of p
   */
  public final void set(Point3DF p) {
    this.x = p.x;
    this.y = p.y;
    this.z = p.z;
  }

  public final void negate() {
    x = -x;
    y = -y;
    z = -z;
  }

  public final void offset(float dx, float dy, float dz) {
    x += dx;
    y += dy;
    z += dz;
  }

  /**
   * Returns true if the point's coordinates equal (x,y)
   */
  public final boolean equals(float x, float y, float z) {
    return this.x == x && this.y == y && this.z == z;
  }

  @Override
  public String toString() {
    return "Point3DF(" + x + ", " + y + ", " + z + ")";
  }
}
