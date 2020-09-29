package algs.week2.seam;

import edu.princeton.cs.algs4.Picture;

/**
 * SeamCarver. Seam-carving is a content-aware image resizing technique where
 * the image is reduced in size by one pixel of height (or width) at a time.
 */
public class SeamCarver {

  // make original picture immuatable
  private Picture img;
  private int imgW;
  private int imgH;
  private double[][] energy;

  // tranposed or not
  private boolean isEnergyTranposed = false;

  /**
   * create a seam carver object based on the given picture.
   */
  public SeamCarver(Picture picture) {

    // null argument check
    checkNull(picture);

    // initialize image
    img = new Picture(picture);

    // initialize energy array
    imgW = img.width();
    imgH = img.height();
    energy = new double[imgW][imgH];
    energyFill();
  }

  /**
   * calculate energy array.
   */
  private void energyFill() {
    for (int i = 0; i < imgW; i++) {
      for (int j = 0; j < imgH; j++) {
        energy[i][j] = energy(i, j);
      }
    }
  }

  /**
   * current picture.
   */
  public Picture picture() {
    return new Picture(img);
  }

  /**
   * width of current picture.
   */
  public int width() {
    return imgW;
  }

  /**
   * height of current picture.
   */
  public int height() {
    return imgH;
  }

  /**
   * return energy of pixel at column x and row y.
   */
  public double energy(int x, int y) {

    // bound check
    if (x < 0 || x > imgW - 1) {
      throw new IllegalArgumentException("idx x out of range");
    }
    if (y < 0 || y > imgH - 1) {
      throw new IllegalArgumentException("idx y out of range");
    }

    // border
    if (x == 0 || x == imgW - 1 || y == 0 || y == imgH - 1) {
      return 1000.0;
    }

    // interior
    int colorUpper = img.getRGB(x, y - 1);
    int colorBelow = img.getRGB(x, y + 1);
    int colorLeft = img.getRGB(x - 1, y);
    int colorRight = img.getRGB(x + 1, y);
    double delta = 0.0;

    // red
    delta += Math.pow(((colorLeft >> 16) & 0xFF) - ((colorRight >> 16) & 0xFF), 2);
    delta += Math.pow(((colorUpper >> 16) & 0xFF) - ((colorBelow >> 16) & 0xFF), 2);

    // green
    delta += Math.pow(((colorLeft >> 8) & 0xFF) - ((colorRight >> 8) & 0xFF), 2);
    delta += Math.pow(((colorUpper >> 8) & 0xFF) - ((colorBelow >> 8) & 0xFF), 2);

    // blue
    delta += Math.pow((colorLeft & 0xFF) - (colorRight & 0xFF), 2);
    delta += Math.pow((colorUpper & 0xFF) - (colorBelow & 0xFF), 2);

    // return
    delta = Math.sqrt(delta);
    return delta;
  }

  /**
   * transpose energy array.
   */
  private double[][] transposeEnergy(int width, int height) {

    // transpose energy array
    double[][] energyTpd = new double[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        energyTpd[i][j] = energy[j][i];
      }
    }

    // return transposed energy
    return energyTpd;
  }

  /**
   * sequence of indices for horizontal seam.
   */
  public int[] findHorizontalSeam() {

    // transpose(update) energy array if necessary
    if (isEnergyTranposed) {
      energy = transposeEnergy(imgH, imgW);
      isEnergyTranposed = false;
    }

    // find seam
    return findSeam(imgW, imgH);
  }

  /**
   * sequence of indices for vertical seam.
   */
  public int[] findVerticalSeam() {

    // transpose(update) energy array if necessary
    if (!isEnergyTranposed) {
      energy = transposeEnergy(imgW, imgH);
      isEnergyTranposed = true;
    }

    // find seam
    return findSeam(imgH, imgW);
  }

  private int[] findSeam(int width, int height) {

    // initialization
    double[][] distTo = new double[width][height];
    int[][] prevTo = new int[width][height];

    // initial distTo
    for (int i = 1; i < width; i++) {
      for (int j = 0; j < height; j++) {
        distTo[i][j] = Double.POSITIVE_INFINITY;
      }
    }

    // calcu dist directly connected to source
    for (int j = 0; j < height; j++) {
      distTo[0][j] = energy[0][j];
    }

    // relaxation in topological order
    for (int i = 0; i < width - 1; i++) {
      for (int j = 0; j < height; j++) {
        int x = i + 1;
        int y = j - 1;

        // dist to current vertex
        double distV = distTo[i][j];

        // store relaxed dist
        double distRelax = 0.0;

        // below-left
        if (y >= 0) {
          distRelax = distV + energy[x][y];
          if (distTo[x][y] > distRelax) {
            distTo[x][y] = distRelax;
            prevTo[x][y] = j;
          }
        }

        // below-right
        y += 2;
        if (y < height) {
          distRelax = distV + energy[x][y];
          if (distTo[x][y] > distRelax) {
            distTo[x][y] = distRelax;
            prevTo[x][y] = j;
          }
        }

        // below-middle
        y--;
        distRelax = distV + energy[x][y];
        if (distTo[x][y] > distRelax) {
          distTo[x][y] = distRelax;
          prevTo[x][y] = j;
        }
      }
    }

    // chose dest vertex
    int dest = 0;

    // set search colum
    int endCol = width - 1;

    // find shortest
    double shortest = Double.POSITIVE_INFINITY;
    for (int j = 0; j < height; j++) {
      if (distTo[endCol][j] < shortest) {
        shortest = distTo[endCol][j];
        dest = j;
      }
    }

    // get path
    int[] path = new int[width];
    for (int i = endCol; i >= 0; i--) {
      path[i] = dest;
      dest = prevTo[i][dest];
    }
    return path;
  }

  /**
   * remove horizontal seam from current picture.
   */
  public void removeHorizontalSeam(int[] seam) {

    // check height
    if (imgH <= 1) {
      throw new IllegalArgumentException("height <= 1");
    }

    // null argument check
    checkNull(seam);

    // check seam
    checkSeamH(seam, imgW, imgH);

    // remove seam and update energy array
    updateHorizontal(seam, imgW, imgH);
  }

  /**
   * remove vertical seam from current picture.
   */
  public void removeVerticalSeam(int[] seam) {

    // check width
    if (imgW <= 1) {
      throw new IllegalArgumentException("width <= 1");
    }

    // null argument check
    checkNull(seam);

    // check seam
    checkSeamV(seam, imgW, imgH);

    // remove seam and update energy array
    updateVertical(seam, imgH, imgW);
  }

  /**
   * remove seam and update energy array. transpose to use left-right order. the
   * width used here is original height. not reuse updateHorizontal to avoid
   * transposing picture.
   */
  private void updateVertical(int[] seam, int width, int height) {

    // transpose(update) energy array if necessary
    if (!isEnergyTranposed) {
      energy = transposeEnergy(height, width);
      isEnergyTranposed = true;
    }

    // initialize new picture and energy array
    Picture picUpdated = new Picture(height - 1, width);
    // double[][] energyUpdated = new double[width][height - 1];

    // set value
    for (int col = 0; col < width; col++) {
      int split = seam[col];

      // left part
      for (int row = 0; row < split; row++) {

        // row is col in picture and vice versa
        picUpdated.setRGB(row, col, img.getRGB(row, col));
      }

      // right part
      for (int row = split + 1; row < height; row++) {
        picUpdated.setRGB(row - 1, col, img.getRGB(row, col));
      }

      // update energy parts
      // System.arraycopy(energy[col], 0, energyUpdated[col], 0, split);
      // System.arraycopy(energy[col], split + 1, energyUpdated[col], split, height - split - 1);
      System.arraycopy(energy[col], split + 1, energy[col], split, height - split - 1);
    }

    // update picture
    img = picUpdated;
    imgW--;

    // update energy on the seam
    for (int col = 0; col < width; col++) {
      int split = seam[col];

      // update, need to check corner case
      if (split > 0) {
        // energyUpdated[col][split - 1] = energy(split - 1, col);
        energy[col][split - 1] = energy(split - 1, col);
      }

      if (split < height - 1) {
        // energyUpdated[col][split] = energy(split, col);
        energy[col][split] = energy(split, col);
      }
    }

    // update energy
    // energy = energyUpdated;
  }

  /**
   * remove seam and update energy array. use left-right order.
   */
  private void updateHorizontal(int[] seam, int width, int height) {

    // transpose(update) energy array if necessary
    if (isEnergyTranposed) {
      energy = transposeEnergy(height, width);
      isEnergyTranposed = false;
    }

    // initialize new picture and energy array
    Picture picUpdated = new Picture(width, height - 1);
    // double[][] energyUpdated = new double[width][height - 1];

    // set value
    for (int col = 0; col < width; col++) {
      int split = seam[col];

      // upper part
      for (int row = 0; row < split; row++) {
        picUpdated.setRGB(col, row, img.getRGB(col, row));
      }

      // below part
      for (int row = split + 1; row < height; row++) {
        picUpdated.setRGB(col, row - 1, img.getRGB(col, row));
      }

      // update energy parts
      // System.arraycopy(energy[col], 0, energyUpdated[col], 0, split);
      // System.arraycopy(energy[col], split + 1, energyUpdated[col], split, height - split - 1);
      System.arraycopy(energy[col], split + 1, energy[col], split, height - split - 1);
    }

    // update picture
    img = picUpdated;
    imgH--;

    // update energy on the seam
    for (int col = 0; col < width; col++) {
      int split = seam[col];

      // update, need to check corner case
      if (split > 0) {
        // energyUpdated[col][split - 1] = energy(col, split - 1);
        energy[col][split - 1] = energy(col, split - 1);
      }

      if (split < height - 1) {
        // energyUpdated[col][split] = energy(col, split);
        energy[col][split] = energy(col, split);
      }
    }

    // update energy
    // energy = energyUpdated;
  }

  /**
   * null argument check.
   */
  private <T> void checkNull(T arg) {
    if (arg == null) {
      throw new IllegalArgumentException("null argument");
    }
  }

  /**
   * vertical seam check.
   */
  private void checkSeamV(int[] seam, int width, int height) {

    // length check
    if (seam.length != height) {
      throw new IllegalArgumentException("wrong vertical length");
    }

    // idx check
    int prev = seam[0];
    for (int e : seam) {

      // consistency check
      if (Math.abs(e - prev) > 1) {
        throw new IllegalArgumentException("adjs differ by more than 1");
      }

      // idx range check
      if (e < 0 || e > width - 1) {
        throw new IllegalArgumentException("wrong vertical length");
      }

      // update previous element
      prev = e;
    }
  }

  /**
   * horizontal seam check.
   */
  private void checkSeamH(int[] seam, int width, int height) {

    // length check
    if (seam.length != width) {
      throw new IllegalArgumentException("wrong horizontal length");
    }

    // idx check
    int prev = seam[0];
    for (int e : seam) {

      // consistency check
      if (Math.abs(e - prev) > 1) {
        throw new IllegalArgumentException("adjs differ by more than 1");
      }

      // idx range check
      if (e < 0 || e > height - 1) {
        throw new IllegalArgumentException("wrong horizontal length");
      }

      // update previous element
      prev = e;
    }
  }
}