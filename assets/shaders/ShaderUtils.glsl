/**
 * A fast approximation for sigmoid function, from http://www.dontveter.com/bpr/activate.html
 * Deviation from real sigmoid is less than 0.02.
 * x should be in range -1 .. 1
 * Returns value from 0 to 1
 */
float sigmoid(float x) {
  if (x >= 1.0) return 1.0;
  else if (x <= -1.0) return 0.0;
  else return 0.5 + x * (1.0 - abs(x) * 0.5);
}

/**
 * Performs sigmoid on x over the specified range.
 * Returns value from 0 to 1, 0 if less than startX, 1 if larger than endX, and a sigmoid between the two in between.
 * startX must not be equal to endX.
 */
float sigmoid(float x, float startX, float endX) {
  if (x <= startX) return 0.0;
  else if (x >= endX) return 1.0;
  else {
    float scaledX = (x - startX) / (endX - startX);
    return 0.5 + scaledX * (1.0 - abs(scaledX) * 0.5);
  }
}


/**
 * A fast approximation for sigmoid function, from http://dinodini.wordpress.com/2010/04/05/normalized-tunable-sigmoid-functions/
 * x should be in range -1 .. 1
 * Returns value from -1 to 1
 * sharpness can be from -Inf to Inf, closer to zero it is sharper, closer to +/- inf it is more straight.
 * at 1 is is close to normal sigmoid.
 */
 //
float sigmoid2(float x, float sharpness) {
  if (x >= 1.0) return 1.0;
  else if (x <= -1.0) return -1.0;
  else {
    if (sharpness < 0.0) sharpness -= 1.0;

    if (x > 0.0) return sharpness * x / (sharpness - x + 1.0);
    else if (x < 0.0) return sharpness * x / (sharpness - abs(x) + 1.0);
    else return 0.0;
  }
}


float scaleClamp(float v, float inStart, float inEnd, float outStart, float outEnd) {
  if (v <= inStart) return outStart;
  else if (v >= inEnd) return outEnd;
  else return outStart + (outEnd - outStart) * (v - inStart) / (inEnd - inStart);
}
