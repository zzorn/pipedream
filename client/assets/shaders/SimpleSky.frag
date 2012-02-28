
uniform vec3 m_sunDir;

varying vec3 vertexPos;

void main()
{
    vec3 posN = normalize(vertexPos);
    vec3 sunN = normalize(m_sunDir);

    // Alignment of this fragment with the sun
    float sunDir = (dot(posN, sunN) + 1.0) / 2.0;
    float skyHeight = (1.0 - posN.z) * 0.5;

    // Sky bluishness
    float skyBlue = (sunDir + (skyHeight)) * 1.4;
    skyBlue = pow(skyBlue, 1.1);
    vec3 skyColor = 0.4 * skyBlue * vec3(0.15, 0.2, 1.0);

    // Sun halo & highlight
    float sunSpot = pow(sunDir, 2000.0);
    vec3 sunSpotColor = 1.0 * sunSpot * vec3(1.0, 0.9, 0.7);
    float sunGlow = pow(sunDir, 3.0);
    vec3 sunGlowColor = 0.5 * sunGlow * vec3(1.0, 0.9, 0.7);

    // Fog
    float fog = (skyHeight) * 1.0 + 0.5;
    fog = pow(fog, 4.0);
    vec3 fogColor = 0.6 * fog * vec3(1.0, 0.6, 0.3);

    gl_FragColor = vec4(fogColor + skyColor + sunSpotColor + sunGlowColor, 1.0);
}