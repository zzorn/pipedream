
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



// Optional parameters:
uniform float haze0_luminesenceAmount; // Self illuminating light
uniform vec3 haze0_luminesenceColor; // Self illuminating light color

// Haze parameters
/*
 - Translucent color and opacity ( * background color)
 - Forward scatter color and opacity ( * background color)
 - Forward scatter profile - radius, focus
 - Scattered color ( * sun color)



H = 8000m for air (scale height http://en.wikipedia.org/wiki/Scale_height )

If some particle is heavier / lighter than air, the H is adjusted as follows:
H' = H / massFactor  (e.g., if something is twice the weight, divide H with two.)

p(0) at sea level = 101325 pascals for air

For some other haze component, e.g. smoke, this is usually (much) smaller, e.g. p_air(0) * 0.01 etc.
Maybe give the value relative to air pressure.

Pressure of haze / gas
p(y) = p(0) * e^(-y/H)

Density of haze / gas:
g = gravitational constant

d(y) = (p(0)/g*H) * e^(-y/H)


Constants used:
H = 8000.0
multFactor = 101325 / (9.807 * H) = 1.291488222
invH = 1.0 / H

Parameters:
amount = 1.0 (for air)  This is the pressure at sea level, compared to the air pressure.
massFactor = 1.0 (for air, use 2 if substance is twice the (molecular) mass of air, etc)

Function for density:
d(z) = amount * multFactor * massFactor * e ^ (-z * massFactor * invH)

Integrate between terrain height and camera height:
F(z) = amount * multFactor * massFactor * e ^ (-z * massFactor * invH) / (-massFactor * invH)
verticalDensityTraveled = F(cameraHeight) - F(terrainHeight)

Opened up it is:
verticalDensityTraveled = - H * amount * multFactor * (e^(-cameraZ * massFactor * invH) - e^(-groundZ * massFactor * invH))

We consider the upper limit of visible atmosphere is to be 1000 km
For rays not hitting the ground, the traveled density is then
verticalDensityTraveled = F(inf) - F(cameraHeight) = H * amount * multFactor * e^(-cameraZ * massFactor * invH)

We'll need to scale this with the distance from the camera to the point where the ray hits the ground or the upper end of the atmosphere.
densityTraveled = verticalDensityTraveled * rayLength / rayLength.z  // Divide by z, as the verticalDensity traveled has already covered the z distance.

Now we have the total density passed.  To get the color, we'll multiply this with haze strengths and opacities, etc.

See e.g. http://http.developer.nvidia.com/GPUGems2/gpugems2_chapter16.html


*/

// Constants for air
const float H = 8000.0; // Scale height for air (8km)
const float airPressureAtSeaLevel = 101325.0; // 1 atm
const float gravitationalConstant = 9.807; // g
const float c1 = airPressureAtSeaLevel / (gravitationalConstant * H); // = 1.291488222;
const float invH = 1.0 / H;



void main()
{
    vec3 posN = normalize(vertexPos);
    vec3 sunN = normalize(m_sunDir);

    // Alignment of this fragment with the sun
    float sunAlignment = (dot(posN, sunN) + 1.0) / 2.0;
    float distanceFromSun = 1.0 - sunAlignment;

    float densityUp = H * haze0_amount * c1 * exp( - g_CameraPosition.y * haze0_weight * invH);
    float density = densityUp / max(posN.z, 0.000001);

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
    float forwardScatteringGlowScale = 0.0;//pow(sunAlignment, 1.0 / haze0_forwardScatteringSize);

    vec3 forwardScattering =
      visualDensity *
      forwardScatteringGlowScale *
      haze0_forwardScattering.a *
      haze0_forwardScattering.rgb *
      sunlightWithIntensity;

    // Sum together light components
    vec3 hdr = sideScattering + forwardScattering;

    // Simple tone mapping
    vec3 ldr = hdr / (hdr + 1.0);

    // Gamma correct
    ldr.r = pow(ldr.r, 1.0 / 2.2);
    ldr.g = pow(ldr.g, 1.0 / 2.2);
    ldr.b = pow(ldr.b, 1.0 / 2.2);

    gl_FragColor = vec4(ldr, 1.0);




/* ad-hoc system:
    // Sky bluishness
    float skyBlue = (sunAlignment + (skyHeight)) * 1.4;
    skyBlue = pow(skyBlue, 1.1);
    vec3 skyColor = 0.4 * skyBlue * vec3(0.15, 0.2, 1.0);

    // Sun halo & highlight
    float sunSpot = pow(sunAlignment, 2000.0);
    vec3 sunSpotColor = 1.0 * sunSpot * vec3(1.0, 0.9, 0.7);
    float sunGlow = pow(sunAlignment, 3.0);
    vec3 sunGlowColor = 0.5 * sunGlow * vec3(1.0, 0.9, 0.7);

    // Fog
    float fog = (skyHeight) * 1.0 + 0.5;
    fog = pow(fog, 4.0);
    vec3 fogColor = 0.6 * fog * vec3(1.0, 0.6, 0.3);

    gl_FragColor = vec4(fogColor + skyColor + sunSpotColor + sunGlowColor, 1.0);
*/
}