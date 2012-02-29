
// The global uniform World view projection matrix
uniform mat4 g_WorldViewProjectionMatrix;

// Pre-calculated. Could maybe be calculated in vertex shader?  But no point in interpolating them.
uniform float haze0_densityToCamera;
uniform float haze0_densityToSpace;

uniform float haze0_amount; // Amount of the haze compared to air (pressure at sea level), 1.0 = same amount as air.
uniform float haze0_weight; // Weight of the particles in the haze, relative to air (1.0 = weight of air, 2.0 = twice as heavy, the haze will be more compressed).
uniform float haze0_glowStrength; // Amount of forward scattering of sunlight
uniform float haze0_glowSize; // Forward scattering spread
uniform vec3 haze0_glowColor; // Forward scattering color components
uniform float haze0_scatterAmount; // Amount of scattering to sides (uniformly).
uniform vec3 haze0_scatterColor; // Color components scattered to sides.  The color components not scattered are the ones that pass through.

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
verticalDensityTraveled = F(1000000) - F(cameraHeight)

We'll need to scale this with the distance from the camera to the point where the ray hits the ground or the upper end of the atmosphere.
densityTraveled = verticalDensityTraveled * rayLength / rayLength.z  // Divide by z, as the verticalDensity traveled has already covered the z distance.

Now we have the total density passed.  To get the color, we'll multiply this with haze strengths and opacities, etc.



*/


uniform float haze0_;


//The attribute inPosition is the Object space position of the vertex
attribute vec3 inPosition;

attribute vec2 inTexCoord; // Used to pass in position on the sky

varying vec3 vertexPos;
varying vec2 texPos;

void main()
{
    vertexPos = inPosition.xyz;
    texPos = inTexCoord.xy;
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}