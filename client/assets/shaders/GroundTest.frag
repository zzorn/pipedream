// Texture coordinate, actually contains world coordinates
varying vec2 texCoord;

// Thicknesses of the ecotopes
varying vec4 ecotopeThickness0;
//varying vec4 ecotopeThickness1;

// Ecotope textures + depths
uniform sampler2D m_Ecotope0Map;
uniform sampler2D m_Ecotope1Map;
uniform sampler2D m_Ecotope2Map;
uniform sampler2D m_Ecotope3Map;
/*
uniform sampler2D m_Ecotope4Map;
uniform sampler2D m_Ecotope5Map;
uniform sampler2D m_Ecotope6Map;
uniform sampler2D m_Ecotope7Map;
*/


// Atmosphere
uniform vec3 m_sunDir;

varying vec3 vertexPos;

uniform vec3 g_CameraPosition;

uniform vec4 sunLight = vec4(0.9, 0.85, 0.8, 100.0); // Color and intensity

uniform float haze0_amount = 2.0; // Amount of the haze compared to air (pressure at sea level), 1.0 = same amount as air.
uniform float haze0_weight = 4.0; // Weight of the particles in the haze, relative to air (1.0 = weight of air, 2.0 = twice as heavy, the haze will be more compressed).

uniform vec4 haze0_forwardScattering = vec4(0.9, 0.85, 0.8, 10.35); // Forward scattering color components, and strength (in alpha)
uniform float haze0_forwardScatteringSize = 0.01; // Forward scattering spread

uniform vec4 haze0_sideScattering = vec4(0.1, 0.2, 0.95, 0.1); // Amount and color components scattered to sides.  Alpha is the amount of scattering to sides (uniformly).

uniform vec4 haze0_backScattering = vec4(1.0, 1.0, 1.0, 0.0); // Color and amount of back scattering
uniform float haze0_backScatterSize = 1.0; // Back scattering spread




const float contrastPower = 5.0;
const float epsilon = 0.01;




// Constants for air
const float H = 8000.0; // Scale height for air (8km)
const float airPressureAtSeaLevel = 101325.0; // 1 atm
const float gravitationalConstant = 9.807; // g
const float c1 = airPressureAtSeaLevel / (gravitationalConstant * H); // = 1.291488222;
const float invH = 1.0 / H;




void main(){

    // Testures are passed in in top to bottom order, so ecotope0 is on top of ecotope1, and so on.
    // The ecotope thicknesses and surface scales are both given in the same unit (meters).

    vec4 color0 = texture2D(m_Ecotope0Map, texCoord);
    vec4 color1 = texture2D(m_Ecotope1Map, texCoord);
    vec4 color2 = texture2D(m_Ecotope2Map, texCoord);
    vec4 color3 = texture2D(m_Ecotope3Map, texCoord);

    // TODO: Get surface depth scales as material parameters
    float surfaceScale0 = 1.3;
    float surfaceScale1 = 5.0;
    float surfaceScale2 = 10.0;
    float surfaceScale3 = 1.0;

    // Calculate visibility
    float strengthLeft = 1.0;
    float s0 = clamp(ecotopeThickness0.x / surfaceScale0, 0.0, strengthLeft); strengthLeft -= s0;
    float s1 = clamp(ecotopeThickness0.y / surfaceScale1, 0.0, strengthLeft); strengthLeft -= s1;
    float s2 = clamp(ecotopeThickness0.z / surfaceScale2, 0.0, strengthLeft); strengthLeft -= s2;
    float s3 = strengthLeft;

    // Adjust visibility with surface heights
    s0 *= (epsilon + color0.a);
    s1 *= (epsilon + color1.a);
    s2 *= (epsilon + color2.a);
    s3 *= (epsilon + color3.a);

    // Increase contrast
    /* TODO: Is it faster to do pow, or to multiply repeatedly?
    s0 = pow(s0, contrastPower);
    s1 = pow(s1, contrastPower);
    s2 = pow(s2, contrastPower);
    s3 = pow(s3, contrastPower);
    */
    s0 *= s0 * s0;
    s0 *= s0 * s0;

    s1 *= s1 * s1;
    s1 *= s1 * s1;

    s2 *= s2 * s2;
    s2 *= s2 * s2;

    s3 *= s3 * s3;
    s3 *= s3 * s3;

    // Calculate color
    float sum = s0 + s1 + s2 + s3;
    if (sum > 0.0) {
        gl_FragColor =  (color0 * s0 + color1 * s1 + color2 * s2 + color3 * s3) / sum;
    }
    else {
	    gl_FragColor =  color3;
    }



    // Lighting
    vec3 posN = normalize(vertexPos - g_CameraPosition);
    vec3 sunN = normalize(m_sunDir);

    // Alignment of this fragment with the sun
    float sunAlignment = (dot(posN, sunN) + 1.0) / 2.0;
    float distanceFromSun = 1.0 - sunAlignment;

    float densityToGround =
      H *
      haze0_amount *
      c1 *
      (exp( -g_CameraPosition.y * haze0_weight * invH) -
       exp( -vertexPos.y        * haze0_weight * invH));
    float density = abs(densityToGround) / max(abs(posN.y), 0.000001);

    // TODO: What is this factor?
    float visualDensity = density / 100000.0;

    vec3 sunlightWithIntensity = sunLight.rgb * sunLight.a;
    float costTheta = cos(distanceFromSun * 3.1415);
    float sideScatteringGlowScale = (3.0 / 4.0) * (1.0 + costTheta * costTheta);

    vec3 sideScattering =
    sideScatteringGlowScale *
    visualDensity *
    haze0_sideScattering.a *
    haze0_sideScattering.rgb *
    sunlightWithIntensity;

    // Forward scattering

    // Gaussian distribute the glow:
    //float forwardScatteringGlowScale = exp(-distanceFromSun * distanceFromSun / (2.0 * haze0_forwardScatteringSize * haze0_forwardScatteringSize));

    // Exponent glow
    float forwardScatteringGlowScale = 0.0;// pow(sunAlignment, 1.0 / haze0_forwardScatteringSize);

    vec3 forwardScattering =
    visualDensity *
    forwardScatteringGlowScale *
    haze0_forwardScattering.a *
    haze0_forwardScattering.rgb *
    sunlightWithIntensity;

    // Sum together light components
    vec3 hdr = gl_FragColor.rgb + sideScattering + forwardScattering;

    // Simple tone mapping
    vec3 ldr = hdr / (hdr + 1.0);

    // Gamma correct
    ldr.r = pow(ldr.r, 1.0 / 2.2);
    ldr.g = pow(ldr.g, 1.0 / 2.2);
    ldr.b = pow(ldr.b, 1.0 / 2.2);

    gl_FragColor = vec4(ldr, 1.0);








/* debug
    gl_FragColor =  vec4(
                       clamp(h0 / hsum, 0.0, 1.0),
                       clamp(h1 / hsum, 0.0, 1.0),
                       clamp(h2 / hsum, 0.0, 1.0),
                       1.0);
*/
}