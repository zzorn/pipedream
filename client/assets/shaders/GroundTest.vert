
//the global uniform World view projection matrix
//(more on global uniforms below)
uniform mat4 g_WorldViewProjectionMatrix;

//The attribute inPosition is the Object space position of the vertex
attribute vec3 inPosition;
attribute vec4 inTexCoord2;
attribute vec2 inTexCoord;

varying vec2 texCoord;

varying vec4 textureStrengths0;


void main(){
    //Transformation of the object space coordinate to projection space
    //coordinates.
    //- gl_Position is the standard GLSL variable holding projection space
    //position. It must be filled in the vertex shader
    //- To convert position we multiply the worldViewProjectionMatrix by
    //by the position vector.
    //The multiplication must be done in this order.
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);

    texCoord = inTexCoord;

    // Reusing JME attributes to pass own ones, as JME3 doesn't support user defined attributes... -_-;
    textureStrengths0 = inTexCoord2;
}