package org.skycastle.client;

import com.jme3.terrain.noise.ShaderUtils;
import com.jme3.terrain.noise.modulator.NoiseModulator;

public class ClampingNoiseModulator implements NoiseModulator {
    @Override
    public float value(float... in) {
        return ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1);
    }
}
