
// The global uniform World view projection matrix
uniform mat4 g_WorldViewProjectionMatrix;


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