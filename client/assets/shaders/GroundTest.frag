
varying vec2 texCoord;

uniform sampler2D m_Ecotope1Map;
uniform sampler2D m_Ecotope2Map;
uniform sampler2D m_Ecotope3Map;


void main(){
    //returning the color of the pixel (here solid blue)
    //- gl_FragColor is the standard GLSL variable that holds the pixel
    //color. It must be filled in the Fragment Shader.

    //gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);

    vec4 color1 = texture2D(m_Ecotope1Map, texCoord);
    vec4 color2 = texture2D(m_Ecotope2Map, texCoord);
    vec4 color3 = texture2D(m_Ecotope3Map, texCoord);

    float eco1str =     mod(texCoord.x, 10) / 10;
    float eco2str = 1 - mod(texCoord.x, 10) / 10;
    float eco3str =     mod(texCoord.y, 10) / 10;

    float ecotope1Str = color1.a * eco1str;
    float ecotope2Str = color2.a * eco2str;
    float ecotope3Str = color3.a * eco3str;

    float h1 = ecotope1Str * ecotope1Str;
    float h2 = ecotope2Str * ecotope2Str;
    float h3 = ecotope3Str * ecotope3Str;

    // Increase contrast
    h1 *= h1 * h1;
    h2 *= h2 * h2;
    h3 *= h3 * h3;

    float hsum = h1 + h2 + h3;

    if (hsum > 0) {
    	gl_FragColor =  (color1 * h1 + color2 * h2 + color3 * h3) / hsum;
    }
    else {
	    gl_FragColor =  color1;
    }

/*
    if (h1 > h2) {
    	gl_FragColor =  color1;
    }
    else {
    	gl_FragColor =  color2;
    }
*/



}