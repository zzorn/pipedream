
varying vec2 texCoord;

// Strengths of the first four textures
varying vec4 ecotopeThickness0;


uniform sampler2D m_Ecotope0Map;
uniform sampler2D m_Ecotope1Map;
uniform sampler2D m_Ecotope2Map;
uniform sampler2D m_Ecotope3Map;

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


const float maxSeeThroughDepth = 1.0;
const float ecotopeSurfaceScale = 1.0;

void main(){

    // TODO: We want to overlap the textures on top of each other, using some translucency (for each?).
    // They are passed in in top to bottom order, so ecotope0 is on top of ecotope1, and so on.
    // The strengths are the ecotope thicknesses, in meters.

    vec4 color0 = texture2D(m_Ecotope0Map, texCoord);
    vec4 color1 = texture2D(m_Ecotope1Map, texCoord);
    vec4 color2 = texture2D(m_Ecotope2Map, texCoord);
    vec4 color3 = texture2D(m_Ecotope3Map, texCoord);

    // Calculate visibility
    float landHeight = 0.0;
    float h3 = landHeight + ecotopeThickness0.w; landHeight += h3;
    float h2 = landHeight + ecotopeThickness0.z; landHeight += h2;
    float h1 = landHeight + ecotopeThickness0.y; landHeight += h1;
    float h0 = landHeight + ecotopeThickness0.x; landHeight += h0;

    // Surfacematerial
    h0 += color0.a * ecotopeSurfaceScale;
    h1 += color1.a * ecotopeSurfaceScale;
    h2 += color2.a * ecotopeSurfaceScale;
    h3 += color3.a * ecotopeSurfaceScale;

    // Increase contrast
    h0 *= h0 * h0;
    h0 *= h0 * h0;

    h1 *= h1 * h1;
    h1 *= h1 * h1;

    h2 *= h2 * h2;
    h2 *= h2 * h2;

    h3 *= h3 * h3;
    h3 *= h3 * h3;

    float hsum = h0 + h1 + h2 + h3;

    if (hsum > 0.0) {
    	gl_FragColor =  (color0 * h0 + color1 * h1 + color2 * h2 + color3 * h3) / hsum;
    }
    else {
	    gl_FragColor =  color0;
    }

/* debug
    gl_FragColor =  vec4(
                       clamp(h0, 0.0, 1.0),
                       clamp(h1, 0.0, 1.0),
                       clamp(h2, 0.0, 1.0),
                       1.0);
*/
}